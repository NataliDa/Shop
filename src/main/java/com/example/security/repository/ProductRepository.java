package com.example.security.repository;

import com.example.security.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {


    List<Product> findByTitleContainingIgnoreCase(String name);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByTitleOrderByPriceAsc(String title, float ot, float Do);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDesc(String title, float ot, float Do);

    @Query(value = "select * from product where category_id = ?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price", nativeQuery = true)
    List<Product> findByCategoryOrderByPriceAsc(String title, float ot, float Do, int category);

    @Query(value = "select * from product where category_id = ?4 and ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (price >= ?2 and price <= ?3) order by price desc", nativeQuery = true)
    List<Product> findByCategoryOrderByPriceDesc(String title, float ot, float Do, int category);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (category_id = ?2) order by price", nativeQuery = true)
    List<Product> findByCategoryOrderByPriceAsc(String title, int category);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) and (category_id = ?2) order by price desc", nativeQuery = true)
    List<Product> findByCategoryOrderByPriceDesc(String title, int category);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) order by price", nativeQuery = true)
    List<Product> findByTitleOrderByPriceAsc(String title);

    @Query(value = "select * from product where ((lower(title) LIKE %?1%) or (lower(title) LIKE '?1%') OR (lower(title) LIKE '%?1')) order by price desc", nativeQuery = true)
    List<Product> findByTitleOrderByPriceDesc(String title);
}

