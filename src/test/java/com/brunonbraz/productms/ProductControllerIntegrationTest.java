package com.brunonbraz.productms;

import com.brunonbraz.productms.domain.Product;
import com.brunonbraz.productms.dto.ErrorDTO;
import com.brunonbraz.productms.dto.ProductDTO;
import com.brunonbraz.productms.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductMsApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    Product createProduct(){
        Product product = new Product();
        product.setName("Tesoura");
        product.setDescription("tesoura sem pontas");
        product.setPrice(BigDecimal.valueOf(5.00));

        return productRepository.save(product);
    }

    @Test
    public void givenProduct_whenSaveProduct_thenStatus200()
            throws Exception {
        ProductDTO product = new ProductDTO("Bola", "Bola da copa do mundo", BigDecimal.valueOf(20.00));

        MvcResult mvcResult = mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ProductDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDTO.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(product.getName(), result.getName());
    }

    @Test
    public void givenInvalidProduct_whenSaveProduct_thenStatus400()
            throws Exception {
        ProductDTO product = new ProductDTO("Bola", "", BigDecimal.valueOf(20.00));

        MvcResult mvcResult = mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode());
    }

    @Test
    public void whenGetProducts_thenStatus200()
            throws Exception {

        MvcResult mvcResult = mvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ProductDTO[] result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDTO[].class);

        Assertions.assertNotNull(result);

        if(result.length > 0){
            Assertions.assertTrue(Arrays.stream(result).anyMatch(productDTO -> productDTO.getName().equals("Bola")));
        }
    }

    @Test
    public void givenProductName_whenGetProductsBySearch_thenStatus200()
            throws Exception {

        Product product = createProduct();

        MvcResult mvcResult = mvc.perform(get("/products/search")
                .param("q", product.getName())
                .param("min_price", String.valueOf(BigDecimal.valueOf(2)))
                .param("max_price", String.valueOf(product.getPrice().add(BigDecimal.valueOf(20))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ProductDTO[] result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDTO[].class);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(Arrays.stream(result).anyMatch(productDTO -> productDTO.getName().equals(product.getName())));
    }

    @Test
    public void givenProductDoesNotExist_whenUpdateProducts_thenStatus404()
            throws Exception {

        ProductDTO product = new ProductDTO("Bola", "Bola da copa do mundo", BigDecimal.valueOf(20.00));

        mvc.perform(put("/products/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenProduct_whenUpdateProducts_thenStatus200()
            throws Exception {

        Product product = createProduct();

        ProductDTO productDTO = new ProductDTO("Piano Digital", "61 teclas", BigDecimal.valueOf(2000.00));

        MvcResult mvcResult = mvc.perform(put("/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ProductDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProductDTO.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(productDTO.getName(), result.getName());
        Assertions.assertNotEquals(product.getName(), result.getName());
    }

    @Test
    public void givenProductInvalid_whenUpdateProducts_thenStatus400()
            throws Exception {

        Product product = createProduct();

        ProductDTO productDTO = new ProductDTO("Piano Digital", "", BigDecimal.valueOf(2000.00));

        MvcResult mvcResult = mvc.perform(put("/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorDTO result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatusCode());
    }

    @Test
    public void givenProductId_whenDeleteProducts_thenStatus200()
            throws Exception {

        Product product = createProduct();

        mvc.perform(delete("/products/" + product.getId()))
                .andExpect(status().isOk());

        Product result = productRepository.findById(product.getId()).orElse(null);

        Assertions.assertNull(result);
    }

    @Test
    public void givenProductDoesNotExist_whenDeleteProducts_thenStatus404()
            throws Exception {

        mvc.perform(delete("/products/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
