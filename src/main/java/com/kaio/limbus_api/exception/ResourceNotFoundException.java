package com.kaio.limbus_api.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String entidade, Long id){
        super(entidade + " não encontrado(a) com id: " + id);
    }
}
