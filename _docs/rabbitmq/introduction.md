---
title: 1. Introduction
category: 09. RabbitMQ
exclude: true
order: 1
---
![RabbitMQ logo]({{ site.baseurl }}/images/rabbitmq_logo.png)
<h2>Contents</h2>
* toc
{:toc}
<iframe width="100%" height="315" src="https://www.youtube.com/embed/TgBm_bdzGg8?si=O4EP-CMjjiuiaPHH" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
## What is RabbitMQ
<a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com">RabbitMQ</a> is an open-source message-broker software that implements various message-passing protocols such as Advance Message Queuing Protocol (AMQP), Streaming Text Oriented Messaging Protocol (STOMP), MQ Telemetry Transport (MQTT). It is one of the most popular open-source message brokers.  

### The AMQP Protocol
AMQP is an open standard protocol for message-oriented middleware.  
RabbitMQ was originally developed to support AMQP version 9-0-1 (that is an early and different version of the standard AMQP 1.0 specification), however, AMQP 1.0 is supported via a plug-in.  

### The AMQP model
There are three main types of components:
- **Exchange**: receives message from publisher applications and route these to message queues, based on arbitrary criteria.
- **Message queue**: stores messages until they can safely processed by a consuming client application
- **Binding**: defines the relationship between a message queue and an exchange and provides the message routing criteria.
Publishers publish messages in an Exchange. The exchange then takes its messages and routes them into zero or more queues. The routing algorithm used depends on the exchange type and binding rules. 
![RabbitMQ intro1]({{ site.baseurl }}/images/rabbitmq_intro_1.png)
## Exchange types
An exchange could be:
- **Direct**: A direct exchange delivers messages to queues based on the message routing key: a queue binds to the exchange with a routing key *K*; when a message with routing key R arrives at the direct exchange, the exchange will route it to the queue if K=R (or to all the queues for which K = R).
![RabbitMQ intro2]({{ site.baseurl }}/images/rabbitmq_intro_2.png)
Consider the example depicted in the image: all the messages published in the *images*  exchange that have routing key *images.archive* flow in queues *archiver1* and *archiver2*. All the messages that have routing key *images.crop* go in queue *cropper*. All the messages that have routing key *images.resize* flow in queue *resizer*.
- **Fanout**: A fanout exchange routes messages to all of the queues that are bound to it (the routing key is ignored). This type of exchange is ideal for broadcasting messages.
![RabbitMQ intro3]({{ site.baseurl }}/images/rabbitmq_intro_3.png)
- **Topic**: A topic exchanges route messages to one or many queues based on matching between a message routing key and the pattern that was used to bind a queue to an exchange. Messages sent to a topic exchange must have the routing key defined as a list of words, delimited by dots. Binding keys follow the same mechanism, but in addition, can have stars (that can substitute exactly one word) or hashes (that can substitute zero or more words). To better understand the topic exchange, consider this example: we have a routing key consisting of three words, "**celerity.colour.species**". Messages describe animals, and the words of the message routing key define some characteristics of the animal: the celerity (quick, lazy), the colour (orange, pink, ...), and the specie (rabbit, elephant, fox, ...). We have two Queues, Q1 and Q2. Q1 is bounded to the exchange with binding key **\*.orange.\***, and Q2 with **\*.\*.rabbit** and **lazy.#**. 
These bindings mean that in queue Q1 goes all the orange animals (so, for example, messages with routing key **quick.orange.rabbit**, or **lazy.orange.fox**, or **rapid.orange.dog**) and in queue Q2 goes all messages regarding rabbits and all lazy animals (e.g., **lazy.white.dog**, **fast.pink.rabbit**, **lazy.orange.rabbit** (this last message is delivered to both queues)). If a message's routing key does not match any binding will be lost and discarded (i.e. not delivered to any queue).  

![RabbitMQ intro4]({{ site.baseurl }}/images/rabbitmq_intro_4.png){: width="100%" }
- **Headers**: Headers Exchange permits the routing of multiple attributes taken from the headers of the message. In this case, the routing key is ignored.

Generally, exchanges have some attributes:
- **Name**
- **Durability**: permits to say if exchanges survive broker restart, or not.
- **Auto delete**: tells if the exchange needs to be deleted when the last queue is unbounded from it, or not.
- **Arguments**: an optional attribute that permits the definition of custom attributes for a plugin.

More details <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/tutorials/amqp-concepts.html">here</a>.
## Queues
In RabbitMQ, a queue is an ordered collection of messages. Messages are enqueued and dequeued in a FIFO (First In, First Out) manner. However, the ordering of messages can be affected by message priorities or requeueing. Queues, like exchanges, have attributes:
- **Name**
- **Durability**: queue survives if broker restarts.
- **Exclusive**: tells if the queue can be used just by only one connection (the queue will be removed when that connection closes).
- **Auto delete**: queue that has had at least one consumer is deleted when the last consumer unsubscribes.
- **Arguments**: like exchange.
Other information about queues can be found <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/queues.html">here</a>.  

In the next section, we will start a "dockerized" instance of RabbitMQ to see how it works.
<div>
Next: <a href="/SoftwareArchitectures_2025/rabbitmq/quick-start">RabbitMQ - Quickstart</a> 
</div>