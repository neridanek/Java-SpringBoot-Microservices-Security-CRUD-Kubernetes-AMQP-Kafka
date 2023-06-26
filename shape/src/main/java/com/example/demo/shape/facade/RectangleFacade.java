package com.example.demo.shape.facade;

import com.example.demo.shape.Shape;
import com.example.demo.shape.ShapeCreateRequest;
import com.example.demo.shape.rectangle.Rectangle;
import com.example.demo.user.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("rectangleFacade")
public class RectangleFacade implements ShapeFacade<Rectangle>{
    @Override
    public Rectangle createShape(ShapeCreateRequest shapeCreateRequest, User createdBy) {
        Rectangle rectangle = Rectangle.builder()
                .parameters(shapeCreateRequest.getParameters())
                .type(shapeCreateRequest.getType())
                .createdBy(createdBy)
                .createdAt(LocalDate.now())
                .lastModifiedAt(LocalDate.now())
                .lastModifiedBy(String.valueOf(createdBy))
                .build();
        return rectangle;
    }


}
