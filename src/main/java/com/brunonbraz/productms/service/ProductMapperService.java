package com.brunonbraz.productms.service;

import com.brunonbraz.productms.domain.Product;
import com.brunonbraz.productms.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductMapperService {

    private final ModelMapper modelMapper = new ModelMapper();

    public Product fromDtoToEntity(ProductDTO dto) {
        return modelMapper.map(dto, Product.class);
    }

    public ProductDTO fromEntityToDto(Product entity) {
        return modelMapper.map(entity, ProductDTO.class);
    }

    public List<ProductDTO> fromListEntityToListDto(List<Product> list) {
        Type listType = new TypeToken<List<ProductDTO>>() {
        }.getType();
        return modelMapper.map(list, listType);
    }

}
