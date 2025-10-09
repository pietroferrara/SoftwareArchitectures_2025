---
title: 1. Introduction
category: 08. Camel
exclude: true
order: 1
---
![Camel logo]({{ site.baseurl }}/images/camel_logo.png)
<h2>Contents</h2>
* toc
{:toc}
<iframe width="100%" height="315" src="https://www.youtube.com/embed/9JZQ1dp1cZ4?si=_ZDKpYcp9RUx8mr8" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
## Introduction
Complex applications rarely live in isolation. Consider for example an e-commerce system. The front-end needs to interface with a back-end, and this one must be connected to a database (that stores for example the catalogs and the users), an OMS (Order Management System) for processing customer orders, a CRM (Customer Relationship Management) and a marketing automation instance for sending transactional emails and manages promotions. Complex applications need to be integrated with other applications and integration could be challenging: we need to permit communication between distinct applications (perhaps written in different languages).  
There are different ways to achieve this: we saw the REST architectural style, which if implemented well can potentially be used to exchange information between systems (one system POST or EDIT something on another one), and we talked about databases (that can be shared among applications). Another way to integrate two systems is by using File Transfer: some applications of the overall system can produce files (stored for example in an FTP server) while others can consume and vice versa. Another approach is to use Message-Passing, a concept that we will deepen in a moment.  
Managing integrations can be very hard and Camel could help us.

## What is Camel
<a target="_blank" rel="noopener noreferrer" href="https://github.com/apache/camel/">Apache Camel</a> is a Open Source Integration Framework that permits to quickly integrate various systems by consuming or producing data. It provides a simple API that abstracts away all the internal details like, for example, the transport style or the communication. 
### Camel common use cases
Camel can be used to perform some operations, such as (but not limited to):
- File integration: it can be used to monitor file changes, processing, and move files to other locations.
- Messaging: it can be used to connect different systems asynchronously by exchanging messages.
- Data Transformation: it can be used to convert data to different formats, for example from XML to JSON or CSV.
### Messages
Integrating systems needs some sort of communication between them. In Camel, communication is provided by exchanging messages. A message flows in one direction from a sender to a receiver.  
Messages have a body (payload), headers, and optional attachments. A message has a unique ID of type String.
- **Headers and Attachments**: headers are name-value pairs. Names are unique strings while value can be any Java Object. Headers are stored as a Map within the message. Optional attachments can be present, and they are typically used for web service and email components.
- **Body**: the body is a Java Object. If the sender and receiver use different body formats, Camel can provide mechanisms to transform data into an acceptable format. In many cases, this transformation is done automatically.
The most important feature of Camel is the routing engine, which permits to moving of messages from one system to another.
### Routes
A route is a chain of processors. Routes permit to decouple clients from servers and producers to consumers. A route for example can decide dynamically what server a client will invoke, using some rules or message filtering and also add extra processing to messages. Routes are defined using Camel's DSL.
Routes can be thought of as a graph that has processors as nodes and some lines that connect different processors (different nodes). Lines connect the output of one processor to the input of another one.
Camel provides some features, such as a domain-specific language on top of regular programming languages (such as Java). In addition, you can also specify Camel semantics in XML.
## Getting Started
Let's create a new Java Project, and add Camel dependency.
{% highlight kotlin %}
implementation("org.apache.camel:camel-core:3.18.1")
{% endhighlight %}
Then, create this CamelMain file:
{% highlight java %}
package org.example;

import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.main.Main;


public class CamelMain {
    public static void main(String[] args) throws Exception {
        Main main = new Main(CamelMain.class);
        main.run();
    }

    public static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("file://input?delete=true")
            .process(exchange -> System.out.println("File name: " + exchange.getIn().getHeader("CamelFileName")))
            .to("file://output");
        }
    }
}
{% endhighlight %}
This is an example of an endless Camel application. Let's analyze what it is written by looking at the RouteBuilder.
### The Route Builder
The RouteBuilder class is a base Camel class that is derived to create routing rules using the DSL. Here, we are extending the RouteBuilder class to create a custom Rule. Camel routes are defined in the configure method, where we use Camel DSL: from defines the starting point of our route, i.e., the point from which messages are taken. The parameter passed to the "from" function is a String that defines the URI endpoint of the route. Camel provides a huge set of <a target="_blank" rel="noopener noreferrer" href="https://camel.apache.org/manual/component.html">Components</a>, each of one used to connect a wide range of technologies. For our example, we are using the <a target="_blank" rel="noopener noreferrer" href="https://camel.apache.org/components/4.0.x/file-component.html">File</a> component, which provides access to file systems and allows the processing of files. Each endpoint has a URI with the format ***"component:context-path?query"***. In our case, file://input?delete=true means that we are using the file component and that we are looking for files in /input directory. The query delete=true tells Camel to remove the file after the processing (all the available query parameters for a component are well documented in the official documentation).  
Then, we have a process method. A processor defines custom business logic that must be done for the message: in our example, we just print out the file name (obtained from the header of the message).
At the end, we have a to, which is the last endpoint in which messages flow.  
Our example simply moves all the files of the /input directory into the /output dir, logging the name.  
If you look at the static main method, probably you will notice something strange: we create a main Camel object passing our class as a costructor parameter, and then we call the run method. We did this because a Camel pipeline works in a separate thread. Using the Camel Main permits to keep running the pipeline. Now launch the application, and try to create some files in the /input folder. You should see that the file moves to the /output folder.
<div>
Next: <a href="/SoftwareArchitectures_2025/camel/example">Camel - Example</a> 
</div>