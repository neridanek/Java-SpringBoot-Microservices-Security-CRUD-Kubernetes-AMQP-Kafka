package com.example.demo.shape.circle;

import com.example.demo.shape.Shape;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("circle")
@Data
@NoArgsConstructor
@SuperBuilder
public class Circle extends Shape {

    @Override
    public Double calculateArea() {
        return Math.PI * Math.pow(getParameters().get(0), 2);
    }

    @Override
    public Double calculatePerimeter() {
        return 2 * Math.PI * getParameters().get(0);
    }
}
