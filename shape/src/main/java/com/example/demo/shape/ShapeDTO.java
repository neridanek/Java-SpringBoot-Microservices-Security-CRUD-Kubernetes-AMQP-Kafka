package com.example.demo.shape;

import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

public record ShapeDTO(
        Integer id,
        String type,
        User createdBy,
        LocalDate createdAt,
        List<Double> parameters,
        Double area,
        Double perimeter,
        String lastModifiedBy,
        LocalDate lastModifiedAt,
        Long version) {

    public static ShapeDTO createFrom(Shape shape){
        return new ShapeDTO(shape.getId(),shape.getType(),shape.getCreatedBy(),shape.getCreatedAt(),shape.getParameters(),shape.calculateArea(),shape.calculatePerimeter(),shape.getLastModifiedBy(),shape.getLastModifiedAt(),shape.getVersion());
    }

}


