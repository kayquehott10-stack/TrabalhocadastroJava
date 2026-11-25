public enum Categoria {
    ALIMENTACAO("Alimentacao", TipoLancamento.DESPESA),
    TRANSPORTE("Transporte", TipoLancamento.DESPESA),
    MORADIA("Moradia", TipoLancamento.DESPESA),
    SAUDE("Saude", TipoLancamento.DESPESA),
    EDUCACAO("Educacao", TipoLancamento.DESPESA),
    LAZER("Lazer", TipoLancamento.DESPESA),
    SALARIO("Salario", TipoLancamento.RECEITA),
    OUTROS("Outros", null); // null = serve para receita e despesa

    private final String nome;
    private final TipoLancamento tipoPermitido;

    Categoria(String nome, TipoLancamento tipoPermitido) {
        this.nome = nome;
        this.tipoPermitido = tipoPermitido;
    }

    public String getNome() {
        return nome;
    }

    // Verifica se a categoria e coerente com o tipo do lancamento
    public boolean aceita(TipoLancamento tipo) {
        return tipoPermitido == null || tipoPermitido == tipo;
    }
}