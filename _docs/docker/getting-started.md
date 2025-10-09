---
title: 2. Getting Started
category: 01. Docker
order: 2
---
<h2>Contents</h2>
* toc
{:toc}
In this section, we will see step-by-step how we can create images and initialize containers. To follow properly, you should have <a target="_blank" rel="noopener noreferrer" href="../introduction">Docker Desktop</a> (or at least the Docker Engine if you want to do it from the CLI) installed properly on your machine.

## The Docker Hub
We want now to play around with Docker to understand better how it works. We will use, for now, the GUI client of Docker. The easier way to see Docker in action is to download pre-defined images from the Docker Hub.  

The <a target="_blank" rel="noopener noreferrer" href="https://hub.docker.com/">Docker Hub</a> is a container image registry. From Docker Hub, you can fetch ready-to-use images from which you can base your application. Do you want to deploy in a container an application written in Python? You can download a <a target="_blank" rel="noopener noreferrer" href="https://hub.docker.com/_/python">Docker image</a> that has python already installed and configured.

## Image tags
A docker image has a tag. This permits you to specify which version of the Docker image you want to use. For example, suppose that your python application relies on libraries that were developed with python 3.9. Probably you don't want to use the latest version of python, since the library could not work properly on python >= 3.9. So, you should use a python 3.9 Docker image. You can see the available tags of an image directly from the image page of the Docker Hub:
![Docker container]({{ site.baseurl }}/images/docker_hub_tags.png)


## Getting Started
From Docker Desktop, click on the search input field and type "**docker/getting-started**":
![Docker Getting Started 1]({{ site.baseurl }}/images/docker_gettingstarted_1.png)
On the search results pop-up, click on "Pull" on the first result:
![Docker Getting Started 2]({{ site.baseurl }}/images/docker_gettingstarted_2.png)
This will download a Docker Image that contains a simple tutorial. The download requires a bit, and at the end, you should find the getting-started images inside the images tab:
![Docker Getting Started 3]({{ site.baseurl }}/images/docker_gettingstarted_3.png)
Now click on the "Play" button of the image. A pop-up that permits setting up the container should appear. From here, you can set a container name, a host port, volumes, and environment variables. Insert only a Host port (for example, 8080) and a name (the latter is facultative), and click on "Run". This will create and run a container from the getting-started image. You can find the newly created container on the Container tab of Docker Desktop:
![Docker Getting Started 4]({{ site.baseurl }}/images/docker_gettingstarted_4.png)
To see the deployed application in action, go on <a target="_blank" rel="noopener noreferrer" href="localhost:8080">localhost:8080</a> (or, if you choose a different host port, substitute the port on the URL). If you see something like this, it means that you have launched your first container correctly :) :
![Docker Getting Started 5  ]({{ site.baseurl }}/images/docker_gettingstarted_5.png)
We will turn back on this later. Now it is time to see how we can interact with the container with Docker Desktop.

From the container tab, you can stop, pause, restart, and delete containers. If you click on the container name, you can find a simple interface that permits you to see the container logs and some statistics (like, for example, CPU and memory usage), access the container's files, and interact with the container using a terminal:
![Docker Getting Started 6]({{ site.baseurl }}/images/docker_gettingstarted_6.png)
![Docker Getting Started 7]({{ site.baseurl }}/images/docker_gettingstarted_7.png)
![Docker Getting Started 8]({{ site.baseurl }}/images/docker_gettingstarted_8.png)

## Exercises
1. Try to **install python3** inside the container created on the Getting Started section (```apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python```) and launch a python minimal hello world from the container. 
2. Experiment with environmental variables. Create a new container from the getting-started image with an environment variable. Install python also on this container (as we said in the previous lessons, the edits we do in a container remain in the container layer - parent image layers are read-only) and try to print out the environment variable from a python script (see <a target="_blank" rel="noopener noreferrer" href="https://docs.python.org/3/library/os.html">the os python standard module</a>).  
<div class="lesson-nav">
    <div>
    Previous: <a href="/SoftwareArchitectures_2025/docker/introduction">Docker - Introduction</a>
    </div>
    <div>
    Next: <a href="/SoftwareArchitectures_2025/docker/cli-and-dockerfile">Docker - CLI and Dockerfile</a>  
    </div>
</div>