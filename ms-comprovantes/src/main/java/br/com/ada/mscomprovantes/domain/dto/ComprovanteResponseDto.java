package br.com.ada.mscomprovantes.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprovanteResponseDto {

    @JsonProperty("identificador_comprovante")
    private UUID identificadorComprovante;

    @JsonProperty("data_hora_requisicao")
    private LocalDateTime dataHoraRequisicao;
}
