package org.security.sample.to_do_list_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

// Final Project - Step #0 (copy entity class from ToDoList app developed during Spring Data JPA course)

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String task;

    @Temporal(TemporalType.DATE)
    private Date deadline;
}
