package com.example.demo.shape.rectangle;

import com.example.demo.shape.Shape;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("rectangle")
@Data
@SuperBuilder
@NoArgsConstructor
public class Rectangle extends Shape {


    @Override
    public Double calculateArea() {
        return getParameters().get(1) * getParameters().get(0);
    }

    @Override
    public Double calculatePerimeter() {
        return 2 * getParameters().get(1) + 2 * getParameters().get(0);
    }
}
