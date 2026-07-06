package br.com.ada.mscomprovantes.domain.dto;

import br.com.ada.mscomprovantes.domain.entity.Comprovante;
import br.com.ada.mscomprovantes.domain.enums.TipoChavePix;
import br.com.ada.mscomprovantes.domain.enums.TipoDocumento;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO retornado no GET /comprovantes/{id}.
 * Serializable para ser armazenado no Redis.
 */
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

    public ComprovanteDetalheDto() {}

    /** Converte a entidade JPA para o DTO de detalhe. */
    public static ComprovanteDetalheDto fromEntity(Comprovante c) {
        ComprovanteDetalheDto dto = new ComprovanteDetalheDto();
        dto.identificadorComprovante = c.getId();
        dto.nome                     = c.getNome();
        dto.tipoDocumento            = c.getTipoDocumento();
        dto.numeroDocumento          = c.getNumeroDocumento();
        dto.numeroAgencia            = c.getNumeroAgencia();
        dto.numeroConta              = c.getNumeroConta();
        dto.digitoVerificadorConta   = c.getDigitoVerificadorConta();
        dto.valorTransacao           = c.getValorTransacao();
        dto.tipoChavePixDestino      = c.getTipoChavePixDestino();
        dto.chavePixDestino          = c.getChavePixDestino();
        dto.nomeClienteDestino       = c.getNomeClienteDestino();
        dto.identificacaoPix         = c.getIdentificacaoPix();
        dto.dataHoraTransacao        = c.getDataHoraTransacao();
        dto.dataHoraRequisicao       = c.getDataHoraRequisicao();
        return dto;
    }

    public UUID getIdentificadorComprovante() { return identificadorComprovante; }
    public void setIdentificadorComprovante(UUID v) { this.identificadorComprovante = v; }

    public String getNome() { return nome; }
    public void setNome(String v) { this.nome = v; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento v) { this.tipoDocumento = v; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String v) { this.numeroDocumento = v; }

    public String getNumeroAgencia() { return numeroAgencia; }
    public void setNumeroAgencia(String v) { this.numeroAgencia = v; }

    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String v) { this.numeroConta = v; }

    public String getDigitoVerificadorConta() { return digitoVerificadorConta; }
    public void setDigitoVerificadorConta(String v) { this.digitoVerificadorConta = v; }

    public BigDecimal getValorTransacao() { return valorTransacao; }
    public void setValorTransacao(BigDecimal v) { this.valorTransacao = v; }

    public TipoChavePix getTipoChavePixDestino() { return tipoChavePixDestino; }
    public void setTipoChavePixDestino(TipoChavePix v) { this.tipoChavePixDestino = v; }

    public String getChavePixDestino() { return chavePixDestino; }
    public void setChavePixDestino(String v) { this.chavePixDestino = v; }

    public String getNomeClienteDestino() { return nomeClienteDestino; }
    public void setNomeClienteDestino(String v) { this.nomeClienteDestino = v; }

    public String getIdentificacaoPix() { return identificacaoPix; }
    public void setIdentificacaoPix(String v) { this.identificacaoPix = v; }

    public LocalDateTime getDataHoraTransacao() { return dataHoraTransacao; }
    public void setDataHoraTransacao(LocalDateTime v) { this.dataHoraTransacao = v; }

    public LocalDateTime getDataHoraRequisicao() { return dataHoraRequisicao; }
    public void setDataHoraRequisicao(LocalDateTime v) { this.dataHoraRequisicao = v; }
}
