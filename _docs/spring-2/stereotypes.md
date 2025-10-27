---
title: 1. Beans and Stereotypes
category: 04. Java Spring (Part II)
exclude: false
order: 1
---
<h2>Contents</h2>
* toc
{:toc}
## Spring Beans
A Spring Bean is simply a Java object that is instantiated, configured, and managed by the Spring IoC container. Beans are fundamental in Spring, as they represent the core building blocks of an application, containing the logic, state, and interactions necessary to perform business functions.
A bean can be defined in multiple ways, including:

1. Annotated with @Component or its specializations (@Controller, @Service, @Repository) (more details in a moment).
2. Declared in XML configuration:
For example, you have a simple Java class:
{% highlight java %}
package com.example.springbeans;

public class HelloWorldBean {

    private String text;

    public HelloWorldBean() {
        System.out.println("Instantiating HelloWorldBean");
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void hello() {
        System.out.println("Text: " + text);
    }
}
{% endhighlight %}
You can use it as a Spring bean by using an XML configuration file (stored in the resources folder):
{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Bean definition for UserService -->
    <bean id="userService" class="com.example.springbeans.HelloWorldBean">
        <!-- Setting the property 'serviceName' -->
        <property name="text" value="Hello World!"/>
    </bean>
</beans>
{% endhighlight %}
Then, you need to instruct Spring where to find these beans definitions (see for example <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ImportResource.html" target="_blank" rel="noopener noreferrer">@ImportResource</a>).
{% highlight java %}
package com.example.springbeans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:applicationContext.xml") // applicationContext.xml contains the beans definitions
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
{% endhighlight %}
If you run the application, you should see the printed line from the constructor.
## Spring Stereotypes
In the Spring Framework, stereotype annotations define the roles of classes within an application, making the codebase more organized and easy to manage. These annotations serve as a form of metadata instructing the Spring container on handling the components during the application runtime. The most generic stereotype in Spring is the **@Component** annotation, and all other stereotype annotations, like **@Controller**, **@Service**, and **@Repository**, are specialized subtypes of **@Component**. Let’s explore each of these annotations in detail.
### @Component
**@Component** is a generic stereotype annotation for Spring-managed components. When a class is annotated with @Component, it tells Spring that it is a bean, or Spring-managed object, and should be automatically discovered and registered in the Spring ApplicationContext during classpath scanning.

This annotation is ideal for classes that don’t fall under specific roles.

### @Controller
The **@Controller** annotation is a specialization of @Component used to mark a class as a Spring MVC controller. This annotation handles HTTP requests and maps them to methods that produce responses (such as HTML views or JSON/XML data).

A controller in Spring typically processes user input, interacts with service components, and returns a view or data response.
For example, one can use @RestController (subclass of @Controller) to expose an REST HTTP endpoint. 
{% highlight java %}
package com.example.springbeans;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerExample {
    
    @RequestMapping("/")
    public String index() {
        return "Hello World!";
    }
}
{% endhighlight %}
In the snippet above, @RequestMapping is an annotation that maps web requests to specific handler classes and methods in Spring MVC. It allows you to specify the URL pattern, HTTP methods, request parameters, headers, and other conditions under which a method should be invoked. The default HTTP method is GET, but you can implements other HTTP methods by using the method parameter, for example:
{% highlight java %}
@RequestMapping(value = "/users", method = RequestMethod.POST)
{% endhighlight %}
To make code more readable and concise, Spring introduced more specialized annotations to handle specific HTTP methods:
- @GetMapping: For HTTP GET requests.
- @PostMapping: For HTTP POST requests.
- @PutMapping: For HTTP PUT requests.
- @DeleteMapping: For HTTP DELETE requests.
- @PatchMapping: For HTTP PATCH requests.
You don't need to specify the method in the annotation using these.

### @Service
The @Service annotation is another specialization of @Component. It indicates that a class provides some business logic or service. This annotation is often used for the middle layer of an application, where you encapsulate the business rules or logic.

It plays a key role in the Service Layer pattern, handling the logic behind user requests and coordinating various repository operations. In the next part of the lesson, we will see how to use it.
### @Repository
The @Repository annotation is a specialization of @Component and is used to designate a class as a Data Access Object (DAO). This class interacts with the database, managing persistence operations like creating, reading, updating, and deleting records. In other words, a class annotated with @Repository encapsulates the logic needed to interact with the database or other data storage mechanisms.
<div class="lesson-nav">
<div>
Next: <a href="/SoftwareArchitectures_2025/spring-2/application-example">Java Spring - Example of a Spring Application</a>  
</div>
</div>