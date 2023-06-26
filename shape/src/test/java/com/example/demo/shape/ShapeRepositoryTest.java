package com.example.demo.shape;

import com.example.demo.shape.square.Square;
import com.example.demo.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShapeRepositoryTest {

    @Mock
    private ShapeRepository shapeRepository;

    @InjectMocks
    private ShapeService shapeService;

    @Test
    public void findShapesByParameters() {
        // Given
        Shape.ShapeType type = Shape.ShapeType.SQUARE;
        Double areaFrom = 50.0;
        Double areaTo = 150.0;
        Double perimeterFrom = 15.0;
        Double perimeterTo = 25.0;
        Double widthFrom = null;
        Double widthTo = null;
        Double radiusFrom = null;
        Double radiusTo = null;
        User createdBy = null;
        LocalDate createdFrom = LocalDate.of(2022, 1, 1);
        LocalDate createdTo = LocalDate.of(2022, 12, 31);

        Shape shape = Square.builder().build(); // Create a sample shape object
        List<Shape> expectedShapes = new ArrayList<>();
        expectedShapes.add(shape);

        // Stub the shapeRepository.findShapesByParameters() method
        when(shapeRepository.findShapesByParameters(
                eq(type), eq(areaFrom), eq(areaTo), eq(perimeterFrom), eq(perimeterTo),
                eq(widthFrom), eq(widthTo), eq(radiusFrom), eq(radiusTo),
                eq(createdBy), eq(createdFrom), eq(createdTo)
        )).thenReturn(expectedShapes);

        // When
        List<Shape> actualShapes = shapeRepository.findShapesByParameters(
                type, areaFrom, areaTo, perimeterFrom, perimeterTo,
                widthFrom, widthTo, radiusFrom, radiusTo,
                createdBy, createdFrom, createdTo
        );

        // Then
        assertEquals(expectedShapes, actualShapes);
    }
}
