package br.com.ada.mscomprovantes.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ComprovanteNaoEncontradoException extends RuntimeException {

    public ComprovanteNaoEncontradoException(String message) {
        super(message);
    }
}
