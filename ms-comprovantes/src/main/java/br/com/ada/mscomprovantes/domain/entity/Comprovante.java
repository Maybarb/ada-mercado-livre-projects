package br.com.ada.mscomprovantes.domain.entity;

import br.com.ada.mscomprovantes.domain.enums.TipoChavePix;
import br.com.ada.mscomprovantes.domain.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comprovantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
