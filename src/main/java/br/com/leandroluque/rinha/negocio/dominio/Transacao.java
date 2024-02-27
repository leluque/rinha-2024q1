package br.com.leandroluque.rinha.negocio.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

// Foi criada como uma classe de domínio anêmica por conta do desempenho.
// Em um projeto real, eu traria a lógica de negócio para a classe de domínio.
// Ainda, foi adicionada configuração de JSON para evitar criar objetos DTO extras
// para transações -  visando reduzir o uso de memória.
@Serdeable
@MappedEntity
public record Transacao(
        @JsonIgnore long idCliente,
        @JsonProperty("realizada_em") Instant realizadaEm,
        String tipo,
        @JsonProperty("valor") long valorEmCentavos,
        String descricao) {
}