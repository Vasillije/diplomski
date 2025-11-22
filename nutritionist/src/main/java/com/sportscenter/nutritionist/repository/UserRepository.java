package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = 'PATIENT'")
    List<UserEntity> findAllPatients();
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = 'NUTRITIONIST'")
    List<UserEntity> findAllNutritionists();
}
