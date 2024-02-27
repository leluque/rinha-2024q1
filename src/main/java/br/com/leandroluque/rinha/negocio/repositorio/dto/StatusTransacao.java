package br.com.leandroluque.rinha.negocio.repositorio.dto;

public record StatusTransacao(StatusTransacao.Status status, RespostaTransacao respostaTransacao) {
    private static final StatusTransacao CLIENTE_INEXISTENTE = new StatusTransacao(Status.CLIENTE_INEXISTENTE, null);
    private static final StatusTransacao SALDO_INSUFICIENTE = new StatusTransacao(Status.SALDO_INSUFICIENTE, null);
    public static StatusTransacao clienteInexistente() {
        return CLIENTE_INEXISTENTE;
    }
    public static StatusTransacao saldoInsuficiente() {
        return SALDO_INSUFICIENTE;
    }
    public static StatusTransacao sucesso(RespostaTransacao respostaTransacao) {
        return new StatusTransacao(Status.SUCESSO, respostaTransacao);
    }

    public enum Status {
        CLIENTE_INEXISTENTE, SALDO_INSUFICIENTE, SUCESSO
    }
}
