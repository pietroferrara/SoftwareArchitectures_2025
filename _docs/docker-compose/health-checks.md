---
title: 2. Health Checks
category: 07. Docker Compose
order: 2
exclude: true
---
<h2>Contents</h2>
* toc
{:toc}
## Health Checks
In Docker Compose, services can be dependent on each other. For example, a web application that relies on a database must wait until the database is fully ready before trying to establish a connection. While Docker Compose's depends_on option ensures that one container starts after another, it does not guarantee that the service is ready to accept connections. This can lead to errors if, for example, your web application starts trying to query a database that's still initializing.

Here's where health checks come in. Health checks allow you to specify a test command that Docker will periodically run to check the health status of a container. If the service is healthy (i.e., the test passes), Docker will consider it ready. If not, Docker will continue running the test until it succeeds or until a timeout or retry limit is reached.

## Usage example
Health checks can be added to any service in your docker-compose.yml file. Let's see how to add a health check for the MySQL database to ensure that our web application doesn't start until the database is fully ready.
{% highlight yaml %}
services:
  web:
    build: .
    restart: always
    ports:
      - "8080:8080"
    depends_on:
      - students_db
  students_db:
    volumes:
      - students-db:/var/lib/mysql
    image: "mysql:5.7"
    restart: always
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=secret_password
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 3
volumes:
  students-db:
{% endhighlight %}
*test* specifies the command that Docker will run to check if the container is healthy. In this case, we use mysqladmin ping, which checks if the MySQL server is ready to accept connections. The command is executed inside the container (CMD). Docker will execute the command every interval seconds, and for every execution, wait timeout seconds to receive an answer. If the response takes longer than timeout seconds, Docker considers the health check to have failed. The*retries* parameter indicates how many times Docker will retry the health check before marking the container as unhealthy. In our case, if the check fails three times in a row, the container will be considered unhealthy.

The web service depends on the database (students_db). However, without a health check, the web service could start querying the database before it's ready. By adding the health check, Docker ensures that the web application will only attempt to connect after the MySQL database is confirmed to be ready.
<div>
Previous: <a href="/SoftwareArchitectures_2025/docker-compose/compose">Docker Compose</a> 
</div>