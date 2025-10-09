---
title: 4. Example - log4j
category: 02. Gradle
order: 4
---
<h2>Contents</h2>
* toc
{:toc}

Here, we will see an example of how we can add a dependency to our Gradle project. For this purpose, we will present a Java logging library, <a target="_blank" rel="noopener noreferrer" href="https://logging.apache.org/log4j/2.x/">Log4j</a>.
## Logging
When you write software, it is useful to introduce logs. Logs can help, for example, to record the execution of events in your application, to identify and understand the pattern of activities, and to identify the source of problems in case of software incidents.  
Log4j is a library that enhances the main logging mechanism of Java. To use Log4J in a Java Gradle project, we follow what the <a target="_blank" rel="noopener noreferrer" href="https://logging.apache.org/log4j/2.x/maven-artifacts.html">documentation</a> says and we add the dependencies. Our build.gradle file should look like this:
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
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

application {
    mainClass.set("org.example.Main")
}

tasks.test {
    useJUnitPlatform()
}
{% endhighlight %}

Then, we can just start using the log4j library directly on our application:
{% highlight java %}
package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger("Main");
    public static int add(int a, int b) {
        return Math.addExact(a, b);
    }

    public static void main(String[] args) {
        // int a = Integer.parseInt(args[0]);
        // int b = Integer.parseInt(args[1]);
        // int result = Main.add(a, b);
        logger.info("Hello World from log4j");
    }
}
{% endhighlight %}
However, nothing is printed out in the console. This is because we need to configure the log4J library. To do so, create a file in /src/main/resources, named "log4j2.xml", with this content:
{% highlight java %}
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
        </Console>
        <File name="File" fileName="./logs/log.log" immediateFlush="false" append="false">
            <PatternLayout pattern="%d{yyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="debug">
            <AppenderRef ref="Console" />
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
{% endhighlight %}
<a target="_blank" rel="noopener noreferrer" href="https://logging.apache.org/log4j/2.x/manual/configuration.html">Here</a> you can find additional information. Our configuration file tells log4j to log file in the console and in a file in folder /logs/log.log. Check it out!  

## Log level
You can classify logs based on their importance. You can do this by using the log level. A high log level means that the logged information is more important than a lower one and requires particular attention. Usually, a high level reports fatal errors and errors in general. Here are what log level log4j offers (ordered by importance):
1. Fatal: tells that the application enters a state in which one of the crucial functionalities stopped working.
2. Error: tells that one or more functionality doesn't work properly.
3. Warn: tells that something unexpected happens, but the application is not affected.
4. Info: tells that something happens (for example, used to track that the application enters a certain state).
5. Debug: in this level, you should put all the information that regards debugging.
6. Trace: used when you need full visibility of what it is happening inside your application (for example, you can use trace to keep track of the control flow of your application).

You can use log levels in the following way:

{% highlight java %}
package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger("Main");
    public static int add(int a, int b) {
        return Math.addExact(a, b);
    }

    public static void main(String[] args) {
        // int a = Integer.parseInt(args[0]);
        // int b = Integer.parseInt(args[1]);
        // int result = Main.add(a, b);
        logger.trace("Hello World from log4j");
        logger.debug("Hello World from log4j");
        logger.info("Hello World from log4j");
        logger.warn("Hello World from log4j");
        logger.error("Hello World from log4j");
        logger.fatal("Hello World from log4j");
    }
}
{% endhighlight %}

{% highlight bash %}
~/IdeaProjects/Gradle-GettingStarted ./gradlew run

> Task :run
15:45:27.762 [main] DEBUG Main - Hello World from log4j
15:45:27.763 [main] INFO  Main - Hello World from log4j
15:45:27.763 [main] WARN  Main - Hello World from log4j
15:45:27.763 [main] ERROR Main - Hello World from log4j
15:45:27.764 [main] FATAL Main - Hello World from log4j

BUILD SUCCESSFUL in 1s
3 actionable tasks: 2 executed, 1 up-to-date
~/IdeaProjects/Gradle-GettingStarted 
{% endhighlight %}

## Exercises
1. Define a class Student that has a name, a surname,  a matriculation number, and a list of passed Courses. Then, write a test function that creates a Student and adds some courses to its list. Log the created Student in a log entry in JSON format using the <a target="_blank" rel="noopener noreferrer" href="https://github.com/google/gson">gson</a> library and log4j.
<div class="lesson-nav">
    <div>
        Previous: <a href="/SoftwareArchitectures_2025/gradle/testing-java-applications">Gradle - Testing Java Application</a>
    </div>
</div>