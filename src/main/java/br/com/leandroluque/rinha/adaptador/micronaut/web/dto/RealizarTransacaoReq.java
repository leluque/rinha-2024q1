package br.com.leandroluque.rinha.adaptador.micronaut.web.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RealizarTransacaoReq(String valor,
                                   String tipo,
                                   String descricao) {

  // Parte da validação deveria, por questão de qualidade, ser realizada na camada de negócio, mas
  // foi trazida para cá para otimizar
  public boolean eValido() {
    boolean eValido = (tipo != null && (tipo.equals("c") || tipo.equals("d"))) &&
            valor != null &&
            descricao != null && !descricao.isEmpty() && descricao.length() <= 10;
    if(!eValido) {
      return false;
    }

    try {
      Long.valueOf(valor);
    } catch(NumberFormatException e) {
      return false;
    }

    return true;
  }
}
