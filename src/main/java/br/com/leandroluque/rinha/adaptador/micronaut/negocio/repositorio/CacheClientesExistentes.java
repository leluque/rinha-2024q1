package br.com.leandroluque.rinha.adaptador.micronaut.negocio.repositorio;

import br.com.leandroluque.rinha.negocio.repositorio.Contas;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

@Singleton
public class CacheClientesExistentes {
  private final Contas contas;
  private Flux<Long> idsClientesExistentes;

  public CacheClientesExistentes(Contas contas) {
    this.contas = contas;
    idsClientesExistentes = contas.obterIdsClientesExistentes().cache();
  }

  public Flux<Long> idsClientesExistentes() {
    return idsClientesExistentes;
  }
}
