package com.example.Spring.models;

import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class AppStudent extends Student {

    public AppStudent(Student s) {
        this.setName(s.getName());
        this.setSurname(s.getSurname());
        this.setId(s.getId());
        this.add(linkTo(methodOn(com.example.Spring.controllers.Student.class).getStudents(s.getId())).withSelfRel());
    }

}