package br.com.leandroluque.rinha.negocio.servico;

import br.com.leandroluque.rinha.negocio.repositorio.dto.StatusTransacao;
import reactor.core.publisher.Mono;

// A ideia inicial era implementar casos de uso, mas visando reduzir o uso de memória, optou-se por uma
// camada de serviço.

public interface ContaServico {
  Mono<String> obterContaParaGeracaoExtrato(long idCliente);
  Mono<StatusTransacao> realizarTransacao(long idCliente, char tipo, long valorEmCentavos, String descricao);
}
