package com.example.demo.shape.facade;

import com.example.demo.shape.Shape;
import com.example.demo.shape.ShapeCreateRequest;
import com.example.demo.shape.square.Square;
import com.example.demo.user.User;

import java.time.LocalDate;

public class SquareFacade implements ShapeFacade<Square> {
    @Override
    public Square createShape(ShapeCreateRequest shapeCreateRequest, User createdBy) {
        return Square.builder()
                .parameters(shapeCreateRequest.getParameters())
                .createdBy(createdBy)
                .type(shapeCreateRequest.getType())
                .createdAt(LocalDate.now())
                .lastModifiedAt(LocalDate.now())
                .lastModifiedBy(String.valueOf(createdBy))
                .build();
    }
}
