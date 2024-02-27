package br.com.leandroluque.rinha.adaptador.micronaut.negocio.repositorio;

import br.com.leandroluque.rinha.negocio.dominio.Conta;
import br.com.leandroluque.rinha.negocio.dominio.Transacao;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@R2dbcRepository(dialect = Dialect.POSTGRES)
public interface ContaRepositorioR2DBC extends ReactiveStreamsCrudRepository<Conta, Long> {
  @Query("SELECT id_cliente FROM conta")
  Flux<Long> findIdsClientesExistentes();
  Mono<Conta> findByIdCliente(long idCliente);
  @Query("SELECT * FROM transacao WHERE id_cliente = :idCliente ORDER BY realizada_em DESC LIMIT 10")
  Flux<Transacao> obterTransacoesDaContaParaExtrato(long idCliente);
}