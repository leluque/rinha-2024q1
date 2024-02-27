package br.com.leandroluque.rinha.negocio.repositorio.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record RespostaTransacao (long limite, long saldo) {}
