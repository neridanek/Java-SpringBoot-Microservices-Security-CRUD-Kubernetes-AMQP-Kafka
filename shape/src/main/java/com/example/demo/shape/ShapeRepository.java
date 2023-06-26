package com.example.demo.shape;


import com.example.demo.user.FullUserDTO;
import com.example.demo.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ShapeRepository extends JpaRepository<Shape, Integer> {

    @Query("SELECT s FROM Shape s WHERE "
        + "(s.type = :type OR :type IS NULL) "
        + "AND (s.area BETWEEN :areaFrom AND :areaTo OR :areaFrom IS NULL OR :areaTo IS NULL) "
        + "AND (s.perimeter BETWEEN :perimeterFrom AND :perimeterTo OR :perimeterFrom IS NULL OR :perimeterTo IS NULL) "
            + "AND (:widthFrom IS NULL OR :widthTo IS NULL OR EXISTS (SELECT p FROM s.parameters p WHERE p IN (:widthFrom, :widthTo))) "
            + "AND (:radiusFrom IS NULL OR :radiusTo IS NULL OR EXISTS (SELECT p FROM s.parameters p WHERE p IN (:radiusFrom, :radiusTo))) "
        + "AND (s.createdBy = :createdBy OR :createdBy IS NULL) "
        + "AND (s.createdAt BETWEEN :createdFrom AND :createdTo OR :createdFrom IS NULL OR :createdTo IS NULL)")
    List<Shape> findShapesByParameters(
            @Param("type") String type, @Param("areaFrom") Double areaFrom, @Param("areaTo") Double areaTo,
            @Param("perimeterFrom") Double perimeterFrom, @Param("perimeterTo") Double perimeterTo,
            @Param("widthFrom") Double widthFrom, @Param("widthTo") Double widthTo, @Param("radiusFrom") Double radiusFrom,
            @Param("radiusTo") Double radiusTo, @Param("createdBy") User createdBy,
            @Param("createdFrom") LocalDate createdFrom, @Param("createdTo") LocalDate createdTo);


    @Query("select s from Shape s left join fetch s.createdBy")
    List<Shape> findAllWithUserDetails();

    @Query("select s from Shape s left join fetch s.createdBy where s.id=?1")
    Optional<Shape> findShapeWithUserDetails(Long id);

    @Query("select s from Shape s where s.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Shape> findByIdWithLocking(int id);

}




