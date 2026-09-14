package com.golajugaenyang.product.adapter.out.persistence.product;

import com.golajugaenyang.product.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

}
