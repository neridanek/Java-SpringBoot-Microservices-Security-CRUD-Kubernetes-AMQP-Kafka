package com.example.demo.shape;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.shape.facade.ShapeFacade;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShapeService {

    private final Map<String, ShapeFacade> facades;

    private final ShapeRepository shapeRepository;

    private final UserRepository userRepository;


    public Shape createShape(ShapeCreateRequest shapeCreateRequest, User createdBy) {
        ShapeFacade facade = facades.get(shapeCreateRequest.getType().toLowerCase()+"Facade");
        return shapeRepository.save(facade.createShape(shapeCreateRequest,createdBy));
    };

    @Transactional
    public Shape addShape(ShapeCreateRequest shapeCreateRequest, UserDetails userDetails){
        String username = userDetails.getUsername();
        User user = userRepository.findUserByLogin(username).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return createShape(shapeCreateRequest,user);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "shapes", key = "#id")
    public List<Shape> getShapes(ShapeFindRequest shapeRequest){
        User user = userRepository.findUserByLogin(shapeRequest.getCreatedBy())
                .orElseThrow(()-> new ResourceNotFoundException("user not found"));

        return shapeRepository.findShapesByParameters(
                shapeRequest.getType(),
                shapeRequest.getAreaFrom(),
                shapeRequest.getAreaTo(),
                shapeRequest.getPerimeterFrom(),
                shapeRequest.getPerimeterTo(),
                shapeRequest.getWidthFrom(),
                shapeRequest.getWidthTo(),
                shapeRequest.getRadiusFrom(),
                shapeRequest.getRadiusTo(),
                user,
                shapeRequest.getCreatedFrom(),
                shapeRequest.getCreatedTo()
        );
    }

    public void deleteShape(int id,UserDetails userDetails){
        Shape shape = shapeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Shape not found"));
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isShapeCreator = shape.getCreatedBy().getLogin().equals(userDetails.getUsername());

        if (!isAdmin && !isShapeCreator) {
            throw new AccessDeniedException("Access Denied");
        };

        shapeRepository.deleteById(id);
    }

    @Transactional
    public Shape updateShape(int id, ShapeUpdateRequest shapeUpdateRequest, UserDetails userDetails){
        Shape shape = shapeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Shape not found"));
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isShapeCreator = shape.getCreatedBy().getUsername().equals(userDetails.getUsername());

        //TODO: OPTIMISTICK LOCK HOW TO CREATE COPY WITH ABSTRACT CLASS ?
        if (!isAdmin && !isShapeCreator) {
            throw new AccessDeniedException("Access Denied");
        }

        if(shapeUpdateRequest.getType() != null){
            shape.setType((shapeUpdateRequest.getType()).toUpperCase());
        }
        if(shapeUpdateRequest.getParameters() != null){
            shape.setParameters(shapeUpdateRequest.getParameters());
        }
        shape.calculatePerimeter();
        shape.calculateArea();
        shape.setLastModifiedBy(userDetails.getUsername());
        shape.setLastModifiedAt(LocalDate.now());
        shape.setArea(shape.calculateArea());
        shape.setVersion(shapeUpdateRequest.getVersion());
        shape.setPerimeter(shape.calculatePerimeter());

        return shapeRepository.save(shape);
    }

    public void addUserToShape(int shapeId,int userId){
        Shape shape = shapeRepository.findByIdWithLocking(shapeId).orElseThrow();
        User user = userRepository.findByIdWithShapes(userId).orElseThrow();
        shape.addUser(user);
    }

    public void removeShapeFromUser(int shapeId){
        Shape shape = shapeRepository.findByIdWithLocking(shapeId).orElseThrow();
        shape.removeUser();
    }

}

