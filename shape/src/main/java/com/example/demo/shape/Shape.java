package com.example.demo.shape;


import com.example.demo.exception.UserAlreadyAssignedException;
import com.example.demo.exception.UserNotAssignedException;
import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "shapes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "shape_type")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Shape {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shapeIdGenerator")
    @SequenceGenerator(name = "shapeIdGenerator", sequenceName = "shape_seq", initialValue = 1, allocationSize = 100)
    private int id;

    @Column(name = "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "createdAt")
    private LocalDate createdAt;

    @Column(name = "parameters")
    @ElementCollection
    private List<Double> parameters;

    @Column(name = "area")
    Double area;

    @Column(name = "perimeter")
    Double perimeter;

    @Column(name = "lastModifiedBy")
    private String lastModifiedBy;

    @Column(name = "lastModifiedAt")
    private LocalDate lastModifiedAt;

    @Version
    private Long version;


    public Double calculateArea() {
        return null;
    };


    public Double calculatePerimeter() {
        return null;
    }

    public void addUser(User user) {
            validateAddUser(user);
            setCreatedBy(user);
    }

    public void removeUser() {
        validateRemoveUser();
        setCreatedBy(null);
    }

    private void validateRemoveUser() {
        if(createdBy == null) throw new UserNotAssignedException("user is not assigned");
    }

    private void validateAddUser(User user){
        if(createdBy.equals(user)) throw new UserAlreadyAssignedException("user is already added");
    }


}
