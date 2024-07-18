package org.security.sample.to_do_list_app.controller;

import lombok.AllArgsConstructor;
import org.security.sample.to_do_list_app.model.ToDo;
import org.security.sample.to_do_list_app.repository.ToDoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

// Final Project - Step #0 (copy controller class from ToDoList app developed during Spring Data JPA course)

@Controller
@RequestMapping("/todo-list")
@AllArgsConstructor
public class ToDoController {

    private final ToDoRepository todoRepository;

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("todos", todoRepository.findAll());
        return "todo-page";
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam String task,
                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline) {
        ToDo todo = new ToDo();
        todo.setTask(task);
        todo.setDeadline(deadline);
        todoRepository.save(todo);
        return "redirect:/todo-list";
    }

    @PostMapping("/delete")
    public String deleteTodo(@RequestParam Long id) {
        todoRepository.deleteById(id);
        return "redirect:/todo-list";
    }

    @PostMapping("/update")
    public String updateTodo(@RequestParam Long id,
                             @RequestParam String task,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date deadline) {
        Optional<ToDo> todoOptional = todoRepository.findById(id);
        if (todoOptional.isPresent()) {
            ToDo todo = todoOptional.get();
            todo.setTask(task);
            todo.setDeadline(deadline);
            todoRepository.save(todo);
        }
        return "redirect:/todo-list";
    }
}
