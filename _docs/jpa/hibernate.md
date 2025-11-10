---
title: 1. JPA (Hibernate)
category: 08. JPA (Hibernate)
exclude: true
order: 1
---
![Hibernate logo]({{ site.baseurl }}/images/hibernate_logo.png)
<h2>Contents</h2>
* toc
{:toc}

## Introduction
We have our multi-container application. We want now to connect the containerized database to our REST service.  
In the previous lesson, we mentioned something about JPA and Hibernate. Let's delve into it.

## JPA and ORM
JPA (Java Persistence API) is an API specification that defines standard techniques and guidelines for Object Relational Mapping (ORM) implementations and database operations.  
ORM is a programming technique that permits easy mapping between entities of an application (for example, a Java Object) and an entry in a database. To give some context, Hibernate (we will talk about it in a moment), is an ORM tool.
We want to stress the fact that JPA is not an implementation. The implementation stays inside the ORM tools that adhere to the JPA specification. 

## Hibernate
Hibernate is an implementation of JPA, thus an ORM tool. Hibernate implements the guidelines defined by JPA. The basic idea of JPA is that one could switch the used ORM quickly without touching too much of the already existing code. We will see now an example.
### Project dependencies
To work with JPA and Hibernate, we need to inject some dependencies inside our StudentsApp. To do so, edit the build.gradle file of the project and add all the needed dependencies:
{% highlight kotlin %}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-core:6.2.6.Final")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
{% endhighlight %}
As you can see, we also have a MySQL connector. This is required because Hibernate permits to connect to different DBMS (e.g., Postgres, MySQL, etc.). Every DBMS has its connector, that contains all the drivers needed for the application to communicate and interact with a specific DBMS server.
Sync the gradle file and build the project (maybe you will find some problems in the build phase due to failed internal tests, we don't focus on it and we can skip the tests by the command **./gradlew build -x test**).  
Then, we set all the properties needed by the Spring framework to deal with Hibernate and JPA:
## Application Properties
{% highlight bash %}
spring.devtools.restart.enabled=false
server.port=8888

spring.datasource.url=jdbc:mysql://localhost:3306/studentsapp?createDatabaseIfNotExist=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.datasource.username=root
spring.datasource.password=secret_password
{% endhighlight %}
Look at the properties: we are setting a driver and a dialect. The driver handles the communication between a particular instance of a DBMS system while the dialect permits the generation of the underlying queries optimized for a particular relational database (in our case, MySQL).  
JPA provides some annotations that permit to map Java classes (and their attributes) to database tables (and their fields). It is very straightforward: let's add some annotations to our Student model.

## Models
{% highlight java %}
package com.example.studentsapp.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;
@Entity // Entity means that this class must be threatened as a relational entity.
@Table( name = "Students" ) // This is used by the ORM to link this Entity in a DBMS Table.
public class Student extends RepresentationModel<Student> {
    @Column //This tells ORM to map this attribute (name) to a table field with the same name. You can specify the name of the field inside the database by using @Column(name="db_column_name")
    private String name;
    @Column
    private String surname;
    @Id // Id means that this field is the Primary Key of the table.
    @Column
    private String id;

    public Student(String name, String surname, String id) {
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    public Student() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String toString() {
        return "[ID: " + id + ", Name: " + name +", Surname: " + surname + "]";
    }
}
{% endhighlight %}
Here we removed the links. The explanation of it is that we want to separate application logic from the database model. We can then extend this class to handle attributes specific to the application:
{% highlight java %}
package com.example.studentsapp.models;

import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class AppStudent extends Student {

    public AppStudent(Student s) {
        this.setName(s.getName());
        this.setSurname(s.getSurname());
        this.setId(s.getId());
        this.add(linkTo(methodOn(com.example.studentsapp.controllers.Student.class).getStudents(s.getId())).withSelfRel());
    }

}
{% endhighlight %}
Another reason leads to this separation: JPA creates objects by using reflections and not by calling our constructor (the links will not be added by default). We will see in a moment how to use the AppStudent class.  
## Repository
These are the changes needed for our Repository to communicate with the database using Hibernate. The code is commented to have a better understanding of how it works.

{% highlight java %}
package com.example.studentsapp.repositories;

import com.example.studentsapp.errors.DuplicatedEntryError;
import com.example.studentsapp.errors.UnknownError;
import com.example.studentsapp.models.Student;
import jakarta.persistence.EntityManager;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class StudentRepository {

    @Autowired
    private EntityManager entityManager; //EntityManager permits to interact with the database. It is Autowired: Spring creates and injects this object for us.
    public StudentRepository() {
    }

    public void create(Student student) throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            // All operations that change the entries in a database must be performed inside a Transaction.
            // Suppose that you must do multiple insertions atomically for an application purpose. If one of these insertions fails, you don't want the other to be committed.
            // Example: you are creating a REST API endpoint that permits to register Users. Every user can have an Address.
            // During registration, if a user provides also the address in the request body, you want to add that Address to the database. Addresses and Users stay in different tables.
            // So, your application does two insertions: first, it will insert the Users, then it will insert the Address.
            // But if there are some problems inserting the Address, you want to roll back the insertion of the User.
            Transaction t = currentSession.beginTransaction();
            currentSession.persist(student);
            t.commit();
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t != null) {
                // catch the error
                if (t instanceof ConstraintViolationException) {
                    ConstraintViolationException exc = (ConstraintViolationException) t;
                    // get the SQL Exception error code
                    //Here you have a list of all the possible MySQL error codes: https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html
                    //For now, we catch only ERROR CODE 1062 (Duplicated Entry), and we consider all the other exceptions as unknown errors.
                    if (exc.getSQLException().getErrorCode() == 1062) {
                        throw new DuplicatedEntryError();
                    }
                    //Here you can handle other exceptions
                }
            }
            throw new UnknownError();
        }
    }

    public Collection<Student> getAll() throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            //To get all the Students, we use a different approach: we build a query.
            // CriteriaBuilder class permits the creation of custom and complex queries on a table.
            //For example, here we are creating a query like "SELECT * FROM students"
            CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
            CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> root = criteriaQuery.from(Student.class);
            criteriaQuery.select(root);
            //Then we execute the query and we get the result (a List of Students).
            Query query = currentSession.createQuery(criteriaQuery);
            List<Student> s = query.getResultList();
            return s;
        } catch(Exception e) {
            // throw an UnknownError
            // possible handle and log the error.
            //The idea is to mask the internal error such that API users will get a 500 error (it doesn't need to know that something with the database is not working).
            //Think about building the APIs from the final user perspective: internal details and implementations should not be things that concern the user.
            // Users should not care if the problem regards the connection to the database or a software bug. This is an internal problem.
            // However, it is important to log the error such that developers can understand what's going on easily and handle errors in time.
            throw new UnknownError();
        }
    }
    public Student fetch(String id) throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            Student s = currentSession.find(Student.class, id);
            return s;
        } catch (Exception ignored) {
        }
        throw new UnknownError();
    }


    public Student delete(String id) {
        // TODO as an exercise
        return null;
    }
}
{% endhighlight %}
We create also the custom error classes:

