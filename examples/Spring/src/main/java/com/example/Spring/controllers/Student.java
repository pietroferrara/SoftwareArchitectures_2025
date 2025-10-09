package com.example.Spring.controllers;

import com.example.Spring.errors.DuplicatedEntryError;
import com.example.Spring.models.AppStudent;
import com.example.Spring.models.ErrorResponse;
import com.example.Spring.services.AppStudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class Student {
    @Autowired
    private AppStudentService studentService;

    @GetMapping("/students")
    public ResponseEntity<?> getStudents() {
        try {
            return ResponseEntity.ok(studentService.findAll());

        } catch (Exception ignored) {
        }
        ErrorResponse responseJson = new ErrorResponse("Internal error");
        return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @PostMapping(value = "/students")
    public ResponseEntity<?> addStudent(HttpServletRequest request, @RequestBody com.example.Spring.models.Student student) throws URISyntaxException {
        try {
            studentService.add(student);
            return ResponseEntity.created(new URI(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + request.getRequestURI() + "/" + student.getId())).build();

        } catch (Exception e) {
            if (e instanceof DuplicatedEntryError) {
                ErrorResponse responseJson = new ErrorResponse("Student with this id already exists");
                return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.CONFLICT);
            }
        }
        ErrorResponse responseJson = new ErrorResponse("Internal error");
        return new ResponseEntity<ErrorResponse>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @GetMapping("/students/{id}")
    public ResponseEntity<AppStudent> getStudents(@PathVariable String id) {
        try {
            AppStudent s = studentService.fetch(id);
            if (s == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(s);

        } catch (Exception ignored) {}

        return ResponseEntity.notFound().build();
    }
}