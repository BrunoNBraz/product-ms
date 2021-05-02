package com.brunonbraz.productms.repository;

import com.brunonbraz.productms.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findAllBySearch(String q, BigDecimal minPrice, BigDecimal maxPrice);
}
