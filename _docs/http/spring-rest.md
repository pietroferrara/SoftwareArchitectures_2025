---
title: 3. Spring REST
category: 05. HTTP
order: 3
---
<h2>Contents</h2>
* toc
{:toc}
## Spring REST
Here, we are going to build a RESTful application in Spring. With the term RESTful we mean a web service that adheres to the REST Architectural style.

In Spring, all comes easy for us. Let's make our Students App RESTful.
### Students App
In the previous lesson, we built our first Spring Controller:
{% highlight java %}
package com.example.studentsapp.controllers;

import com.example.studentsapp.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Student {
    @Autowired
    private StudentService studentService;
    @RequestMapping("/students")
    public String getStudents() {
        return studentService.findAll().toString();
    }
    @RequestMapping("/students/add")
    public String addStudent(@RequestParam String id, @RequestParam String name, @RequestParam String surname) {
        studentService.add(name, surname, id);
        return "OK";
    }
}
{% endhighlight %}
However, this is not RESTful: REST puts the focus on resources, and resources should be clearly identifiable by identifiers and accessed by URLs.  
Our simple example has some problems: if you remember, we perform a GET to insert a new student, and we pass all the student's info in the query string. Then, we return an "OK". But what happens if the student already exists in the application? What happens if we have some problems? We did not catch these things. Let's fix it.

## Spring REST
We present now some annotations and classes that Spring uses to build REST controllers.
### @RestController
This annotation tells Spring that our Student class contains a REST Controller. @RestController is a Stereotype, i.e. a specialization, of @Controller that adds the capability to automatically bind the returned value of a function to the response body.
### Mapping annotations
In order to map specific requests to appropriate request handlers (i.e., methods), we use mapping annotation. For example, @GetMapping: maps all GET requests to a particular resource to the annotated methods. Same for @PostMapping, @PutMapping, and so on. Change the annotation of getStudents() method to @GetMapping("/students"):
{% highlight java %}
    @GetMapping("/students")
    public String getStudents() {
        return studentService.findAll().toString();
    }
{% endhighlight %}
Let's try to add a new student, and then call GET /students: as you can notice, this is not REST already! 
### ResponseEntity class
Suppose that we want to produce a JSON.
ResponseEntity is a Spring class that represents a REST response. A ResponseEntity is parametric to an Object that we wish to produce as body. In our case, we want to return a list of Students (remember to rebuild your application):
{% highlight java %}
    @GetMapping("/students")
    public ResponseEntity<List<com.example.studentsapp.models.Student>> getStudents() {
        return ResponseEntity.ok(studentService.findAll());
    }
{% endhighlight %}
We have a JSON object as a response: wonderful! You can see how to build different responses in the <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html">documentation</a>.  Using ResponseEntity you can, for example, set a header, return other status code, etc without too many efforts. 
### @RequestBody
Now we are going to make the POST more POST. We wish to use a JSON Body as a Request, that corresponds to a Student Object, and throw away all the query string parameters. We can use the @RequestBody annotation, which works like @RequestParam but, instead of mapping query string parameters in the method arguments, it maps the content of the body. Firstly, let's change @RequestMapping in @PostMapping, and change also the path to be more REST-style:
{% highlight java %}
@PostMapping("/students")
    public String addStudent(@RequestParam String id, @RequestParam String name, @RequestParam String surname)
{% endhighlight %}
Now we change the method signature and the returned value:
{% highlight java %}
@PostMapping("/students")
    public ResponseEntity<Student> addStudent(HttpServletRequest request, @RequestBody com.example.studentsapp.models.Student student)
{% endhighlight %}
Here we are going to POST a new resource (a new Student) inside our application. We can then return a 201 (Created) status, and we can use the Location header to tell the client where this newly created resource is located:
{% highlight java %}
@PostMapping("/students")
public ResponseEntity<Student> addStudent(HttpServletRequest request, @RequestBody com.example.studentsapp.models.Student student) throws URISyntaxException {
    studentService.add(student);
    return ResponseEntity.created(new URI(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + request.getRequestURI() + "/" +  student.getId())).build();
}
{% endhighlight %}
Now we are ready to do a test using insomnia:
![HTTP Spring]({{ site.baseurl }}/images/http_springrest_1.png)
As you can see, Location contains the URL from which we can access the resource. However, the function that we wrote is not properly correct: can you tell us why? Spoiler: what happens if we try to POST a new Student with the same ID? We would like to catch this case. We can return a 409 (Conflict) status code.
### Handle Status Codes
Add a new Class that models the ErrorResponse:
{% highlight java %}
package com.example.studentsapp.models;

