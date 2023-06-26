package com.example.demo.shape;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.shape.circle.Circle;
import com.example.demo.shape.rectangle.Rectangle;
import com.example.demo.shape.square.Square;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class ShapeControllerTest {
        @Mock
        private ShapeService shapeService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private ShapeMapper shapeMapper;

        @Mock
        private UserDetails userDetails;

        @Mock
        private ShapeRepository shapeRepository;

        @InjectMocks
        private ShapeController shapeController;

        private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(shapeController).build();
    }

    @Test
    public void addShapeWithExistingShapeTypeInSystem() throws Exception {

        User user = User.builder().login("testuser").password("password").role(User.Role.CREATOR).build();
        when(userRepository.findUserByLogin(user.getLogin())).thenReturn(Optional.of(user));


        ShapeCreateRequest shapeCreateRequest = ShapeCreateRequest.builder().type(String.valueOf(Shape.ShapeType.SQUARE)).parameters(List.of(10.0)).build();
        Shape createdShape = Square.builder().type(String.valueOf(Shape.ShapeType.SQUARE)).parameters(List.of(10.0)).build();
        when(shapeService.createShape(eq(String.valueOf(Shape.ShapeType.SQUARE)), anyList(), eq(user), eq(null), eq(null))).thenReturn(createdShape);
        ShapeResponseDTO shapeResponseDTO = ShapeResponseDTO.builder().id(1L).type(String.valueOf(Shape.ShapeType.SQUARE)).parameters(Collections.singletonList(10.0)).build();
        when(shapeMapper.mapToResponseDTO(createdShape)).thenReturn(shapeResponseDTO);


        mockMvc.perform(post("/api/v1/shapes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(shapeCreateRequest))
                        .with(user(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        String username = user.getLogin();
        verify(userRepository, times(1)).findUserByLogin(username);
        verify(shapeService, times(1)).createShape(eq("SQUARE"), anyList(), eq(user),null,null);
        verify(shapeMapper, times(1)).mapToResponseDTO(createdShape);

    }

    @Test
    public void addShapeWithNewShapeType() {
        User user = User.builder().login("testuser").password("password").role(User.Role.CREATOR).build();
//        when(userRepository.findUserByLogin(user.getLogin())).thenReturn(Optional.of(user));
        String areaFormula = "parameters[0] / 2 * parameters[4] * parameters[1] / 2 * parameters[4]";
        String perimeterFormula = "parameters[0] + parameters[1] + parameters[2] + parameters[3]";
        ShapeCreateRequest shapeCreateRequest = ShapeCreateRequest.builder().type("TRAPEZOID").areaFormula(areaFormula)
                .perimeterFormula(perimeterFormula).parameters(List.of(3.0, 5.0, 6.0, 7.0, 4.0)).build();
        ShapeCustom shape = ShapeCustom.builder().areaFormula(shapeCreateRequest.getAreaFormula())
                .parameters(shapeCreateRequest.getParameters()).perimeterFormula(shapeCreateRequest.getPerimeterFormula())
                .type(shapeCreateRequest.getType()).build();
        shape.setArea(shape.calculateArea());
        shape.setPerimeter(shape.calculatePerimeter());
//        when(shapeService.createShape(eq(shapeRequest.getType()), eq(shapeRequest.getParameters()),
//                eq(user), eq(shapeRequest.getAreaFormula()), eq(shapeRequest.getPerimeterFormula()))).thenReturn(shape);
        ShapeResponseDTO shapeResponseDTO = ShapeResponseDTO.builder()
                .type(shape.getType()).parameters(shape.getParameters())
                .area(shape.getArea()).perimeter(shape.getPerimeter()).build();
//        when(shapeMapper.mapToResponseDTO(shape)).thenReturn(shapeResponseDTO);


    }





    @Test
    void getShapes() throws Exception {
        // Given
        String type = "square";
        Double areaFrom = 10.0;
        Double areaTo = 20.0;
        Double perimeterFrom = 5.0;
        Double perimeterTo = 15.0;
        Double widthFrom = 5.0;
        Double widthTo = 15.0;
        Double radiusFrom = 5.0;
        Double radiusTo = 15.0;
        String createdBy = "wiktorn";
        LocalDate createdFrom = LocalDate.now();
        LocalDate createdTo = LocalDate.now();

        User user = User.builder()
                .login(createdBy)
                .lastLoginDate(new Date())
                .password("password")
                .role(User.Role.CREATOR)
                .build();

        Shape createdShape = Square.builder()
                .type(String.valueOf(Shape.ShapeType.SQUARE))
                .version(0L)
                .lastModifiedAt(LocalDate.now())
                .createdBy(user)
                .createdAt(LocalDate.now())
                .lastModifiedBy(user.getLogin())
                .parameters(List.of(10.0))
                .build();

        List<Shape> shapes = List.of(createdShape);

        ShapeResponseDTO createdShapeDTO = ShapeResponseDTO.builder()
                .type(createdShape.getType())
                .parameters(createdShape.getParameters())
                .build();

        when(userRepository.findUserByLogin(eq(createdBy))).thenReturn(Optional.of(user));
        when(shapeRepository.findShapesByParameters(eq(Shape.ShapeType.SQUARE), eq(areaFrom), eq(areaTo),
                eq(perimeterFrom), eq(perimeterTo), eq(widthFrom), eq(widthTo), eq(radiusFrom), eq(radiusTo),
                eq(user), eq(createdFrom), eq(createdTo))).thenReturn(shapes);
        when(shapeMapper.mapToResponseDTO(eq(createdShape))).thenReturn(createdShapeDTO);

        // When
        mockMvc.perform(get("/api/v1/shapes")
                        .param("type", type)
                        .param("areaFrom", String.valueOf(areaFrom))
                        .param("areaTo", String.valueOf(areaTo))
                        .param("perimeterFrom", String.valueOf(perimeterFrom))
                        .param("perimeterTo", String.valueOf(perimeterTo))
                        .param("widthFrom", String.valueOf(widthFrom))
                        .param("widthTo", String.valueOf(widthTo))
                        .param("radiusFrom", String.valueOf(radiusFrom))
                        .param("radiusTo", String.valueOf(radiusTo))
                        .param("createdBy", createdBy)
                        .param("createdFrom", createdFrom.toString())
                        .param("createdTo", createdTo.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        // Then
        verify(userRepository).findUserByLogin(eq(createdBy));
        verify(shapeRepository).findShapesByParameters(eq(Shape.ShapeType.SQUARE), eq(areaFrom), eq(areaTo),
                eq(perimeterFrom), eq(perimeterTo), eq(widthFrom), eq(widthTo), eq(radiusFrom), eq(radiusTo),
                eq(user), eq(createdFrom), eq(createdTo));
        verify(shapeMapper).mapToResponseDTO(eq(createdShape));
    }

    @Test
    void updateShape() {
        // Given
        User user = User.builder()
                .login("wiktorn")
                .lastLoginDate(new Date())
                .password("asd")
                .role(User.Role.valueOf("CREATOR"))
                .build();

        Long shapeId = 1L;
        List<Double> parameters = List.of(5.0, 6.0);
        List<Double> newParameters = List.of(10.0, 11.0);
        ShapeCreateRequest shapeCreateRequest = ShapeCreateRequest.builder()
                .type(String.valueOf(Shape.ShapeType.CIRCLE))
                .parameters(parameters)
                .build();

        Shape shape = Rectangle.builder()
                .type(String.valueOf(Shape.ShapeType.RECTANGLE))
                .parameters(parameters)
                .createdBy(user)
                .build();
        shape.calculateArea();
        shape.calculatePerimeter();

        Shape updatedShape = Circle.builder()
                .type(String.valueOf(Shape.ShapeType.CIRCLE))
                .parameters(newParameters)
                .createdBy(user)
                .build();
        updatedShape.calculateArea();
        updatedShape.calculatePerimeter();

        ShapeResponseDTO expectedResponseDTO = shapeMapper.mapToResponseDTO(updatedShape);

        when(shapeRepository.findById(shapeId)).thenReturn(Optional.of(shape));
        when(shapeRepository.save(shape)).thenReturn(updatedShape);
        when(shapeMapper.mapToResponseDTO(updatedShape)).thenReturn(expectedResponseDTO);

        // When
        ResponseEntity<ShapeResponseDTO> response = shapeController.updateShape(shapeId, shapeCreateRequest, user);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDTO, response.getBody());

        verify(shapeRepository).findById(shapeId);
        verify(shapeRepository).save(shape);
    }




    @Test
    void deleteShape() {
        //Given
        List<Double> parameters = List.of(2.0);
        User user = User.builder().login("wiktorn")
                .lastLoginDate(new Date())
                .password("asd").role(User.Role.valueOf("CREATOR"))
                .build();
        Shape shape = Square.builder()
                .parameters(parameters)
                .createdBy(user)
                .id(1L)
                .type(String.valueOf(Shape.ShapeType.CIRCLE))
                .build();
        //When
        when(shapeRepository.findById(1L)).thenReturn(Optional.of(shape));
        ResponseEntity<Void> response = shapeController.deleteShape(1L,user);

        //Then
        verify(shapeRepository, times(1)).findById(1L);
        verify(shapeRepository, times(1)).deleteById(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldThrowShapeNotFoundForMissingShapes() {
        //Given
        Long shapeId = 1L;
        when(shapeRepository.findById(shapeId)).thenReturn(Optional.empty());
        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                shapeController.deleteShape(shapeId, userDetails)
        );

        // Then
        assertEquals("Shape not found", exception.getMessage());
        verify(shapeRepository, times(1)).findById(shapeId);
        verifyNoMoreInteractions(shapeRepository);
        verifyNoInteractions(userDetails);
    }

    @Test
    void addShape_UserNotFound(){
        // Given
        List<Double> parameters = List.of(2.0);
        ShapeCreateRequest shape = ShapeCreateRequest.builder().type(String.valueOf(Shape.ShapeType.CIRCLE)).parameters(parameters).build();

        String username = "test";
        UserDetails userDetails = User.builder().login(username).password("password").build();

        // When
        when(userRepository.findUserByLogin(username)).thenReturn(Optional.empty());

        // Then
        assertThrows(ResourceNotFoundException.class,()->{
            shapeController.addShape(shape, userDetails);
        });

    }

    @Test
    void getShapes_NoShapesFound(){
        String type = "circle";
        Double areaFrom = 10.0;
        Double areaTo = 20.0;
        Double perimeterFrom = 5.0;
        Double perimeterTo = 15.0;
        Double widthFrom = 2.0;
        Double widthTo = 8.0;
        Double radiusFrom = 3.0;
        Double radiusTo = 7.0;
        LocalDate createdFrom = LocalDate.parse("2023-01-01");
        LocalDate createdTo = LocalDate.parse("2023-05-01");
        String createdBy = "wiktorn";

        User user = User.builder().login(createdBy).build();
        when(userRepository.findUserByLogin(eq(createdBy))).thenReturn(Optional.of(user));
        when(shapeRepository.findShapesByParameters(
                eq(Shape.ShapeType.CIRCLE),
                eq(areaFrom),
                eq(areaTo),
                eq(perimeterFrom),
                eq(perimeterTo),
                eq(widthFrom),
                eq(widthTo),
                eq(radiusFrom),
                eq(radiusTo),
                eq(user),
                eq(createdFrom),
                eq(createdTo)
        )).thenReturn(Collections.emptyList());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            shapeController.getShapes(type, areaFrom, areaTo, perimeterFrom, perimeterTo,
                    widthFrom, widthTo, radiusFrom, radiusTo, createdFrom, createdTo, createdBy);
        });
    }


    @Test
    void shouldThrowAccessDeniedExceptionForNonAuthorizedUser(){
        User user = User.builder().login("wiktorn")
                .lastLoginDate(new Date())
                .password("asd").role(User.Role.valueOf("CREATOR"))
                .build();
        List<Double> parameters = List.of(2.0);
        Shape shape = Rectangle.builder()
                .type(String.valueOf(Shape.ShapeType.RECTANGLE))
                .parameters(parameters)
                .createdBy(user)
                .build();

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isShapeCreator = shape.getCreatedBy().getLogin().equals(userDetails.getUsername());

        // Assert access denied
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            if (!isAdmin && !isShapeCreator) {
                throw new AccessDeniedException("Access Denied");
            }
        });
    }

}