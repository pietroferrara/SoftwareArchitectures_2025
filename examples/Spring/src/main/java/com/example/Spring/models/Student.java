package com.example.Spring.models;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;
@Entity // Entity means that this class must be threatened as a relational entity.
@Table( name = "Students" ) // This is used by the ORM to link this Entity in a DBMS Table.
public class Student extends RepresentationModel<Student> {
    @Column //This tells ORM to map this attribute (name) to a table field with the same name. You can specify the name of the field inside the database by using @Column(name="db_column_name")
    private String name;
    @Column
    private String surname;
    @Id // Id means that this field is the Primary Key of the table.
    @Column
    private String id;

    public Student(String name, String surname, String id) {
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    public Student() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String toString() {
        return "[ID: " + id + ", Name: " + name +", Surname: " + surname + "]";
    }
}