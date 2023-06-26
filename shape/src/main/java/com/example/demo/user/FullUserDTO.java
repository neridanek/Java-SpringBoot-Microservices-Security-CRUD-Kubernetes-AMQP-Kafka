package com.example.demo.user;

import com.example.demo.shape.Shape;

import java.util.Date;
import java.util.List;
import java.util.Set;

public record FullUserDTO(Long id, String login, Date lastLoginDate, String password, Set<Shape> shapes, Long amountOfShapes) {

}
