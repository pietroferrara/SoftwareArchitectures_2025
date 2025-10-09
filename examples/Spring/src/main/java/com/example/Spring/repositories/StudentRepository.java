package com.example.Spring.repositories;

import com.example.Spring.errors.DuplicatedEntryError;
import com.example.Spring.errors.UnknownError;
import com.example.Spring.models.Student;
import jakarta.persistence.EntityManager;

import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class StudentRepository {

    @Autowired
    private EntityManager entityManager; //EntityManager permits to interact with the database. It is Autowired: Spring creates and injects this object for us.
    public StudentRepository() {
    }

    public void create(Student student) throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            // All operations that change the entries in a database must be performed inside a Transaction.
            // Suppose that you must do multiple insertions atomically for an application purpose. If one of these insertions fails, you don't want the other to be committed.
            // Example: you are creating a REST API endpoint that permits to register Users. Every user can have an Address.
            // During registration, if a user provides also the address in the request body, you want to add that Address to the database. Addresses and Users stay in different tables.
            // So, your application does two insertions: first, it will insert the Users, then it will insert the Address.
            // But if there are some problems inserting the Address, you want to roll back the insertion of the User.
            Transaction t = currentSession.beginTransaction();
            currentSession.persist(student);
            t.commit();
        } catch (Exception e) {
            Throwable t = e.getCause();
            if (t != null) {
                // catch the error
                if (t instanceof ConstraintViolationException) {
                    ConstraintViolationException exc = (ConstraintViolationException) t;
                    // get the SQL Exception error code
                    //Here you have a list of all the possible MySQL error codes: https://dev.mysql.com/doc/mysql-errors/8.0/en/server-error-reference.html
                    //For now, we catch only ERROR CODE 1062 (Duplicated Entry), and we consider all the other exceptions as unknown errors.
                    if (exc.getSQLException().getErrorCode() == 1062) {
                        throw new DuplicatedEntryError();
                    }
                    //Here you can handle other exceptions
                }
            }
            throw new UnknownError();
        }
    }

    public Collection<Student> getAll() throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            //To get all the Students, we use a different approach: we build a query.
            // CriteriaBuilder class permits the creation of custom and complex queries on a table.
            //For example, here we are creating a query like "SELECT * FROM students"
            CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
            CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
            Root<Student> root = criteriaQuery.from(Student.class);
            criteriaQuery.select(root);
            //Then we execute the query and we get the result (a List of Students).
            Query query = currentSession.createQuery(criteriaQuery);
            List<Student> s = query.getResultList();
            return s;
        } catch(Exception e) {
            // throw an UnknownError
            // possible handle and log the error.
            //The idea is to mask the internal error such that API users will get a 500 error (it doesn't need to know that something with the database is not working).
            //Think about building the APIs from the final user perspective: internal details and implementations should not be things that concern the user.
            // Users should not care if the problem regards the connection to the database or a software bug. This is an internal problem.
            // However, it is important to log the error such that developers can understand what's going on easily and handle errors in time.
            throw new UnknownError();
        }
    }
    public Student fetch(String id) throws Exception {
        try {
            Session currentSession = entityManager.unwrap(Session.class);
            Student s = currentSession.find(Student.class, id);
            return s;
        } catch (Exception ignored) {
        }
        throw new UnknownError();
    }


    public Student delete(String id) {
        // TODO as an exercise
        return null;
    }
}