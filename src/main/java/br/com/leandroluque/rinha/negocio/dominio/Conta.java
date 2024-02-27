package br.com.leandroluque.rinha.negocio.dominio;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

// Foi criada como uma classe de domínio anêmica por conta do desempenho.
// Em um projeto real, eu traria a lógica de negócio para a classe de domínio.

@Serdeable
@MappedEntity
public record Conta(@Id long idCliente,
                    long limiteEmCentavos,
                    long saldoInicialEmCentavos,
                    long saldoAtualEmCentavos,
                    @Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.NONE) List<Transacao> transacoes) {
}