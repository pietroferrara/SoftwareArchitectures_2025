---
title: 1. NodeJS
category: 11. Node JS and React
exclude: true
order: 1
---
![NodeJS logo]({{ site.baseurl }}/images/nodejs_logo.svg)
<h2>Contents</h2>
* toc
{:toc}
## Introduction

Let's create a <a href="https://nodejs.org/en">NodeJS</a> Server that interacts with our REST API and shows a front-end.  
Node.js, often called Node, is an open-source, cross-platform runtime environment that allows developers to run JavaScript code on the server side. It enables the creation of scalable and high-performance applications, particularly web servers, that can handle numerous simultaneous connections with high throughput.

## Install NodeJS
Execute the next commands in your terminal to install NodeJS (alternative, you can use the official <a href="https://nodejs.org/en/download/prebuilt-installer">installer</a>, or use a Docker image).
{% highlight bash %}
# installs nvm (Node Version Manager)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.0/install.sh | bash
# download and install Node.js (you may need to restart the terminal)
nvm install 20
# verifies the right Node.js version is in the environment
node -v # should print `v20.17.0 (or above)`
# verifies the right npm version is in the environment
npm -v # should print `10.8.2 (or above)`
{% endhighlight %}
- **nvm** stands for Node Version Manager and is a tool used to manage multiple versions of Node.js on a single machine. It allows developers to easily switch between different versions of Node.js, which is particularly useful when working on projects requiring different versions of Node.js or when testing code compatibility with different versions. With the above code snippet, we downloaded and ran the nvm installer. Then, with nvm, we installed NodeJS v20.x.
- **npm** stands for Node Package Manager. It is a powerful tool that helps developers install, share, and manage JavaScript packages and dependencies in their projects. npm is included with Node.js, so when you install Node.js, npm is also installed automatically. It is like **pip** for Python.

### Create a new Node project
To create a new Node project, make a new folder, and then run **npm init** inside that folder:
{% highlight bash %}
➜  ~ mkdir nodejs-proj
➜  ~ cd nodejs-proj 
➜  nodejs-proj npm init
This utility will walk you through the process of creating a package.json file.
It only covers the most common items and tries to guess sensible defaults.

See `npm help init` for definitive documentation on these fields
and exactly what they do.

Use `npm install <pkg>` afterward to install a package and
save it as a dependency in the package.json file.

Press ^C at any time to quit.
package name: (nodejs-proj) 
version: (1.0.0) 
description: 
entry point: (index.js) 
test command: 
git repository: 
keywords: 
author: 
license: (ISC) 
About to write to /Users/giacomozanatta/nodejs-proj/package.json:

{
 "name": "nodejs-proj",
 "version": "1.0.0",
 "main": "index.js",
 "scripts": {
 "test": "echo \"Error: no test specified\" && exit 1"
 },
 "author": "",
 "license": "ISC",
 "description": ""
}


Is this OK? (yes) 

npm notice
npm notice New patch version of npm available! 10.8.2 -> 10.8.3
npm notice Changelog: https://github.com/npm/cli/releases/tag/v10.8.3
npm notice To update run: npm install -g npm@10.8.3
npm notice
➜  nodejs-proj 
{% endhighlight %}
This command will create a json file containing project-related metadata information, like name, version, and the entry point.
Now, we are going to create a simple Hello World application. Create a new file index.js and copy-paste this code:
{% highlight javascript %}
const http = require('http');

const hostname = '127.0.0.1';
const port = 3000;

const server = http.createServer((req, res) => {
 res.statusCode = 200;
 res.setHeader('Content-Type', 'text/plain');
 res.end('Hello, World!\n');
});

server.listen(port, hostname, () => {
 console.log(`Server running at http://${hostname}:${port}/`);
});
{% endhighlight %}
The semantics of this code are straightforward: we import the http module, a built-in Node.js module used to create HTTP servers and handle HTTP requests and responses. We define the hostname and port, create the server, and then start the server (server.listen).
### Launch the server
Just execute:
{% highlight bash %}
➜  nodejs-proj node index.js    
Server running at http://127.0.0.1:3000/
{% endhighlight %}
![NodeJS intro 1]({{ site.baseurl }}/images/nodejs_intro_1.png)
### Install a node package
To install a node package, we use npm. Since we want to call our REST API, we install Axios. Axios is an HTTP client API library that is easily the craft of HTTP calls.
{% highlight bash %}
➜  nodejs-proj npm install axios
{% endhighlight %}
After executing this command, you should see a change in the package.json and a new node-modules folder.
The node-modules folder contains all your application's external dependencies. In package.json, a dependency entry appears. **packages** specify the external packages (or modules) that a Node.js project requires to function correctly. 
{% highlight json %}
 "dependencies": {
 "axios": "^1.7.5"
 }
{% endhighlight %}
When you clone a Node.js project repository from a version control system like Git, you must install all the dependencies required for the project to run properly. You can do it by launching the command:
{% highlight bash %}
npm install
{% endhighlight %}
We use Express to build our web server. Express is a fast, minimalist web framework for Node.js that simplifies the process of building web applications and APIs. It provides a robust set of features and tools that make it easier to manage routes, handle HTTP requests, handle middleware, and more, all while offering great flexibility and scalability.
To install express:
{% highlight bash %}
➜  nodejs-proj npm install express
{% endhighlight %}
### Quickstart
Let's create a simple GET /students page:
{% highlight javascript %}
const express = require('express');
const axios = require('axios');  // Import Axios for HTTP requests

const app = express();

// Define the /student route
app.get('/students', async (req, res) => {
 res.status(200).json({id: 123, name: "Name", surname: "Surname"});
});

// Set up the server to listen on a specific port and hostname
const hostname = '127.0.0.1';
const port = 3000;

app.listen(port, hostname, () => {
 console.log(`Server running at http://${hostname}:${port}/`);
});
{% endhighlight %}
<div class="lesson-nav">
    <div>
    Next: <a href="/SoftwareArchitectures_2025/node-js-react/react">React</a>  
    </div>
</div>