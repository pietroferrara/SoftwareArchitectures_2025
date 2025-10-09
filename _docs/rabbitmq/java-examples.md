---
title: 3. Java examples
category: 09. RabbitMQ
exclude: true
order: 3
---
<h2>Contents</h2>
* toc
{:toc}
Here, we will see two examples of integrating RabbitMQ: a plain Java integration and a Spring integration inside our Student's App.

## Java Example
Start the previous rabbitMQ container that we created in the previous section. 
Then, create a new Java project and add the <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/java-client.html">rabbitMQ client library</a> gradle dependency:
{% highlight kotlin %}
implementation("com.rabbitmq:amqp-client:5.20.0")
{% endhighlight %}
Now create a Publisher class:
{% highlight java %}
package org.example;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Publisher {

    public static void main(String[] args) throws IOException, TimeoutException {
        Random rng = new Random();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("TEST_JAVA_EXCHANGE", BuiltinExchangeType.DIRECT);
            channel.queueDeclare("QUEUE_YELLOW", true, false, false, null);
            channel.queueDeclare("QUEUE_BLUE", true, false, false, null);
            channel.queueBind("QUEUE_YELLOW", "TEST_JAVA_EXCHANGE", "yellow");
            channel.queueBind("QUEUE_BLUE", "TEST_JAVA_EXCHANGE", "blue");
            while(true) {
                int n = rng.nextInt(1, 4);
                String message = new Date().toString();
                String routingKey = "";
                if (n == 1) {
                    routingKey = "yellow";
                    message  = "yellow " + message;
                } else {
                    routingKey = "blue";
                    message = "blue " + message;
                }
                channel.basicPublish("TEST_JAVA_EXCHANGE", routingKey, null, message.getBytes());
                System.out.println("Sent '" + message + "'");
                Thread.sleep(5000);
            }

            }catch (Exception e) {
                System.out.println(e);
        }
    }
}
{% endhighlight %}
In this main function, we:
1. Create a connection to our RabbitMQ instance. 
2. Declare exchange, queues, and binding (declaring is an idempotent action: if the exchange (or queue, or binding) already exists in RabbitMQ, this function does nothing. Declaring before usage ensures that the entity exists).
3. Publish a message to the exchange every 5 seconds endlessly. The routing key is generated randomly.
Launch the program.
![RabbitMQ examples1]({{ site.baseurl }}/images/rabbitmq_examples_1.png)
Now, open your RabbitMQ WEB-UI and have a look at the queues:
![RabbitMQ examples2]({{ site.baseurl }}/images/rabbitmq_examples_2.png)
![RabbitMQ examples3]({{ site.baseurl }}/images/rabbitmq_examples_3.png)

We write now a new Java Class for a Consumer:
{% highlight java %}
package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ConsumerYellow {

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("TEST_JAVA_EXCHANGE", BuiltinExchangeType.DIRECT);
            channel.queueDeclare("QUEUE_YELLOW", true, false, false, null);
            channel.queueBind("QUEUE_YELLOW", "TEST_JAVA_EXCHANGE", "yellow");
            channel.basicQos(1);
            channel.basicConsume("QUEUE_YELLOW", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                {
                    String routingKey = envelope.getRoutingKey();
                    // simulate processing time
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("[" + routingKey + "] Received: " + new String(body));
                }
            });
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
{% endhighlight %}
The code should be self-explanatory: in the code, we create a consumer application that will fetch and process messages on the yellow queue.
We create also a Consumer for the Blue queue and we run all the mains (Producer, ConsumerYellow, and ConsumerBlue - we have three different processes that interact with the RabbitMQ instance). This is the code for the BlueConsumer:
{% highlight java %}
package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ConsumerBlue {

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare("TEST_JAVA_EXCHANGE", BuiltinExchangeType.DIRECT);
            channel.queueDeclare("QUEUE_BLUE", true, false, false, null);
            channel.queueBind("QUEUE_BLUE", "TEST_JAVA_EXCHANGE", "blue");
            channel.basicQos(1);
            channel.basicConsume("QUEUE_BLUE", true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                {
                    String routingKey = envelope.getRoutingKey();
                    // simulate processing time
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("[" + routingKey + "] Received: " + new String(body));
                }
            });
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
{% endhighlight %}
![RabbitMQ examples4]({{ site.baseurl }}/images/rabbitmq_examples_4.png)
![RabbitMQ examples5]({{ site.baseurl }}/images/rabbitmq_examples_5.png)
Probably you notice that when you launch the Consumer, *ALL* the messages in the queue will be fetched. This behaviour is generated by the so-called <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/consumer-prefetch.html">Consumer pre-fetch mechanism</a> (see also <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/confirms.html#channel-qos-prefetch">this</a>). You can change this by setting limits on how many messages a consumer can fetch at once: before calling channel.basicConsumer, just add:
{% highlight java %}
channel.basicQos(5); // Max. five messages at once
{% endhighlight %}
## Spring Example
Now turn back on our Students App. Let's say that we want to integrate a service on your application and you want to use RabbitMQ. We need to send newly created Students to a service that does something (for example logs the students or whatewer you want).
Spring provides a RabbitMQ Component integration:
### gradle dependencies
Add **spring-boot-starter** to the dependencies of our Students App:
{% highlight kotlin %}
implementation("org.springframework.boot:spring-boot-starter-amqp")
{% endhighlight %}
### application.properties
{% highlight bash %}
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
{% endhighlight %}
### compose.yaml
{% highlight yaml %}
services:
  web:
    build: .
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - students_db
      - studentsftp
      - students_rabbitmq
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://students_db:3306/studentsapp?createDatabaseIfNotExist=true
      - CAMEL_FTP_URI=ftp://studentsapp@studentsftp:21/students/export?password=stUd3nts@pp&passiveMode=true
      - SPRING_RABBITMQ_HOST=students_rabbitmq
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
  students_rabbitmq:
    image: "rabbitmq:3.12.7-management"
    ports:
      - "5672:5672"
      - "15672:15672" # WEB UI
