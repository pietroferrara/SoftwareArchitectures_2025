---
title: 2. React
category: 11. Node JS and React
exclude: true
order: 2
---
![NodeJS logo]({{ site.baseurl }}/images/react_logo.svg)
<h2>Contents</h2>
* toc
{:toc}
## Introduction
React is a popular JavaScript library for building user interfaces, particularly single-page applications where users interact dynamically with the page. Developed by Meta, React emphasizes component-based architecture and the ability to manage application state using a virtual DOM efficiently. Let's build a React application that communicates with the /students endpoint in our Node.js backend.
## Getting Started
First, create a new project:
{% highlight bash %}
# Navigate to the same parent folder as the backend project
cd ..
npx create-react-app react-app
cd react-app
npm start # this will start the react front-end
{% endhighlight %}
The server listens at port 3000 by default. You can change the default port by creating a .env file with the directive PORT=portnumber.

Let's now connect with the nodejs backend.

## Connect with nodejs backend.
Our nodejs backend is listening in port 3000. So, we must (i) change the backend port or (ii) the front-end port. Let's suppose that we had changed the front-end port.

Edit the App.js file:
{% highlight bash %}
import React, { useEffect, useState } from 'react';
import axios from 'axios'; // Import Axios for making HTTP requests

function App() {
 const [student, setStudent] = useState(null);

 // Fetch data from the backend
 useEffect(() => {
 axios.get('http://127.0.0.1:3000/students')
 .then(response => {
 setStudent(response.data);
 })
 .catch(error => {
 console.error('Error fetching student data:', error);
 });
 }, []);

 return (
 <div style={{ textAlign: 'center', marginTop: '50px' }}>
 <h1>Student Information</h1>
 {student ? (
 <div>
 <p><strong>ID:</strong> {student.id}</p>
 <p><strong>Name:</strong> {student.name}</p>
 <p><strong>Surname:</strong> {student.surname}</p>
 </div>
 ) : (
 <p>Loading...</p>
 )}
 </div>
 );
}

export default App;
{% endhighlight %}
Before going further, let's explain the concept of State in React.
The state is like a “memory” for your React component. In technical terms, the state allows React components to hold data that can change over time. React automatically updates the screen when the state changes to reflect the new data.

React components are like living entities—they mount, update, and eventually unmount. When these phases happen, we sometimes need to do something like fetching data, starting a timer, or cleaning up resources.

This is where the useEffect hook comes in. It allows you to perform “side effects” in your component. useEffect runs a piece of code after the component has been rendered. Think of it as React saying, “Okay, the screen is ready; now I’ll do the extra work.”.
Inside the effect, we call the backend (http://127.0.0.1:3000/students) using Axios and update the student state with the fetched data.
When the student state is updated with setStudent, React automatically re-renders the component to display the new data.

For more information about React, <a href="https://react.dev/learn">here</a> is the official documentation, with examples and tutorials.

## Connect with the Spring Students app
If we want to connect the backend with our Students app, the process is straightforward. First, let's containerize our nodejs and react applications:
{% highlight bash %}
## Dockerfile for node-js
FROM node:20

WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["node", "index.js"]
{% endhighlight %}
{% highlight bash %}
## Dockerfile for react
FROM node:20
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
COPY . .
# build the app for production
RUN npm run build 
FROM nginx:stable-alpine
COPY --from=0 /usr/src/app/build /usr/share/nginx/html

# Expose the port React will run on
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
{% endhighlight %}
And creates a docker-compose. Suppose that our project is structured as follows:
{% highlight bash %}
 /examples: # root folder
 - node-js # folder for the nodejs backend app
 - react-app # folder to the react front-end app
 - Spring # folder that contains our Spring app
 - compose.yaml
{% endhighlight %}
The compose.yaml defines services for the backend, the front-end, the spring application and the database.
{% highlight bash %}
services:
 backend:
 build:
 context: ./node-js
 ports:
 - "3000:3000"
 depends_on:
 - students
 command: node index.js
 container_name: nodejs-app
 frontend:
 build:
 context: ./react-app
 ports:
 - "8080:80"
 depends_on:
 - backend
 container_name: react-app
 students:
 build:
 context: "./Spring"
 restart: always
 ports:
 - "8888:8888"
 depends_on:
 - students_db
 environment:
 - SPRING_DATASOURCE_URL=jdbc:mysql://students_db:3306/studentsapp?createDatabaseIfNotExist=true
 container_name: spring-app
 students_db:
 volumes:
 - students-db:/var/lib/mysql
 image: "mysql:5.7"
 restart: always
 ports:
 - "3306:3306"
 environment:
 - MYSQL_ROOT_PASSWORD=secret_password
volumes:
 students-db:
{% endhighlight %}
Then, change the code on the nodejs application to retrieve students by calling the Spring application:
{% highlight javascript %}
const express = require('express');
const axios = require('axios');  // Import Axios for HTTP requests
const cors = require('cors');

const app = express();
app.use(cors());
// Define the /student route
app.get('/students', async (req, res) => {
 const response = await axios.get('http://spring-app:8888/students');
 console.log(response.data)
 res.status(200).json(response.data);
});

// Set up the server to listen on a specific port and hostname
const hostname = '0.0.0.0';
const port = 3000;

app.listen(port, hostname, () => {
 console.log(`Server running at http://${hostname}:${port}/`);
});
{% endhighlight %}

Since we are expecting an array of students, let's also change the react app to handle this:

{% highlight javascript %}
import React, { useEffect, useState } from 'react';
import axios from 'axios'; // Import Axios for making HTTP requests

function App() {
 const [students, setStudents] = useState([]);

 // Fetch data from the backend
 useEffect(() => {
 axios.get('http://127.0.0.1:3000/students')
 .then(response => {
 setStudents(response.data);
 })
 .catch(error => {
 console.error('Error fetching student data:', error);
 });
 }, []);

 return (
 <div style={{ textAlign: 'center', marginTop: '50px' }}>
 <h1>Student Information</h1>
 {students.length > 0 ? (
 <div>
 {students.map((student, index) => (
 <div key={index} style={{ marginBottom: '20px' }}>
 <p><strong>ID:</strong> {student.id} <strong>Name:</strong> {student.name} <strong>Surname:</strong> {student.surname}</p>
 </div>
 ))}
 </div>
 ) : (
 <p>Loading...</p>
 )}
 </div>
 );
}

export default App;
{% endhighlight %}
As exercises, you can try to implement the next functionalities:
1. Insertion of a Student (by using a <a href="https://react.dev/reference/react-dom/components/form">form</a>).
2. Deletion of a Student.
3. Edit of a Student.  

You can also have a look at <a href="https://reactrouter.com/">ReactJS Router</a>, a library that adds routing functionality to React.

<div class="lesson-nav">
    <div>
    Previous: <a href="/SoftwareArchitectures_2025/node-js-react/node-js">NodeJS</a>  
    </div>
</div>