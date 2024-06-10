package com.example.wardrobeapi.repository;

import com.example.wardrobeapi.domain.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingRepository extends JpaRepository<ClothingItem, Long> {
    @Modifying
    @Query("UPDATE ClothingItem c SET c.imageUrl = :imageUrl where c.id = :id")
    void updateImageKeyById(Long id, String imageUrl);
}
