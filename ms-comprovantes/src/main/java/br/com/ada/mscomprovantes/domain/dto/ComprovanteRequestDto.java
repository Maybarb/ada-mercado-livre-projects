package br.com.ada.mscomprovantes.domain.dto;

import br.com.ada.mscomprovantes.domain.enums.TipoChavePix;
import br.com.ada.mscomprovantes.domain.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprovanteRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    @JsonProperty("nome")
    private String nome;

    @NotNull(message = "Tipo de documento é obrigatório")
    @JsonProperty("tipo_documento")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "Número do documento é obrigatório")
    @JsonProperty("numero_documento")
    private String numeroDocumento;

    @NotBlank(message = "Número da agência é obrigatório")
    @JsonProperty("numero_agencia")
    private String numeroAgencia;

    @NotBlank(message = "Número da conta é obrigatório")
    @JsonProperty("numero_conta")
    private String numeroConta;

    @NotBlank(message = "Dígito verificador da conta é obrigatório")
    @JsonProperty("digito_verificador_conta")
    private String digitoVerificadorConta;

    @NotNull(message = "Valor da transação é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor da transação deve ser maior que zero")
    @JsonProperty("valor_transacao")
    private BigDecimal valorTransacao;

    @NotNull(message = "Tipo da chave PIX destino é obrigatório")
    @JsonProperty("tipo_chave_pix_destino")
    private TipoChavePix tipoChavePixDestino;

    @NotBlank(message = "Chave PIX destino é obrigatória")
    @JsonProperty("chave_pix_destino")
    private String chavePixDestino;

    @NotBlank(message = "Nome do cliente destino é obrigatório")
    @JsonProperty("nome_cliente_destino")
    private String nomeClienteDestino;

    @JsonProperty("identificacao_pix")
    private String identificacaoPix;

    @NotNull(message = "Data e hora da transação é obrigatória")
    @JsonProperty("data_hora_transacao")
    private LocalDateTime dataHoraTransacao;
}
