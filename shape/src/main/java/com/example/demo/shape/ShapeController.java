package com.example.demo.shape;

import com.example.demo.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/v1/shapes")
@Slf4j
@RequiredArgsConstructor
public class ShapeController {

    private final ShapeService shapeService;

    @PostMapping
    public ResponseEntity<ShapeDTO> addNewShape(@RequestBody ShapeCreateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("addNewShape {}",request);
        Shape shape = shapeService.addShape(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ShapeDTO.createFrom(shape));
    }


    @GetMapping
    @Operation(summary = "Get specific shape")
    public ResponseEntity<List<ShapeDTO>> getShapes(@ModelAttribute ShapeFindRequest shapeRequest) throws JsonProcessingException {
        log.info("getShapes {}",shapeRequest);
        List<Shape> shapes = shapeService.getShapes(shapeRequest);

        if (shapes.isEmpty()) {
            throw new ResourceNotFoundException("No shapes found with the specified parameters.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(shapes.stream().map(ShapeDTO::createFrom).collect(Collectors.toList()));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ShapeDTO> updateShape(@PathVariable int id, @RequestBody ShapeUpdateRequest request,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        log.info("updateShape {},{}",id,request);
        Shape shape = shapeService.updateShape(id,request,userDetails);

        return ResponseEntity.ok().body(ShapeDTO.createFrom(shape));
    }


    @DeleteMapping("/{id}")
    @CacheEvict(cacheNames = "shapes", key="#id")
    public ResponseEntity<Void> deleteShape(@PathVariable int id,@AuthenticationPrincipal UserDetails userDetails){
        log.info("deleteShape {}",id);
       shapeService.deleteShape(id,userDetails);
        return ResponseEntity.noContent().build();

    }


    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity addUserToShape(@PathVariable int id, @PathVariable int userId){
        log.info("addUserToShape {},{}",id,userId);
        shapeService.addUserToShape(id,userId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}/user")
    public ResponseEntity removeUserFromShape(@PathVariable int id){
        log.info("addUserToShape {}",id);
        shapeService.removeShapeFromUser(id);
        return ResponseEntity.ok().build();
    }

}
