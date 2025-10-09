---
title: 2. build.gradle 
category: 02. Gradle
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
## Anatomy of build.gradle
The build.gradle file contains the configuration that tells gradle how to build the project: inside of it, we have for example the definition of the external dependencies of the project and where to fetch them, and user-defined tasks.
This is the build.gradle file from the previous example:
{% highlight kotlin %}
plugins {
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.example.Main")
}

tasks.test {
    useJUnitPlatform()
}
{% endhighlight %}
Let's see the meaning of each block.
### Plugins
{% highlight kotlin %}
plugins {
    id("application")
}
{% endhighlight %}
A <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/plugins.html">Gradle plugin</a> consists of an enhancement of Gradle core that specifies how to build and run the code, targetting specific build type and providing already-defined tasks. Each programming language has its own plugin. If you remember, in the last lesson we changed to the build.gradle, substituting the Java plugin with the application one. We did it because the application plugin is an extension of the Java plugin (in other words, it inherits all the tasks of the Java plugin) that adds, among other things, the "run" task that permits, as we have seen, to run the application. As documentation <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin">says</a>, *"java plugin adds basic building blocks for working with JVM projects. Its feature set has been superseded by other plugins, offering more features based on your project type. Instead of applying it directly to your project, you should look into the java-library or application plugins or one of the supported alternative JVM language."*.  
The <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/application_plugin.html">documentation</a> explain well the application plugin.  

To summarize, a Plugin is just an extension of the Gradle core that adds tasks and functionality to it.  If we want to be more specific, a Gradle plugin can:
1. Extend the Gradle model (for example, adding new DSL elements that can be configured)
2. Configure the project according to conventions (for example, adding new tasks)
3. Apply specific configuration (for example, enforcing organizational standards)
### Application
{% highlight kotlin %}
application {
    mainClass.set("org.example.Main")
}
{% endhighlight %}
This block is specific to the application plugin. It tells Gradle that the Main Class of the program (i.e. our entry point) is the class org.example.Main (note that the name should be fully qualified). This is an example of an extension of the Gradle model.
### Repositories
{% highlight kotlin %}
repositories {
    mavenCentral()
}
{% endhighlight %}
There exist some repositories that host libraries for projects. In the <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/declaring_repositories.html">repositories</a> block, you can specify which repositorie to use. In our example, we use <a target="_blank" rel="noopener noreferrer" href="https://repo.maven.apache.org/maven2/">Maven Central</a>, but we can have more than one repository. Let's see another example:
{% highlight kotlin %}
repositories {
    mavenCentral()
    google()
    maven {
        url = uri("https://repository.jboss.org/maven2")
    }
}
{% endhighlight %}
Here we have multiple repositories. This means that, if we want to fetch a library named "mickeymouse", if the library does not exist in the first repository declared, gradle will check the existence on the next repository in the list, until it finds something. Let's see these repositories:
1. mavencentral() is just a function that returns the maven central library URL.
2. google() is the <a target="_blank" rel="noopener noreferrer" href="https://maven.google.com/web/index.html">google maven library</a>.
3. the maven block with url variable is a special block that permits the declaration of a custom repository just by adding its URL.  

Note that these are just examples, and we redirect curious readers to the <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/declaring_repositories.html">official documentation</a>.
### Dependencies
{% highlight kotlin %}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
{% endhighlight %}
In this block, we define all the <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/declaring_dependencies.html#declaring-dependencies">dependencies</a> (i.e. libraries and/or frameworks) used by the project.  
We can have different types of dependencies because we could have different dependencies for different scopes. For example, some dependencies should be used for compiling source code whereas others only need to be available at runtime, or only for executing tests. In our build.gradle we have defined some testImplementation, that is, dependencies only used for testing. Let's see the meaning of the second line: as you can notice, we have a function, testImplementation. This defines the configuration, i.e. the scope. A scope can be defined in plugins or by the user. For example, for the Java plugin, we have different scopes (we will see here a few of them):
1. compileOnly: dependencies used only in compile-time.
2. runtimeOnly: dependencies used only in run-time.
3. testImplementation: dependencies used only for testing.
A comprehensive explanation of Java Plugin dependencies can be found <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_plugin_and_dependency_management">here</a>. Note that since Application is an extension of the Java plugin, these scopes are available also if you are using the Application plugin. In the next lesson, we will see an example of how we can import (and use) a dependency.  
"org.junit.jupiter:junit-jupiter" is the dependency that we want to import. The substring before ':' represents the group (i.e., it is an identifier of an organization, company, or project). After the ':' we have the name of the dependencies (in our case, junit-jupiter).  

In addition, you could also have another ':' after the name and the last part of the string represents the version of the dependency that you want to use. For example, the first dependency (that has group org.junit, and name junit-bom) is bound to version 5.9.1. If the version is omitted, then Gradle will fetch the latest version of the dependency. This is not always optimal, because if a new version of a runtimeOnly dependency that you are using ships out, the next build of your app could fetch the newer version, and, if the developers introduce some breaking changes, then these changes may break your app.  

It is important to say that before using a dependency the developer should check his reliability: is the dependency regularly updated? How many people used it? Does the publisher have a good reputation?
#### Resolving dependencies
Gradle has a caching system for dependencies. To resolve a dependency for a project:
1. First, Look if the artifact is in the cache. Usually, the cache is located in "~\.gradle\caches".  
2. If this is the case, retrieve and use the artifact.
3. If not, connect to the given repositories (the ones specified in the "repositories" block).
4. Look to the specific group/name/version.
5. Download the desired artifact and cache it.
6. Retrieve and use the artifact.
### Tasks
{% highlight kotlin %}
tasks.test {
    useJUnitPlatform()
}
{% endhighlight %}
This block configures test tasks. This task is specific to Java-related plugins. We don't want to spend too many words on it, but if you are interested, <a target="_blank" rel="noopener noreferrer" href="https://docs.gradle.org/current/userguide/java_testing.html">here</a> more information is present. 

Our tasks.test block says that for our project we are going to use JUnit for testing. What is JUnit? We will answer this question in the next lesson.
<div class="lesson-nav">
    <div>
        Previous: <a href="/SoftwareArchitectures_2025/gradle/introduction">Gradle - Introduction</a>
    </div>
    <div>
        Next: <a href="/SoftwareArchitectures_2025/gradle/testing-java-applications">Gradle - Testing Java Applications</a>  
    </div>
</div>