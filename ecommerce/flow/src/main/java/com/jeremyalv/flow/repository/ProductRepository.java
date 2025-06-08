package com.jeremyalv.flow.repository;


import com.jeremyalv.flow.model.Product;


import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