{% highlight java %}
package com.example.studentsapp.errors;

public class DuplicatedEntryError extends Exception {
    public DuplicatedEntryError() {

    }
}
{% endhighlight %}

{% highlight java %}
package com.example.studentsapp.errors;

public class UnknownError extends Exception {
    public UnknownError() {}
}
{% endhighlight %}
## Service
Then, it is time to edit the Service: we change the class name in AppStudentService. This is because this class defines the Service used by our application.   
On big projects, we could have multiple Services that access the same Repository, but with different business logic and different returning value. For example, here we wrap the Student objects returned by the StudentRepository in AppStudent.

{% highlight java %}
package com.example.studentsapp.services;

import com.example.studentsapp.models.AppStudent;
import com.example.studentsapp.models.Student;
import com.example.studentsapp.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AppStudentService {
    @Autowired
    StudentRepository studentRepository;


    public List<AppStudent> findAll() throws Exception {
        List<AppStudent> appStudents = new ArrayList<>();
        List<Student> studs = new ArrayList<>(studentRepository.getAll());
        // wrap Students in a List of AppStudent
        for (Student s : studs) {
            appStudents.add(new AppStudent(s));
        }
        return appStudents;
    }

    public AppStudent fetch(String id) throws Exception {
        // wrap Student in AppStudent
        return new AppStudent(studentRepository.fetch(id));
    }

    public void add(String name, String surname, String id) throws Exception {
        studentRepository.create(new Student(name, surname, id));
    }

    public void add(Student student) throws Exception {
        studentRepository.create(student);
    }

    public void delete(String id) {
        studentRepository.delete(id);
    }
}
{% endhighlight %}
## Controller
Fix the Controller:
{% highlight java %}
package com.example.studentsapp.controllers;

import com.example.studentsapp.errors.DuplicatedEntryError;
import com.example.studentsapp.models.AppStudent;
import com.example.studentsapp.models.ErrorResponse;
import com.example.studentsapp.services.AppStudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class Student {
    @Autowired
    private AppStudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        try {
            return ResponseEntity.ok(studentService.findAll());

        } catch (Exception ignored) {
        }
        ErrorResponse responseJson = new ErrorResponse("Internal error");
        return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @PostMapping(value = "/students")
    public ResponseEntity<?> addStudent(HttpServletRequest request, @RequestBody com.example.studentsapp.models.Student student) throws URISyntaxException {
        try {
            studentService.add(student);
            return ResponseEntity.created(new URI(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + request.getRequestURI() + "/" + student.getId())).build();

        } catch (Exception e) {
            if (e instanceof DuplicatedEntryError) {
                ErrorResponse responseJson = new ErrorResponse("Student with this id already exists");
                return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.CONFLICT);
            }
        }
        ErrorResponse responseJson = new ErrorResponse("Internal error");
        return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/students/{id}")
    public ResponseEntity<AppStudent> getStudents(@PathVariable String id) {
        try {
            AppStudent s = studentService.fetch(id);
            if (s == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(s);

        } catch (Exception ignored) {}

        return ResponseEntity.notFound().build();
    }
}
{% endhighlight %}

## Edits on compose.yaml
{% highlight java %}
services:
  web:
    build: .
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - students_db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://students_db:3306/studentsapp?createDatabaseIfNotExist=true
  students_db:
    volumes:
      - students-db:/var/lib/mysql
    image: "mysql:5.7"
    restart: always
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=secret_password
volumes:
  students-db:
{% endhighlight %}
In our compose.yaml, we define an environment variable on the web container that overrides the URL of the database. This is required because containers run in isolation: the two instances are not in the same host (so localhost will not work). Docker automatically creates a network between containers, such that one can access other containerized servers by their name (students_db).
## Build and Testing
Now what remains to do is to build the application (**./gradlew build -x test**), remove the container and the image of our **web** service and then run **docker compose up -d**. We can test:
### Check the database table
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_1.png)
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_2.png)
### POST new Students 
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_3.png)
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_10.png)
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_4.png)
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_5.png)
### Check persistency
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_7.png)
### Check Errors
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_8.png)
![DCJPA hibernate]({{ site.baseurl }}/images/dcjpa_hibernate_9.png)