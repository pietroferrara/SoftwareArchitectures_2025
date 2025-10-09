---
title: 2. Getting Started
category: 10. Kubernetes
exclude: true
order: 2
---

<h2>Contents</h2>
* toc
{:toc}

### Minkube
There exist different tools that permit the deployment of Kubernetes clusters. For this tutorial, we use <a target="_blank" rel="noopener noreferrer" href="https://minikube.sigs.k8s.io/docs/">minikube</a>, for its simplicity and lightweightness. Minikube is a Kubernetes implementation that creates a Virtual Machine on your local machine and deploys a single cluster containing only one node. Minikube is used for development purposes, and it discourages usage in a production environment. However, for our goal (i.e., deploy something on Kubernetes, and see this technology in action), this is enough. If you want to use Kubernetes in a production environment, you can use <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/setup/production-environment/tools/kops/">kops</a>, <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/setup/production-environment/tools/kubespray/">kubespray</a>, <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/">kubeadm</a>, or <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/setup/production-environment/turnkey-solutions/">provider-specific solutions</a>.
#### Installation
Minikube is available for Windows, MacOS, and Linux. We will use Docker as the main driver for Minikube,
To install Minikube, follow the first step of <a target="_blank" rel="noopener noreferrer" href="https://minikube.sigs.k8s.io/docs/start/">this page</a>.
Then, we set the <a target="_blank" rel="noopener noreferrer" href="https://minikube.sigs.k8s.io/docs/drivers/docker/">docker driver</a> as the default driver:
{% highlight bash %}
minikube config set driver docker # make sure that docker is currently running!
{% endhighlight %}

#### Start minikube
You can start minikube by typing:
{% highlight bash %}
minikube start
{% endhighlight %}
This will launch a containerized minikube cluster:
![Kubernetes tuto1]({{ site.baseurl }}/images/kubernetes_tuto_1.png){: width="100%" }
### Kubectl
The command-line tool that permits to interact with a Kubernetes cluster's control pane is <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/reference/kubectl/">**kubectl**</a>.
#### kubectl commands
1. Get a list of all the nodes in the cluster:
{% highlight bash %}
kubectl get nodes
NAME       STATUS   ROLES           AGE   VERSION
minikube   Ready    control-plane   16s   v1.27.4
{% endhighlight %}
In our case, we have only one node (minikube) that acts as a control-pane node.
2. Create Pods / Deployments
A Pod is the smallest unit of the Kubernetes cluster. In practice, there is an abstraction over pods, called deployment. A deployment provides declarative updates to applications, allowing the developer to describe the application's life cycle, which image to use for the app, the number of dedicated pods, and the way in which they should be updated. To create a deployment, use this command:
{% highlight bash %}
kubectl create deployment NAME --image=image [--dry-run] [options]
{% endhighlight %}
For example:
{% highlight bash %}
kubectl create deployment nginx-k8s --image=nginx
deployment.apps/nginx-k8s created
{% endhighlight %}
3. List deployments
You can get a list of all the available deployments with:
{% highlight bash %}
kubectl get deployments                          
NAME        READY   UP-TO-DATE   AVAILABLE   AGE
nginx-k8s   1/1     1            1           49s
{% endhighlight %}
4. List pods
And we can see that our deployment has created a pod:
{% highlight bash %}
kubectl get pod
NAME                        READY   STATUS    RESTARTS   AGE
nginx-k8s-686f5f78d-cmp42   1/1     Running   0          69s
{% endhighlight %}
Deployment can be seen as blueprints for creating pods.
4. List all the commands:
{% highlight bash %}
kubectl help
{% endhighlight %}
We will see other commands in the following section.

### Getting started with Kubernetes
Let's try now to create a deployment from scratch. We want to run MongoDB and expose the mongo-express admin interface.
To do so, we can define the application in a .yaml file:
{% highlight yaml %}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongodb
  labels:
    app: mongodb
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mongodb
  template:
    metadata:
      labels:
        app: mongodb
    spec:
      containers:
      - name: mongodb
        image: mongo
        ports:
        - containerPort: 27017
        env:
        - name: MONGO_INITDB_ROOT_USERNAME
          value: username
        - name: MONGO_INITDB_ROOT_PASSWORD
          value: secret_pwd
---
apiVersion: v1
kind: Service
metadata:
  name: mongodb-service
spec:
  selector:
    app: mongodb
  ports:
    - protocol: TCP
      port: 27017
      targetPort: 27017
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mongo-express
  labels:
    app: mongo-express
spec:
  replicas: 1
  selector:
    matchLabels:
      app: mongo-express
  template:
    metadata:
      labels:
        app: mongo-express
    spec:
      containers:
      - name: mongo-express
        image: mongo-express
        ports:
        - containerPort: 8081
        env:
        - name: ME_CONFIG_MONGODB_ADMINUSERNAME
          value: username
        - name: ME_CONFIG_MONGODB_ADMINPASSWORD
          value: secret_pwd
        - name: ME_CONFIG_MONGODB_SERVER 
          value: mongodb-service
---
apiVersion: v1
kind: Service
metadata:
  name: mongo-express-service
spec:
  selector:
    app: mongo-express
  type: LoadBalancer  
  ports:
    - protocol: TCP
      port: 8081
      targetPort: 8081
      nodePort: 30000
{% endhighlight %}
As you can see, it is very similar to a docker-compose .yaml file. In this .yaml, we create various Kubernetes objects, delimited by **---**.  
The first object defines the deployment of the mongodb instance, plus some configurations like environment variables and container ports. The **replicas** entry specifies the number of desired pods for this specific deployment. Increasing the number of pods permits scaling up the application.  
Then we have a service for mongodb. Service permits to expose an application that is running as one or more pods in the cluster.  
We define also the deployment and the service of the mongo express instance. Here, the service is of type <a target="_blank" rel="noopener noreferrer" href="https://kubernetes.io/docs/concepts/services-networking/service/#loadbalancer">LoadBalancer</a>: this type of service behaves as an external service, and the application is available also outside the cluster.
We can apply the configuration with:
{% highlight bash %}
kubectl apply -f . # The .yaml file created must be in the working dir
{% endhighlight %}
With minikube, you can expose your application by launching:
{% highlight bash %}
minikube service mongo-express-service
{% endhighlight %}
Insert **username=admin** and **password=pass**:
![Kubernetes tuto2]({{ site.baseurl }}/images/kubernetes_tuto_2.png){: width="100%" }
<div>
Previous: <a href="/SoftwareArchitectures_2025/kubernetes/introduction">Kubernetes - Introduction</a>
</div>