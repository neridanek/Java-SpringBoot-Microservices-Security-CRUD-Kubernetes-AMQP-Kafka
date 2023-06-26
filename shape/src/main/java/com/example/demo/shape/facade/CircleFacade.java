package com.example.demo.shape.facade;

import com.example.demo.shape.Shape;
import com.example.demo.shape.ShapeCreateRequest;
import com.example.demo.shape.circle.Circle;
import com.example.demo.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("circleFacade")
@RequiredArgsConstructor
public class CircleFacade implements ShapeFacade<Circle> {
    @Override
    public Circle createShape(ShapeCreateRequest shapeCreateRequest, User createdBy) {

        return Circle.builder()
                .parameters(shapeCreateRequest.getParameters())
                .type(shapeCreateRequest.getType())
                .createdBy(createdBy)
                .createdAt(LocalDate.now())
                .lastModifiedAt(LocalDate.now())
                .lastModifiedBy(String.valueOf(createdBy))
                .build();
    }

}