volumes:
  students-db:
  students-ftp:
{% endhighlight %}
### Code
Create a Java package named **amqp** and write this Java class:
{% highlight java %}
package com.example.studentsapp.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConf {

    public static String POST_STUDENTS_ROUTING_KEY = "post-students";
    public static String POST_STUDENTS_QUEUE = "post-students";
    public static String STUDENTS_TOPIC_EXCHANGE = "students-exchange";

    @Bean
    public Queue postStudentsQueue() {
        return new Queue(POST_STUDENTS_QUEUE);
    }

    @Bean
    TopicExchange studentsExchange() {
        return new TopicExchange(STUDENTS_TOPIC_EXCHANGE);
    }

    @Bean
    Binding bindingPostStudentsExchange(Queue postStudentsQueue, TopicExchange studentsExchange) {
        return BindingBuilder.bind(postStudentsQueue).to(studentsExchange).with(POST_STUDENTS_ROUTING_KEY);
    }
}
{% endhighlight %}
This class will be loaded automatically by Spring during the application bootstrap and defines the exchange, the queue, and the binding that our application will use.
Then, on our student service, let's publish the Student on the RabbitMQ instance: note the Autowired <a target="_blank" rel="noopener noreferrer" href="https://docs.spring.io/spring-amqp/api/org/springframework/amqp/rabbit/core/RabbitTemplate.html">RabbitTemplate</a>, automatically created (using our AMQPConf Configuration) and inject by Spring.

{% highlight java %}
package com.example.studentsapp.services;

import com.example.studentsapp.amqp.AMQPConf;
import com.example.studentsapp.models.AppStudent;
import com.example.studentsapp.models.Student;
import com.example.studentsapp.repositories.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AppStudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


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

    public void add(Student student) throws Exception {
        studentRepository.create(student);
        rabbitTemplate.convertAndSend(AMQPConf.STUDENTS_TOPIC_EXCHANGE, AMQPConf.POST_STUDENTS_ROUTING_KEY, new ObjectMapper().writeValueAsString(student));
    }
    
}
{% endhighlight %}
Launch the containers with docker-compose (remember to build the application and shutdown the RabbitMQ container created previously), then create a new student: you should see a new message in your RabbitMQ instance.
![RabbitMQ examples7]({{ site.baseurl }}/images/rabbitmq_examples_7.png)
![RabbitMQ examples8]({{ site.baseurl }}/images/rabbitmq_examples_8.png)
![RabbitMQ examples6]({{ site.baseurl }}/images/rabbitmq_examples_6.png)
## Exercises
1. Write a consumer that processes queue **post-students**. You can use whatever language you want. Write a Docker image for this consumer application, and add it as a service to the Docker Compose. If you want to experiment with other languages rather than Java, you can use for example <a target="_blank" rel="noopener noreferrer" href="https://pika.readthedocs.io/en/stable/">pika</a> for Python, <a target="_blank" rel="noopener noreferrer" href="https://www.npmjs.com/package/amqplib">amqplib</a> for NodeJS, <a target="_blank" rel="noopener noreferrer" href="https://github.com/rabbitmq/amqp091-go">amqp091-go</a> for GoLang.
<div>
Previous: <a href="/SoftwareArchitectures_2025/rabbitmq/quick-start">RabbitMQ - Quick Start</a> 
</div>