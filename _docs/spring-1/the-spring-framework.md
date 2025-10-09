---
title: 2. The Spring Framework
category: 03. Java Spring (Part I)
exclude: true
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
We saw what Spring is, and we deployed an example of a Spring application in a local server. We will now examine and enhance the example of getting started that we have seen. Note that the Spring Framework is vast, and in these lessons, we want to provide an intuition of how it works from a high-level perspective without going too much into the details. Interested students are encouraged to look at the official documentation. However, all the information provided here should be enough for the scope of the final project.

## Configuration
The @SpringBootApplication annotation is used to mark a configuration class. In a configuration class, we insert our static main, which is the entry point of the application that instantiates and runs the Spring Application, with ***StringApplication.run***:
{% highlight java %}
@SpringBootApplication
public class SpringGettingStartedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringGettingStartedApplication.class, args);
    }
}
{% endhighlight %}
Try to run the application without the @StringBootApplication. The server will not run. This is because the @StringBootApplication annotation (that comes from the Spring Boot dependency, that has we said before helps us to build a Spring Server application) tells the Spring framework how to be configured, doing the dirty job for us. This is great, but sometimes, some changes to the default configuration are required. For example, you may want to change the default port, and there are different ways to achieve this. Let's see some of them.
### application.properties
In **/src/main/resources** folder, there should be a file named **application.properties**. If not, create it.   

In this file, we put properties that configure the behavior of our application. We can set the port with:
{% highlight properties %}
server.port=8888
{% endhighlight %}
Notice that the port changed by rebooting the application:
![Spring framework]({{ site.baseurl }}/images/spring_framework_1.png)
In <a target="_blank" rel="noopener noreferrer" href="https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html">this</a> link, some properties are listed.
### Programmatic Configuration
Properties can be added directly in code in this way:
{% highlight java %}
@SpringBootApplication
public class CustomApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CustomApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8887"));
        app.run(args);
    }
}
{% endhighlight %}
Instead of using the static method SpringApplication.run, we instantiate a StringApplication object and set the desired properties before running the application.
Properties are defined using a Map<String, Object> data structure. If you run the application, probably you will notice that the port does not change. This is because the properties defined in application.properties have priority over what we define in the code. Remove the server.port property from the file and relaunch the app. Now the port should be the one defined in the setDefaultProperties function. As the name suggests, this method sets default values for properties and should be used as a fallback for properties not defined in application.properties.
Another way to configure the application is by using a special Spring Component. A Spring component can be used for IoC, and it is defined by using the @Component annotation. The framework collects all component classes for our project and performs the required operation in a transparent manner. Let's see an example:
{% highlight java %}
@Component
package com.example.spring.customizers;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerPort implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(8886);
    }
}
{% endhighlight %}
Create a file ServerPort.java inside com.example.Spring.customers package and insert the above snippet. The class implements the <a target="_blank" rel="noopener noreferrer" href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/web/server/WebServerFactoryCustomizer.html">WebServerFactoryCustomizer</a> interface, which permits to customize the server creation. Spring then calls this implementation internally (again, note the IoC in action). The core of this little class is defined inside the customize method, in which we set the port.  
Note that with Component, we are not using properties; we are telling the Factory directly how it should initialize the server. This method overrides the value defined in the port property.
### Command-Line
You can also define arguments from the command line:
{% highlight java %}
./gradlew bootRun --args="--server.port=8885"
{% endhighlight %}
<div class="lesson-nav">
    <div>
        Previous: <a href="/SoftwareArchitectures_2025/spring-1/introduction">Java Spring - Introduction</a>
    </div>
</div>