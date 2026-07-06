package br.com.ada.mscomprovantes.domain.entity;

import br.com.ada.mscomprovantes.domain.enums.TipoChavePix;
import br.com.ada.mscomprovantes.domain.enums.TipoDocumento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comprovantes")
public class Comprovante {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false)
    private String numeroDocumento;

    @Column(name = "numero_agencia", nullable = false)
    private String numeroAgencia;

    @Column(name = "numero_conta", nullable = false)
    private String numeroConta;

    @Column(name = "digito_verificador_conta", nullable = false)
    private String digitoVerificadorConta;

    @Column(name = "valor_transacao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTransacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_chave_pix_destino", nullable = false)
    private TipoChavePix tipoChavePixDestino;

    @Column(name = "chave_pix_destino", nullable = false)
    private String chavePixDestino;

    @Column(name = "nome_cliente_destino", nullable = false)
    private String nomeClienteDestino;

    @Column(name = "identificacao_pix", length = 500)
    private String identificacaoPix;

    @Column(name = "data_hora_transacao", nullable = false)
    private LocalDateTime dataHoraTransacao;

    @Column(name = "data_hora_requisicao", nullable = false)
    private LocalDateTime dataHoraRequisicao;

    public Comprovante() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNumeroAgencia() { return numeroAgencia; }
    public void setNumeroAgencia(String numeroAgencia) { this.numeroAgencia = numeroAgencia; }

    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public String getDigitoVerificadorConta() { return digitoVerificadorConta; }
    public void setDigitoVerificadorConta(String digitoVerificadorConta) { this.digitoVerificadorConta = digitoVerificadorConta; }

    public BigDecimal getValorTransacao() { return valorTransacao; }
    public void setValorTransacao(BigDecimal valorTransacao) { this.valorTransacao = valorTransacao; }

    public TipoChavePix getTipoChavePixDestino() { return tipoChavePixDestino; }
    public void setTipoChavePixDestino(TipoChavePix tipoChavePixDestino) { this.tipoChavePixDestino = tipoChavePixDestino; }

    public String getChavePixDestino() { return chavePixDestino; }
    public void setChavePixDestino(String chavePixDestino) { this.chavePixDestino = chavePixDestino; }

    public String getNomeClienteDestino() { return nomeClienteDestino; }
    public void setNomeClienteDestino(String nomeClienteDestino) { this.nomeClienteDestino = nomeClienteDestino; }

    public String getIdentificacaoPix() { return identificacaoPix; }
    public void setIdentificacaoPix(String identificacaoPix) { this.identificacaoPix = identificacaoPix; }

    public LocalDateTime getDataHoraTransacao() { return dataHoraTransacao; }
    public void setDataHoraTransacao(LocalDateTime dataHoraTransacao) { this.dataHoraTransacao = dataHoraTransacao; }

    public LocalDateTime getDataHoraRequisicao() { return dataHoraRequisicao; }
    public void setDataHoraRequisicao(LocalDateTime dataHoraRequisicao) { this.dataHoraRequisicao = dataHoraRequisicao; }
}
