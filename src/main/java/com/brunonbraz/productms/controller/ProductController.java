package com.brunonbraz.productms.controller;

import com.brunonbraz.productms.dto.ErrorDTO;
import com.brunonbraz.productms.dto.ProductDTO;
import com.brunonbraz.productms.exception.ProductNotFound;
import com.brunonbraz.productms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("")
    public ResponseEntity<Object> save(@RequestBody @Valid ProductDTO productDTO){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productDTO));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody @Valid ProductDTO productDTO, @PathVariable("id") String id){
        try{
            return ResponseEntity.ok(productService.update(productDTO, id));
        } catch (ProductNotFound e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<Object> findAll(){
        try{
            return ResponseEntity.ok(productService.findAll());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllBySearch(@RequestParam(name = "q", required = false) String q,
                                                  @RequestParam(name = "min_price", required = false) BigDecimal minPrice,
                                                  @RequestParam(name = "max_price", required = false) BigDecimal maxPrice){
        try{
            return ResponseEntity.ok(productService.findAllBySearch(q, minPrice, maxPrice));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") String id){
        try{
            productService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (ProductNotFound e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}
