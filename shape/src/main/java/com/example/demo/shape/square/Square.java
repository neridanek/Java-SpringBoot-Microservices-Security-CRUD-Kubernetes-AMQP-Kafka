package com.example.demo.shape.square;

import com.example.demo.shape.Shape;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;



@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("square")
@NoArgsConstructor
@SuperBuilder
public class Square extends Shape {

    @Override
    public Double calculateArea() {
        return Math.pow(getParameters().get(0),2);
    }

    @Override
    public Double calculatePerimeter() {
        return 4 * getParameters().get(0);
    }
}
