package br.com.leandroluque.rinha.negocio.repositorio;

import br.com.leandroluque.rinha.negocio.dominio.Transacao;
import reactor.core.publisher.Flux;

public interface Contas {
  Flux<Long> obterIdsClientesExistentes();
  Flux<Transacao> obterTransacoesDaContaParaExtrato(long idCliente);
}
