---
title: 2. Example
category: 06. Camel
exclude: false
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
## Export Students to FTP
You want to integrate in your Students App an external service to do some analytics. This service, unfortunately, is not managed directly by you and does not have API endpoints to communicate with. 
The external agency that manages this service told you "don't worry, just put every students in a folder in a FTP and we manage the import on our side. Students must be in a json format, one student per file".
How we can do this? Let's see an example with Camel.
### Requirements
Before writing code, we should sit and think about the overall export process. It is required to:
- **Avoid multiple exports**: A student should be exported only one time. We need to figure it out how to do this: we can, for example, add a boolean column in our database table that models the fact that a customer is exported or not.
- **Asynchronous export**: the company that manages the external service says that they will perform an import daily, so it is not needed to export a student in a hurry. The idea is to schedule a job that every few minutes (or hours) perform an export of the students created after the last export. Having an asynchronous job permits to catch and handle better errors: in this case, for example, the application will continue to run even if there are problems with regarding to the FTP server.
- **File format**: we know that we need to export students in json format. The company tells us that the available fields are: name, surname, id, and exportDate. This seems not problematic, because in our application we have the same key for these fields except for the exportDate (but this field can be generated on-demand).
## Our compose.yaml
We want to test the integration before going live, so we can add a dockerized FTP server as a service used by our app:
{% highlight bash %}
services:
  web:
    build: .
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - students_db
      - studentsftp
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://students_db:3306/studentsapp?createDatabaseIfNotExist=true
      - CAMEL_FTP_URI=ftp://studentsapp@studentsftp:21/students/export?password=stUd3nts@pp&passiveMode=true
  students_db:
    volumes:
      - students-db:/var/lib/mysql
    image: "mysql:5.7"
    restart: always
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=secret_password
  studentsftp:
    volumes:
      - students-ftp:/ftp/studentsapp
    image: "delfer/alpine-ftp-server"
    ports:
      - "21:21"
      - "21000-21010:21000-21010"
    environment:
      - USERS=studentsapp|stUd3nts@pp
volumes:
  students-db:
  students-ftp:
{% endhighlight %}

Note that ftp name is without the underscore: this is because seems that Camel has some difficulties to handle hostname with underscore.
## Changes to our database table
We want to save the export status of a student: to do this, we can add a column in our student model:
{% highlight java %}
package com.example.studentsapp.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;
@Entity // Entity means that this class must be threatened as a relational entity.
@Table( name = "Students" ) // This is used by the ORM to link this Entity in a DBMS Table.
public class Student extends RepresentationModel<Student> {
    @Column // this tells ORM to map this attribute (name) to a table field with same name. You can specify the name of the field inside the database by using @Column(name="db_column_name")
    private String name;
    @Column
    private String surname;
    @Id // Id means that this field is the Primary Key of the table.
    @Column
    private String id;

    @Column
    @JsonIgnore
    private Boolean exported = false;
    [...]
}
{% endhighlight %}
Note the @JsonIgnore annotation: this means that this attribute is hidden and not shown in REST responses. We sets a default value to false (not exported), but we need also to sets the flag on the existing students. We can do this by running the application (such that Hibernate can create the column in our table) and then perform an UPDATE using Dbeaver:

## Gradle dependencies
Let's add Camel dependencies to our build.gradle:
{% highlight kotlin %}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.orm:hibernate-core:6.2.6.Final")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.apache.camel:camel-jdbc:4.0.0")
    implementation("org.apache.camel:camel-jackson:4.0.0")
    implementation("org.apache.camel:camel-ftp:4.0.0")
    implementation("org.apache.camel:camel-bean:4.0.0")
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.0.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
{% endhighlight %}

## Export model
We want to model the Student to a specific Class for export: we want to keep separate the models used by the Application from the model used by the Export: this is because it is easy to add attributes or perform changes on the model without affecting the application. Our ExportStudent Model can be something like this (note that this is just a POJO: no annotations, no extensions, ...):
{% highlight java %}
package com.example.studentsapp.models;

