---
title: 1. Introduction 
category: 01. Docker
order: 1
---
![Docker logo]({{ site.baseurl }}/images/docker_logo.jpeg)
<h2>Contents</h2>
* toc
{:toc}
<center><iframe width="100%" height="315" src="https://www.youtube.com/embed/2xZNUCwUpsU?si=VFF-zZUAiBiRV932" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe></center>
## Install Docker Desktop
Download **Docker Desktop** from [here](https://www.docker.com/products/docker-desktop/). Then, run the installer and follows the onscreen instruction. This will install the Docker Engine, which is the core of the Docker platform, and an easy-to-use Graphical User Interface (GUI), i.e. Docker Desktop.
![Docker Desktop]({{ site.baseurl }}/images/docker_desktop.png)
## The Docker Engine
The Docker Engine is shipped out with a Command-Line Interface (CLI), and a server. The latter component runs as a root-privilege background process (daemon). You can interact with the server using the CLI client or Docker Desktop. The daemon creates and manages Docker objects, such as images and containers. 
Launching Docker Desktop will automatically start the underlying daemon process.

## Containers

As you may have learned from the video, Docker is a tool that permits running an application in an isolated environment, enabling the capability to separate applications from infrastructures. This environment is named **container** and can be seen as a sandboxed process running on a machine. In a single machine, you can have multiple containers, each of one containing one or more applications. According to docker documentation, "a container is a standard unit of software that packages up code and all its dependencies so the application runs quickly and reliably from one computing environment to another". 
Docker containers are:
1. Industry standard: they could be portable anywhere.
2. Lightweight: containers are not required to run a full OS per application, since all the containers share the machine's OS system kernel.
3. Secure: containers are isolated from the overall system, and this permits to running applications in containers safely.  
![Docker container]({{ site.baseurl }}/images/docker_container.webp)
Docker automates the deployment of applications into containers, providing a set of tools and mechanisms that allow its management.


## Images
A container is a runnable instance of an **image**. Images are the building part of Docker's life cycle, and they are made up of filesystems layered over each other using a union mount. Every layer of an image is mounted in read-only and is itself an image: the image below is called the parent image, whereas the final is called base image.
When we create a container from an image, Docker will add a read-write filesystem on top of all the image layers. This layer runs the application that we want to "Dockerize". When Docker first starts a container, this read-write layer is empty. Changes are applied only to this layer: for example, if the running application needs to change a file of the parent image, this file is copied into the container layer (i.e., the read-write layer), shadowing the read-only file.  
To better understand this concept, just observe the image below:
![Docker image layers]({{ site.baseurl }}/images/docker_image_layers.webp)

This figure depicts the layered structure of an image: starting from the bottom, we have a Kernel layer (bootfs): this layer, transparent to the user, exists for booting purposes. When the container has booted, this layer is unmounted, to free up the memory.

Then, we will find the Base Image: this represents the starting point of our layered image. Usually, base images are basic minimal Linux distributions, for example, Ubuntu, Redhat, Centos, Alpine, or Debian.  

The next layer is the parent image of our image: simply it adds emacs (a CLI text editor) to our base image, Debian. Our image is built on top of Debian + emacs and provides an Apache HTTP server. The last layer is the read-write layer of the container, and inside of it lives the actual app.  
<div class="lesson-nav">
    <div>
    Next: <a href="/SoftwareArchitectures_2025/docker/getting-started">Docker - Getting Started</a>
    </div>
</div>