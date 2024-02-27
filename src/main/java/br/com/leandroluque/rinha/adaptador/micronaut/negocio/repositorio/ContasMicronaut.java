package br.com.leandroluque.rinha.adaptador.micronaut.negocio.repositorio;

import br.com.leandroluque.rinha.negocio.dominio.Transacao;
import br.com.leandroluque.rinha.negocio.repositorio.Contas;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

@Singleton
public class ContasMicronaut implements Contas {
  private final ContaRepositorioR2DBC contaRepositorioR2DBC;

  public ContasMicronaut(ContaRepositorioR2DBC contaRepositorioR2DBC) {
    this.contaRepositorioR2DBC = contaRepositorioR2DBC;
  }

  public Flux<Long> obterIdsClientesExistentes() { return contaRepositorioR2DBC.findIdsClientesExistentes(); }
  @Override
  public Flux<Transacao> obterTransacoesDaContaParaExtrato(long idCliente) {
    return contaRepositorioR2DBC.obterTransacoesDaContaParaExtrato(idCliente);
  }
}
