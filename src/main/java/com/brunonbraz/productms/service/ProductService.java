package com.brunonbraz.productms.service;

import com.brunonbraz.productms.domain.Product;
import com.brunonbraz.productms.dto.ProductDTO;
import com.brunonbraz.productms.exception.ProductNotFound;
import com.brunonbraz.productms.repository.ProductRepository;
import com.brunonbraz.productms.repository.ProductRepositoryCustom;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private ProductMapperService mapperService;

    private ProductRepository repository;

    private ProductRepositoryCustom repositoryCustom;

    public ProductService(ProductMapperService mapperService, ProductRepository repository, ProductRepositoryCustom repositoryCustom) {
        this.mapperService = mapperService;
        this.repository = repository;
        this.repositoryCustom = repositoryCustom;
    }

    public ProductDTO save(ProductDTO productDTO) {
        return mapperService.fromEntityToDto(repository.save(mapperService.fromDtoToEntity(productDTO)));
    }

    public ProductDTO update(ProductDTO productDTO, String id) throws ProductNotFound {
        Product product = repository.findById(id).orElseThrow(ProductNotFound::new);

        productDTO.setId(product.getId());

        return mapperService.fromEntityToDto(repository.save(mapperService.fromDtoToEntity(productDTO)));
    }

    public List<ProductDTO> findAll() {
        return mapperService.fromListEntityToListDto(repository.findAll());
    }

    public void deleteById(String id) throws ProductNotFound {
        repository.findById(id).orElseThrow(ProductNotFound::new);

        repository.deleteById(id);
    }

    public List<ProductDTO> findAllBySearch(String q, BigDecimal minPrice, BigDecimal maxPrice) {
        return mapperService.fromListEntityToListDto(repositoryCustom.findAllBySearch(q, minPrice, maxPrice));
    }
}
