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