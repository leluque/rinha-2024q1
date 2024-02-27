package br.com.leandroluque.rinha.adaptador.micronaut.web.dto;

import br.com.leandroluque.rinha.negocio.dominio.Transacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.List;

@Serdeable
@JsonInclude(JsonInclude.Include.ALWAYS)
public record GerarExtratoRes(Saldo saldo, @JsonProperty("ultimas_transacoes") List<Transacao> ultimasTransacoes) {
  @Serdeable
  public record Saldo(long total, @JsonProperty("data_extrato") Instant dataExtrato, long limite) {}
}
