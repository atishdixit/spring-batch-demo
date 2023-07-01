package com.batch.writer.controller;

import com.batch.writer.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/users")
    public List<User> users() {
        return Arrays.asList(
                new User(1L, "John", "Smith", "john@gmail.com"),
                new User(2L, "Sachin", "Dave", "sachin@gmail.com"),
                new User(3L, "Peter", "Mark", "peter@gmail.com"),
                new User(4L, "Martin", "Smith", "martin@gmail.com"),
                new User(5L, "Raj", "Patel", "raj@gmail.com"),
                new User(6L, "Virat", "Yadav", "virat@gmail.com"),
                new User(7L, "Prabhas", "Shirke", "prabhas@gmail.com"),
                new User(8L, "Tina", "Kapoor", "tina@gmail.com"),
                new User(9L, "Mona", "Sharma", "mona@gmail.com"),
                new User(10L, "Rahul", "Varma", "rahul@gmail.com"));
    }


    @PostMapping("/createUser")
    public User createNewUser(@RequestBody User user) {
        System.out.println("User Created "+ user.getId());
        return new User(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }
}
