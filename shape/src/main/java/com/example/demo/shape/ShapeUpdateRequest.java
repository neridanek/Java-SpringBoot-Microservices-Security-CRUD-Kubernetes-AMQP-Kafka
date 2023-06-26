package com.example.demo.shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShapeUpdateRequest {
    private String type;
    private List<Double> parameters;
    private long version;
}