import java.util.Date;

public class ExportStudent {
    private String name;
    private String surname;
    private String id;
    private Date exportDate;
    public String getName() {
        return name;
    }

    public ExportStudent() {}
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getExportDate() {
        return exportDate;
    }

    public void setExportDate(Date exportDate) {
        this.exportDate = exportDate;
    }



    public ExportStudent(String id, String name, String surname) {
        this.surname = surname;
        this.name = name;
        this.id = id;
        setExportDate();
    }

    private void setExportDate() {
        this.exportDate = new Date();
    }
}
{% endhighlight %}

## Camel Route
What remain to do is to define our Camel Route.
{% highlight java %}
package com.example.studentsapp.camel;

import com.example.studentsapp.models.ExportStudent;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class StudentsExport extends RouteBuilder {
    @Autowired
    DataSource dataSource;

    @Value("${camel.ftp.uri}")
    private String ftpURI;
    private StudentConverter mapper = new StudentConverter();

    @Override
    public void configure() throws Exception {

        JacksonDataFormat jsonDataFormat = new JacksonDataFormat(ExportStudent.class);
        from("timer:simple?period=60000")
        .setBody(constant("select * from students where exported=FALSE"))
        .to("jdbc:dataSource")
        .choice()
            .when(simple("${body.isEmpty()}"))
                .log("No students to export...")
            .otherwise()
            .split(body())
            .bean(mapper, "toStudent")
            .setHeader("StudentID", simple("${body.id}"))
            .setHeader("CamelFileName", simple("${body.id}.json"))
            .process(this::log)
            .marshal(jsonDataFormat)
            .to(ftpURI)
            .setBody(simple("UPDATE students SET exported = TRUE where id='${header.StudentID}'"))
            .to("jdbc:dataSource")
        .end();
    }

    private void log(Exchange exchange) {
        System.out.println("Exporting student " + ((ExportStudent)exchange.getIn().getBody()).getId());
    }
}
{% endhighlight %}
Let's spend some words here: we annotated the class as a Spring Component: in this way, our Route is automatically launched with the application. In the configure() method, all the logic is defined: every 60000 milliseconds (every minute), a SELECT will be performed in our database (database hostname, username and password is taken direcly from application.properties autowiring the dataSource). Then we made a choice (it is like an if): if the body is empty (i.e., no students is returned by the query), we log this fact (log) and we exit the pipeline. Otherwise, we split the body (because we could have more than one students to process) and for every student we call StudentConverter.toStudent: this is a custom bean that permits to parse the HashMap returned by the jdbc camel connector to a Student. The implementation of StudentConverter is easy: just create a new java file in studentsapp.camel package with this content:
{% highlight java %}
package com.example.studentsapp.camel;

import com.example.studentsapp.models.ExportStudent;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class StudentConverter {

    public ExportStudent toStudent(LinkedHashMap<String, Object> map) {
        return new ExportStudent((String)map.get("id"), (String)map.get("name"), (String)map.get("surname"));
    }
}
{% endhighlight %}
Next, we set some headers: we want to keep track of the student id and of the name of the FTP file (studentId.json). Then, we call a process function (in this case, we just log something, but we can do something more complex). We convert the student in a JSON and we export it to the ftp (**to(ftpUri)**). ftpUri is a class attribute, and the @Value("${camel.ftp.uri}") annotation means that the value of this attribute must be taken from the camel.ftp.uri property (defined as an environment variable in our container, or also be taken from application.properties):
{% highlight bash %}
spring.devtools.restart.enabled=false
server.port=8080
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.datasource.url=jdbc:mysql://localhost:3306/studentsapp?createDatabaseIfNotExist=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.datasource.username=root
spring.datasource.password=secret_password
camel.ftp.uri=ftp://studentsapp@localhost:21/students/export?password=stUd3nts@pp&passiveMode=true
{% endhighlight %}

