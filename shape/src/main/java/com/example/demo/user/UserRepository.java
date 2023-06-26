package com.example.demo.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u WHERE "
            + "(u.login = :login OR :login = null)"
            + "AND (u.lastLoginDate BETWEEN :lastLoginDateFrom AND :lastLoginDateTo OR :lastLoginDateFrom = null OR :lastLoginDateTo = null)"
            + "AND (SIZE(u.shapes) BETWEEN :shapesCreatedFrom AND :shapesCreatedTo OR :shapesCreatedFrom = null OR :shapesCreatedTo = null)"
    )
    List<User> searchUsers(@Param("login") String login,
                           @Param("lastLoginDateFrom") Date lastLoginDateFrom,
                           @Param("lastLoginDateTo") Date lastLoginDateTo,
                           @Param("shapesCreatedFrom") int shapesCreatedFrom,
                           @Param("shapesCreatedTo") int shapesCreatedTo);

    Optional<User> findUserByLogin(@Param("login") String login);

    @Query(value = "SELECT new com.example.demo.user.FullUserDTO(u.id, u.login, u.lastLoginDate, u.password, u.shapes, (SELECT COUNT(s) FROM Shape s WHERE s.createdBy.id = u.id)) FROM User u",
            countQuery = "SELECT COUNT(u) FROM User u")
    Page<FullUserDTO> findAllWithShapesCount(Pageable pageable);


    @Query("Select u from User u left join fetch u.shapes")
    List<User> findAllWithShapes();

    @Query("Select u from User u left join fetch u.shapes where u.id = ?1")
    Optional<User> findByIdWithShapes(int id);

}
