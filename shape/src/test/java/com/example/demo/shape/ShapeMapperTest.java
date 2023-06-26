package com.example.demo.shape;

import com.example.demo.shape.square.Square;
import com.example.demo.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ShapeMapperTest {

    @InjectMocks
    private ShapeMapper shapeMapper;

    @Test
    public void mapToResponseDTO() {
        // Given
        List<Double> parameters = List.of(2.0);
        User user = User.builder()
                .login("Wiktor")
                .build();

        Shape square = Square.builder()
                .id(1L)
                .parameters(parameters)
                .createdBy(user)
                .area(100.0)
                .perimeter(20.0)
                .type("SHAPE")
                .createdAt(LocalDate.now())
                .build();

        // When
        ShapeResponseDTO responseDTO = shapeMapper.mapToResponseDTO(square);

        // Then
        assertEquals(square.getId(), responseDTO.getId());
        assertEquals(square.getType(), responseDTO.getType());
        assertEquals(square.getParameters(), responseDTO.getParameters());
        assertEquals(square.getVersion(), responseDTO.getVersion());
        assertEquals(square.getCreatedBy().getUsername(), responseDTO.getCreatedBy());
        assertEquals(square.getCreatedAt(), responseDTO.getCreatedAt());
        assertEquals(square.getLastModifiedBy(),responseDTO.getLastModifiedBy());
        assertEquals(square.getLastModifiedAt(),responseDTO.getLastModifiedAt());
        assertEquals(square.getArea(), responseDTO.getArea(), 0.001);
        assertEquals(square.getPerimeter(), responseDTO.getPerimeter(), 0.001);
    }
}
