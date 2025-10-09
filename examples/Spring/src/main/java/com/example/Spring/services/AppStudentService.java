package com.example.Spring.services;

import com.example.Spring.models.AppStudent;
import com.example.Spring.models.Student;
import com.example.Spring.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AppStudentService {
    @Autowired
    StudentRepository studentRepository;


    public List<AppStudent> findAll() throws Exception {
        List<AppStudent> appStudents = new ArrayList<>();
        List<Student> studs = new ArrayList<>(studentRepository.getAll());
        // wrap Students in a List of AppStudent
        for (Student s : studs) {
            appStudents.add(new AppStudent(s));
        }
        return appStudents;
    }

    public AppStudent fetch(String id) throws Exception {
        // wrap Student in AppStudent
        return new AppStudent(studentRepository.fetch(id));
    }

    public void add(String name, String surname, String id) throws Exception {
        studentRepository.create(new Student(name, surname, id));
    }

    public void add(Student student) throws Exception {
        studentRepository.create(student);
    }

    public void delete(String id) {
        studentRepository.delete(id);
    }
}