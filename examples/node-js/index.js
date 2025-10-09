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