package com.batch.writer.service;

import com.batch.writer.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    public static final String USERS = "http://localhost:8080/api/v1/users";
    public static final String CREATE_USER = "http://localhost:8080/api/v1/createUser";
    List<User> list;

    public List<User> getAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        User[] UserArray = restTemplate.getForObject(USERS, User[].class);
        list = new ArrayList<>();
        for(User sr : UserArray) {
            list.add(sr);
        }

        return list;
    }

   // public User getUser(long id, String name) {
   public User getUser() {
       // System.out.println("id = " + id + " and name = " + name);
        if(list == null) {
            getAllUsers();
        }

        if(list != null && !list.isEmpty()) {
            return list.remove(0);
        }
        return null;
    }


    public User addNewUser(User user) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForObject(CREATE_USER, user, User.class);
    }
}