Note that in application.properties the hostname of FTP (and of the MySQL server) is localhost: this is needed if we want to test directly the application without docker (in this case, we are not in the same network). Container's environment variable overrides the value of these properties. Then we UPDATE the student setting the exported flag as true.
![Camel Example 1]({{ site.baseurl }}/images/camel_example_1.png)
![Camel Example 2]({{ site.baseurl }}/images/camel_example_2.png)
## Testing
Let's test the integration: remove the container and the image of the web service and:
{% highlight bash %}
~/IdeaProjects/StudentsApp ./gradlew clean        

BUILD SUCCESSFUL in 2s
1 actionable task: 1 executed
~/IdeaProjects/StudentsApp ./gradlew build -x test

> Task :compileJava
Note: /Users/giacomo/IdeaProjects/StudentsApp/src/main/java/com/example/studentsapp/repositories/StudentRepository.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

BUILD SUCCESSFUL in 2s
4 actionable tasks: 4 executed
~/IdeaProjects/StudentsApp docker compose up -d   
[+] Building 2.6s (7/7) FINISHED                                                                                                                                                                                                docker:desktop-linux
 => [web internal] load build definition from Dockerfile                                                                                                                                                                                        0.0s
 => => transferring dockerfile: 176B                                                                                                                                                                                                            0.0s
 => [web internal] load .dockerignore                                                                                                                                                                                                           0.0s
 => => transferring context: 2B                                                                                                                                                                                                                 0.0s
 => [web internal] load metadata for docker.io/library/eclipse-temurin:17-jdk-alpine                                                                                                                                                            1.4s
 => [web internal] load build context                                                                                                                                                                                                           0.7s
 => => transferring context: 58.42MB                                                                                                                                                                                                            0.7s
 => CACHED [web 1/2] FROM docker.io/library/eclipse-temurin:17-jdk-alpine@sha256:e890b4f91ec8aa40f1537a50a53ab516fc42341c3b5dd608d1aeee2b1cba55b1                                                                                               0.0s
 => [web 2/2] COPY build/libs/*.jar app.jar                                                                                                                                                                                                     0.1s
 => [web] exporting to image                                                                                                                                                                                                                    0.3s
 => => exporting layers                                                                                                                                                                                                                         0.3s
 => => writing image sha256:b78076a1c79d98322b3bc4bd73ce01c0fa28bd427c139e7b5760a0b0469661c3                                                                                                                                                    0.0s
 => => naming to docker.io/library/studentsapp-web                                                                                                                                                                                              0.0s
[+] Running 4/4
 ✔ Container studentsapp-studentsftp-1        Running                                                                                                                                                                                           0.0s 
 ✔ Container studentsapp-students_rabbitmq-1  Running                                                                                                                                                                                           0.0s 
 ✔ Container studentsapp-students_db-1        Running                                                                                                                                                                                           0.0s 
 ✔ Container studentsapp-web-1                Started                                                                                                                                                                                           0.4s 
~/IdeaProjects/StudentsApp 

{% endhighlight %}
![Camel Example 3]({{ site.baseurl }}/images/camel_example_3.png)
![Camel Example 4]({{ site.baseurl }}/images/camel_example_4.png)
![Camel Example 5]({{ site.baseurl }}/images/camel_example_5.png)

Let's add a new Student:
![Camel Example 6]({{ site.baseurl }}/images/camel_example_6.png)
![Camel Example 7]({{ site.baseurl }}/images/camel_example_7.png)
![Camel Example 8]({{ site.baseurl }}/images/camel_example_8.png)
![Camel Example 9]({{ site.baseurl }}/images/camel_example_9.png)
![Camel Example 10]({{ site.baseurl }}/images/camel_example_10.png)
We can also connect to our FTP Server using an FTP Client like, for example, <a target="_blank" rel="noopener noreferrer" href="https://filezilla-project.org/">FileZilla</a>:
![Camel Example 11]({{ site.baseurl }}/images/camel_example_11.png)
<div>
Previous: <a href="/SoftwareArchitectures_2025/camel/introduction">Camel - Introduction</a> 
</div>