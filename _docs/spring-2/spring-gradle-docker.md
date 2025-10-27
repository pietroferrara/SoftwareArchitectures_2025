---
title: 3. Spring + Gradle + Docker
category: 04. Java Spring (Part II)
exclude: false
order: 3
---
<h2>Contents</h2>
* toc
{:toc}
Before proceeding, we want to connect all the dots that seem so far and see how we can deploy a Spring application as a Docker component.

## The Dockerfile
Let's follow what the Spring Boot <a target="_blank" rel="noopener noreferrer" href="https://spring.io/guides/topicals/spring-boot-docker/">documentation</a> says. The idea is to copy and execute the .jar file produced by Gradle inside a Docker container.  
Firstly, we need to build the application:
{% highlight bash %}
./gradlew build
{% endhighlight %}
This command will generate the application's .jar files. Where? If you are using standard Gradle configuration, they are located inside **build/libs**:
{% highlight bash %}
~/IdeaProjects/StudentsApp ls build/libs
StudentsApp-0.0.1-SNAPSHOT-plain.jar StudentsApp-0.0.1-SNAPSHOT.jar
~/IdeaProjects/StudentsApp 
{% endhighlight %}
As you can see, we have two .jar files. We don't want the -plain.jar one. To tell Gradle not to produce this file, we should add this to our Gradle file:
{% highlight kotlin %}
tasks.getByName<Jar>("jar") {
    enabled = false
}
{% endhighlight %}
Remove the build folder and relaunch the build task: now you should see only one .jar file.
Then we create our Dockerfile in the root folder of our project:
{% highlight bash %}
FROM eclipse-temurin:17-jdk-alpine
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
{% endhighlight %}
After that, we can build the image and run a container:
{% highlight bash %}
docker build -t unive/spring/student .
{% endhighlight %}
{% highlight bash %}
docker run -p 8081:8080 unive/spring/student
{% endhighlight %}
![Spring docker]({{ site.baseurl }}/images/spring_docker_1.png)
<div class="lesson-nav">
    <div>
    Previous: <a href="/SoftwareArchitectures_2025/spring-2/application-example">Java Spring - Example of a Spring Application</a>
    </div>
</div>