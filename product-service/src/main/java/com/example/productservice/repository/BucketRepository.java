package com.example.productservice.repository;

import com.example.productservice.model.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, UUID> {
    Optional<Bucket> findByUserId(UUID userId);
    @Query("SELECT b FROM Bucket b JOIN b.product p WHERE p.id = :productId")
    List<Bucket> findBucketsByProductId(@Param("productId") UUID productId);
}
