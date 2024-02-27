package br.com.leandroluque.rinha.adaptador.micronaut.negocio.servico;

import br.com.leandroluque.rinha.adaptador.micronaut.negocio.repositorio.CacheClientesExistentes;
import br.com.leandroluque.rinha.adaptador.micronaut.negocio.repositorio.ContaRepositorioR2DBC;
import br.com.leandroluque.rinha.adaptador.micronaut.web.dto.GerarExtratoRes;
import br.com.leandroluque.rinha.negocio.dominio.Conta;
import br.com.leandroluque.rinha.negocio.repositorio.dto.RespostaTransacao;
import br.com.leandroluque.rinha.negocio.repositorio.dto.StatusTransacao;
import br.com.leandroluque.rinha.negocio.servico.ContaServico;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.micronaut.data.r2dbc.operations.R2dbcOperations;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;

// A ideia inicial era implementar casos de uso, mas visando reduzir o uso de memória, optou-se por uma
// camada de serviço.

@Singleton
public class ContaServicoMicronaut implements ContaServico {
  private final ContaRepositorioR2DBC contaRepositorio;
  private final R2dbcOperations r2dbcOperations;
  private final CacheClientesExistentes cacheClientesExistentes;
  private final RedisReactiveCommands<String, String> redisReativo;
  private final ObjectMapper mapeadorObjeto;
  private static final String prefixoChaveExtrato = "extrato:";

  public ContaServicoMicronaut(ContaRepositorioR2DBC contaRepositorio, R2dbcOperations r2dbcOperations, CacheClientesExistentes cacheClientesExistentes, RedisClient clienteRedis, ObjectMapper mapeadorObjeto) {
    this.contaRepositorio = contaRepositorio;
    this.r2dbcOperations = r2dbcOperations;
    this.cacheClientesExistentes = cacheClientesExistentes;
    this.redisReativo = clienteRedis.connect().reactive();
    this.mapeadorObjeto = mapeadorObjeto;
  }

  @Override
  public Mono<String> obterContaParaGeracaoExtrato(long idCliente) {
    return cacheClientesExistentes.idsClientesExistentes()
            .any(id -> id == idCliente)
            .flatMap(existe -> {
              if (!existe) {
                return Mono.empty();
              }
              return redisReativo.get("%s%d".formatted(prefixoChaveExtrato, idCliente))
                      .switchIfEmpty(contaRepositorio.findByIdCliente(idCliente)
                              .flatMap(conta -> contaRepositorio.obterTransacoesDaContaParaExtrato(idCliente)
                                      .collectList()
                                      .map(transacoes -> new Conta(conta.idCliente(), conta.limiteEmCentavos(), conta.saldoInicialEmCentavos(),
                                              conta.saldoAtualEmCentavos(), transacoes))
                                      .flatMap(contaPreenchida -> {
                                        GerarExtratoRes extrato = new GerarExtratoRes(new GerarExtratoRes.Saldo(contaPreenchida.saldoAtualEmCentavos(), Instant.now(), contaPreenchida.limiteEmCentavos()),
                                                contaPreenchida.transacoes());
                                        try {
                                          String extratoTexto = mapeadorObjeto.writeValueAsString(extrato);
                                          return redisReativo.set("%s%d".formatted(prefixoChaveExtrato, idCliente), extratoTexto)
                                                  .thenReturn(extratoTexto);
                                        } catch (IOException e) {
                                          return Mono.error(new RuntimeException());
                                        }
                                      })));
            });
  }

  @Override
  public Mono<StatusTransacao> realizarTransacao(long idCliente, char tipo, long valorEmCentavos, String descricao) {
    return cacheClientesExistentes.idsClientesExistentes()
            .any(id -> id == idCliente)
            .flatMap(existe -> {
              if (!existe) {
                return Mono.just(StatusTransacao.clienteInexistente());
              }
              return Mono.from(r2dbcOperations.withConnection(connection ->
                      Mono.from(connection.createStatement("CALL atualizar_saldo_e_inserir_transacao($1, $2, $3, $4, NULL)")
                              .bind("$1", idCliente)
                              .bind("$2", valorEmCentavos)
                              .bind("$3", tipo)
                              .bind("$4", descricao)
                              .fetchSize(1)
                              .execute()
                      ).flatMap(result -> Mono.from(result.map((row, rowMetadata) -> {
                        String retorno = row.get("_retorno", String.class);
                        if ("SI".equals(retorno)) {
                          return StatusTransacao.saldoInsuficiente();
                        }
                        String[] partes = retorno.split(":");
                        return StatusTransacao.sucesso(new RespostaTransacao(Integer.parseInt(partes[1]), Integer.parseInt(partes[0])));
                      }))).single().flatMap(status -> {
                        if (status.status() == StatusTransacao.Status.SUCESSO) {
                          return redisReativo.del("%s%d".formatted(prefixoChaveExtrato, idCliente)).thenReturn(status);
                        }
                        return Mono.just(status);
                      })));
            });
  }
}