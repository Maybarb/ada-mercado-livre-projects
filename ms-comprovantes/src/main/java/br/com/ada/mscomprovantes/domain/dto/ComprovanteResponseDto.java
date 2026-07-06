package br.com.ada.mscomprovantes.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class ComprovanteResponseDto {

    @JsonProperty("identificador_comprovante")
    private UUID identificadorComprovante;

    @JsonProperty("data_hora_requisicao")
    private LocalDateTime dataHoraRequisicao;

    public ComprovanteResponseDto() {}

    public ComprovanteResponseDto(UUID identificadorComprovante, LocalDateTime dataHoraRequisicao) {
        this.identificadorComprovante = identificadorComprovante;
        this.dataHoraRequisicao = dataHoraRequisicao;
    }

    public UUID getIdentificadorComprovante() { return identificadorComprovante; }
    public void setIdentificadorComprovante(UUID identificadorComprovante) { this.identificadorComprovante = identificadorComprovante; }

    public LocalDateTime getDataHoraRequisicao() { return dataHoraRequisicao; }
    public void setDataHoraRequisicao(LocalDateTime dataHoraRequisicao) { this.dataHoraRequisicao = dataHoraRequisicao; }
}
