package com.brunonbraz.productms.exception;

public class ProductNotFound extends Exception{

    public ProductNotFound() {
        super("Produto não encontrado");
    }
}
