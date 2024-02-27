package br.com.leandroluque.rinha.adaptador.micronaut.web;

import br.com.leandroluque.rinha.adaptador.micronaut.web.dto.RealizarTransacaoReq;
import br.com.leandroluque.rinha.negocio.repositorio.dto.RespostaTransacao;
import br.com.leandroluque.rinha.negocio.repositorio.dto.StatusTransacao;
import br.com.leandroluque.rinha.negocio.servico.ContaServico;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import reactor.core.publisher.Mono;

@Controller("/clientes")
public class ContaControladora {
  private final ContaServico contaServico;

  public ContaControladora(ContaServico contaServico) {
    this.contaServico = contaServico;
  }

  @Get("/{idCliente}/extrato")
  public Mono<MutableHttpResponse<String>> gerarExtrato(long idCliente) {
    return contaServico.obterContaParaGeracaoExtrato(idCliente)
            .map(extrato -> HttpResponse.ok(extrato))
            .switchIfEmpty(Mono.just(HttpResponse.notFound()));
  }

  @Post("/{idCliente}/transacoes")
  public Mono<MutableHttpResponse<RespostaTransacao>> realizarTransacao(@PathVariable Long idCliente, @Body RealizarTransacaoReq transacaoDTO) {
    if (!transacaoDTO.eValido()) {
      return Mono.just(HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    return contaServico.realizarTransacao(idCliente, transacaoDTO.tipo().toLowerCase().charAt(0), Long.valueOf(transacaoDTO.valor()), transacaoDTO.descricao())
            .map(statusTransacao -> {
              if(statusTransacao.status() == StatusTransacao.Status.SUCESSO) {
                return HttpResponse.ok(statusTransacao.respostaTransacao());
              } else if(statusTransacao.status() == StatusTransacao.Status.SALDO_INSUFICIENTE) {
                return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY);
              } else {
                return HttpResponse.status(HttpStatus.NOT_FOUND);
              }
            });
  }
}
