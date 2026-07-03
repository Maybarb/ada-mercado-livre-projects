package br.com.ada.mscomprovantes.controller;

import br.com.ada.mscomprovantes.domain.dto.ComprovanteDetalheDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteRequestDto;
import br.com.ada.mscomprovantes.domain.dto.ComprovanteResponseDto;
import br.com.ada.mscomprovantes.service.ComprovanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/comprovantes")
@RequiredArgsConstructor
public class ComprovanteController {

    private final ComprovanteService comprovanteService;

    /**
     * POST /comprovantes
     * Membro 2 — valida o payload, gera UUID v4 e publica na fila RabbitMQ.
     * Retorna 202 Accepted imediatamente (processamento assíncrono).
     */
    @PostMapping
    public ResponseEntity<ComprovanteResponseDto> criarComprovante(
            @Valid @RequestBody ComprovanteRequestDto request) {

        ComprovanteResponseDto response = comprovanteService.criarComprovante(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * GET /comprovantes/{id}
     * Membro 3 — busca primeiramente no cache Redis.
     * Em caso de cache miss, busca no banco com até 3 tentativas.
     * Retorna 200 OK com os detalhes ou 404 Not Found após esgotar as tentativas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComprovanteDetalheDto> buscarComprovante(@PathVariable UUID id) {
        ComprovanteDetalheDto dto = comprovanteService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }
}
