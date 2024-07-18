package org.security.sample.to_do_list_app.repository;

import org.security.sample.to_do_list_app.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Final Project - Step #0 (copy repository class from ToDoList app developed during Spring Data JPA course)

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
