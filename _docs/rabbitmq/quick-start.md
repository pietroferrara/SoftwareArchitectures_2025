---
title: 2. Quick Start
category: 09. RabbitMQ
exclude: true
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
Let's start by creating a docker image from a rabbitmq image:
{% highlight bash %}
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
{% endhighlight %}
Add the -d flag if you want to run the container in detached mode.
We are binding two ports: one for the rabbitmq service (5672), and one for a web-ui (15672). In this section, we will play a bit only with the web-ui, to better understand how the system works.

## The RabbitMQ web-UI
Open your browser at <a target="_blank" rel="noopener noreferrer" href="localhost:15672">localhost:15672</a>. You should see something like this:
![RabbitMQ gettingstarted1]({{ site.baseurl }}/images/rabbitmq_gettingstarted_1.png)
Insert **guest** as username and **guest** as password, and log in.
![RabbitMQ gettingstarted2]({{ site.baseurl }}/images/rabbitmq_gettingstarted_2.png)
From the web interface, we can monitor the rabbitmq instance and see, for example, how many messages are present, the memory footprint of the instance, some statistics, and the messages in a queue. We can also create exchanges, queues, and bindings: let's create a direct exchange to see how it works.
### Create an exchange
Go to the exchange tab. From here, you can see a list of exchanges (rabbitmq adds some pre-defined exchanges). We can create a new exchange:
![RabbitMQ gettingstarted3]({{ site.baseurl }}/images/rabbitmq_gettingstarted_3.png)
Set **type = direct**, **name = test_exchange** and click **Add exchange**.

### Create a queue
We need also the create a queue: for our case, we create two durable queues: **test_queue_1** and **test_queue_2**. To do that, go to the tab **Queues and Streams** and fill out the form:
![RabbitMQ gettingstarted4]({{ site.baseurl }}/images/rabbitmq_gettingstarted_4.png)
### Binding queues to exchanges
What remains to do is to bind the queues to the exchanges. Let's go on **Exchanges** and click on our exchange from the list. Then, create the bindings: 
![RabbitMQ gettingstarted5]({{ site.baseurl }}/images/rabbitmq_gettingstarted_5.png)
![RabbitMQ gettingstarted6]({{ site.baseurl }}/images/rabbitmq_gettingstarted_6.png)
These bindings say that all the messages published to **test_exchange** with routing key **yellow** go on **test_queue_1**, and all the messages published to **test_exchange** with routing key **blue** go on **test_queue_2**. Remember that if the routing key of a message does not match any of the bindings, that message will be discarded!
### Publish some messages
Now we publish some messages to the exchange to see the behavior. Create and publish two messages: one with routing key **yellow** and one with routing key **blue**. 
![RabbitMQ gettingstarted7]({{ site.baseurl }}/images/rabbitmq_gettingstarted_7.png)
![RabbitMQ gettingstarted8]({{ site.baseurl }}/images/rabbitmq_gettingstarted_8.png)
What happens if we publish a message with routing key **red**? 
![RabbitMQ gettingstarted9]({{ site.baseurl }}/images/rabbitmq_gettingstarted_9.png)
Lost forever!

### Get messages
To see messages in a queue, go to the queue and click on **Get Message(s)**: on queue **test_queue_1**, you should see only the yellow message. On queue **test_queue_2**, you should see only the blue message.
![RabbitMQ gettingstarted10]({{ site.baseurl }}/images/rabbitmq_gettingstarted_10.png)
What happens if we publish a message with routing key **red**? 
![RabbitMQ gettingstarted11]({{ site.baseurl }}/images/rabbitmq_gettingstarted_11.png)
#### About ACK
Messages can be <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/confirms.html">acknowledged</a>. 
![RabbitMQ gettingstarted12]({{ site.baseurl }}/images/rabbitmq_gettingstarted_12.png)
ACK is a mechanism to ensure that a message is successfully sent. Acknowledgment can be positive (ACK) or negative (NACK). Positive acknowledgments tell the RabbitMQ instance to consider the message as delivered, while a negative acknowledgment says that the message had some problems during consuming and requires specific action (for example, it can be requested or sent to a <a target="_blank" rel="noopener noreferrer" href="https://www.rabbitmq.com/dlx.html">Dead Letter Exchange</a>).

<div>
Previous: <a href="/SoftwareArchitectures_2025/rabbitmq/introduction">RabbitMQ - Introduction</a> 
</div>
<div>
Next: <a href="/SoftwareArchitectures_2025/rabbitmq/java-examples">RabbitMQ - Java Examples</a> 
</div>