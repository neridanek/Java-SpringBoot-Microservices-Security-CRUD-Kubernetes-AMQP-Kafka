package com.example.demo.shape;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Data
public class ShapeFindRequest {
    String type;
    Double areaFrom;
    Double areaTo;
    Double perimeterFrom;
    Double perimeterTo;
    Double widthFrom;
    Double widthTo;
    Double radiusFrom;
    Double radiusTo;
    LocalDate createdFrom;
    LocalDate createdTo;
    String createdBy;
}
