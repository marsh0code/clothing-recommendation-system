package com.example.wardrobeapi.repository;

import com.example.wardrobeapi.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.imageUrl = :imageUrl where u.id = :id")
    void updateImageKeyById(Long id, String imageUrl);
}
