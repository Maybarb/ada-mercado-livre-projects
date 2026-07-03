package br.com.ada.mscomprovantes.domain.dto;

import br.com.ada.mscomprovantes.domain.enums.TipoChavePix;
import br.com.ada.mscomprovantes.domain.enums.TipoDocumento;
import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO retornado no GET /comprovantes/{id}.
 * Implementa Serializable para ser armazenado no Redis.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprovanteDetalheDto implements Serializable {

    @JsonProperty("identificador_comprovante")
    private UUID identificadorComprovante;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("tipo_documento")
    private TipoDocumento tipoDocumento;

    @JsonProperty("numero_documento")
    private String numeroDocumento;

    @JsonProperty("numero_agencia")
    private String numeroAgencia;

    @JsonProperty("numero_conta")
    private String numeroConta;

    @JsonProperty("digito_verificador_conta")
    private String digitoVerificadorConta;

    @JsonProperty("valor_transacao")
    private BigDecimal valorTransacao;

    @JsonProperty("tipo_chave_pix_destino")
    private TipoChavePix tipoChavePixDestino;

    @JsonProperty("chave_pix_destino")
    private String chavePixDestino;

    @JsonProperty("nome_cliente_destino")
    private String nomeClienteDestino;

    @JsonProperty("identificacao_pix")
    private String identificacaoPix;

    @JsonProperty("data_hora_transacao")
    private LocalDateTime dataHoraTransacao;

    @JsonProperty("data_hora_requisicao")
    private LocalDateTime dataHoraRequisicao;

    /**
     * Converte a entidade JPA para o DTO de detalhe.
     */
    public static ComprovanteDetalheDto fromEntity(Comprovante comprovante) {
        return ComprovanteDetalheDto.builder()
                .identificadorComprovante(comprovante.getId())
                .nome(comprovante.getNome())
                .tipoDocumento(comprovante.getTipoDocumento())
                .numeroDocumento(comprovante.getNumeroDocumento())
                .numeroAgencia(comprovante.getNumeroAgencia())
                .numeroConta(comprovante.getNumeroConta())
                .digitoVerificadorConta(comprovante.getDigitoVerificadorConta())
                .valorTransacao(comprovante.getValorTransacao())
                .tipoChavePixDestino(comprovante.getTipoChavePixDestino())
                .chavePixDestino(comprovante.getChavePixDestino())
                .nomeClienteDestino(comprovante.getNomeClienteDestino())
                .identificacaoPix(comprovante.getIdentificacaoPix())
                .dataHoraTransacao(comprovante.getDataHoraTransacao())
                .dataHoraRequisicao(comprovante.getDataHoraRequisicao())
                .build();
    }
}