public class ErrorResponse {
    private String message;
    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
{% endhighlight %}
Let's make the create method on the repository return false if the Student already exists:
{% highlight java %}
@Repository
public class StudentRepository {
    private Map<String, Student> students;

    public StudentRepository() {
        this.students = new HashMap<>();
    }

    public boolean create(Student student) {
        if (students.get(student.getId()) == null) {
            students.put(student.getId(), student);
            return true;
        }
        return false;
    }

    ...
}
{% endhighlight %}
Idem on StudentService.add:
{% highlight java %}
@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;


    public List<Student> findAll() {
        return new ArrayList<Student>(studentRepository.getAll());
    }

    public Student fetch(String id) {
        return studentRepository.fetch(id);
    }

    public boolean add(String name, String surname, String id) {
        return studentRepository.create(new Student(name, surname, id));
    }

    public boolean add(Student student) {
        return studentRepository.create(student);
    }

    ...
}
{% endhighlight %}
Now catch the existence of the Student inside the Controller:
{% highlight java %}
@RestController
public class Student {
    @Autowired
    private StudentService studentService;
    @GetMapping("/students")
    public ResponseEntity<List<com.example.studentsapp.models.Student>> getStudents() {
        return ResponseEntity.ok(studentService.findAll());
    }
    @PostMapping(value = "/students")
    public ResponseEntity<?> addStudent(HttpServletRequest request, @RequestBody com.example.studentsapp.models.Student student) throws URISyntaxException {
        if (studentService.add(student)) {
            return ResponseEntity.created(new URI(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + request.getRequestURI() + "/" + student.getId())).build();
        }
        ErrorResponse responseJson = new ErrorResponse("Student with this id already exists");
        return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.CONFLICT);
    }
}
{% endhighlight %}
![HTTP Spring]({{ site.baseurl }}/images/http_springrest_2.png)
### @PathVariable
We can also add a GET to fetch a particular Student by ID:
{% highlight java %}
@GetMapping("/students/{id}")
public ResponseEntity<com.example.studentsapp.models.Student> getStudents(@PathVariable String id) {
    com.example.studentsapp.models.Student s = studentService.fetch(id);
    if (s == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(s);
}
{% endhighlight %}
Using @PathVariable annotation, the value of the attribute given to the method is fetched directly from the path of the URL.  
![HTTP Spring]({{ site.baseurl }}/images/http_springrest_3.png)
![HTTP Spring]({{ site.baseurl }}/images/http_springrest_4.png)
### Add hypermedia to Response Body
We said that the most important thing of REST is a Resource. To add some sort of navigation between resources, one can add links in bodies that refer to the resources. Considering the previous GitHub example, a **GET https://api.github.com** call returns a JSON of the current users, and if we look well we can see that inside of it we have a lot of URLs, that permits us to navigate all the linked resource e. We can add this capability easily in our application with the usage of <a href="https://spring.io/guides/gs/rest-hateoas/">**Spring HATEOAS**</a>. Firstly, add Spring HATEOAS to our build.gradle, then rebuild the application.

{% highlight kotlin %}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
{% endhighlight %}

{% highlight java %}
package com.example.studentsapp.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.RepresentationModel;

public class Student extends RepresentationModel<Student> {
    private String name;
    private String surname;
    private String id;

    public Student(String name, String surname, String id) {
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.add(linkTo(methodOn(com.example.studentsapp.controllers.Student.class).getStudents(id)).withSelfRel()); // add a self link.
    }
{% endhighlight %}
![HTTP Spring]({{ site.baseurl }}/images/http_springrest_5.png)
And that's it.
<div>
Previous: <a href="/SoftwareArchitectures_2025/http/rest">HTTP - REST</a> 
</div>
