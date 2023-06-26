package com.example.demo.shape;

import com.example.demo.shape.rectangle.RectangleService;
import com.example.demo.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShapeServiceTest {

    @Mock
    private ShapeRepository shapeRepository;

    @InjectMocks
    private RectangleService rectangleService;


    @Test
    void createShape() {
        //Given
        String type = "RECTANGLE";
        List<Double> parameters = List.of(5.0,6.0);
        User createdBy = User.builder().login("wiktorn")
                .lastLoginDate(new Date())
                .password("asd")
                .role(User.Role.valueOf("CREATOR"))
                .build();

        //When
        Shape createdShape = rectangleService.createRectangle(
                parameters, createdBy);

        // Verify the shapeRepository.save() method
        // was called with Mockito
        verify(shapeRepository).save(Mockito.any(Shape.class));

        //Then
        assertEquals(Shape.ShapeType.RECTANGLE.toString(), createdShape.getType());
        assertEquals(parameters, createdShape.getParameters());
        assertEquals(createdBy, createdShape.getCreatedBy());
        assertEquals(LocalDate.now(), createdShape.getCreatedAt());
    }
}