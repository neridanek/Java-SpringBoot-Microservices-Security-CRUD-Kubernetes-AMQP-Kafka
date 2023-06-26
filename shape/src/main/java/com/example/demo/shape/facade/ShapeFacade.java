package com.example.demo.shape.facade;

import com.example.demo.shape.Shape;
import com.example.demo.shape.ShapeCreateRequest;
import com.example.demo.user.User;

public interface ShapeFacade<ENTITY extends Shape> {
    ENTITY createShape(ShapeCreateRequest shapeCreateRequest, User createdBy);
//    DTO mapToDTO(ENTITY Shape);
}
