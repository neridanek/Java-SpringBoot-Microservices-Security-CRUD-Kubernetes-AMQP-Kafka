package com.example.demo.user;

import com.example.demo.shape.Shape;
import jakarta.persistence.OneToMany;

import java.util.Date;
import java.util.List;
import java.util.Set;

public record UserDTO(Long id, String login, Date lastLoginDate, String password, Set<Shape> shapes) {
    public static UserDTO createFrom(User user){
        return new UserDTO(user.getId(),user.getLogin(),user.getLastLoginDate(),user.getPassword(),user.getShapes());
    }
}

