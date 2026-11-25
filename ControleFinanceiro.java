import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class ControleFinanceiro {

    private final ArrayList<Lancamento> lancamentos = new ArrayList<>();
    private int proximoId = 1;

    // Valida descricao, valor e categoria (RN01)
    private boolean dadosValidos(String descricao, BigDecimal valor, LocalDate data,
                                 TipoLancamento tipo, Categoria categoria) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return false;
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (data == null || tipo == null || categoria == null) {
            return false;
        }
        return categoria.aceita(tipo);
    }

    // RF01 / RN02: retorna o lancamento criado, ou null se os dados forem invalidos
    public Lancamento adicionarLancamento(String descricao, BigDecimal valor, LocalDate data,
                                          TipoLancamento tipo, Categoria categoria) {
        if (!dadosValidos(descricao, valor, data, tipo, categoria)) {
            return null;
        }
        Lancamento novo = new Lancamento(proximoId, descricao.trim(), valor, data, tipo, categoria);
        proximoId++;
        lancamentos.add(novo);
        return novo;
    }

    // RF02
    public ArrayList<Lancamento> listarLancamentos() {
        return new ArrayList<>(lancamentos);
    }

    // RF03: retorna null se nao existir
    public Lancamento buscarPorId(int id) {
        for (Lancamento l : lancamentos) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    // RF04 / RN03: retorna false se o id nao existir ou os dados forem invalidos
    public boolean alterarLancamento(int id, String descricao, BigDecimal valor, LocalDate data,
                                     TipoLancamento tipo, Categoria categoria) {
        Lancamento l = buscarPorId(id);
        if (l == null || !dadosValidos(descricao, valor, data, tipo, categoria)) {
            return false;
        }
        l.setDescricao(descricao.trim());
        l.setValor(valor);
        l.setData(data);
        l.setTipo(tipo);
        l.setCategoria(categoria);
        return true;
    }

    // RF05 / RN03
    public boolean excluirLancamento(int id) {
        Lancamento l = buscarPorId(id);
        if (l == null) {
            return false;
        }
        lancamentos.remove(l);
        return true;
    }

    // RF06
    public ArrayList<Lancamento> filtrarPorTipo(TipoLancamento tipo) {
        ArrayList<Lancamento> resultado = new ArrayList<>();
        for (Lancamento l : lancamentos) {
            if (l.getTipo() == tipo) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    // RF07: periodo inclusivo (inicio e fim entram na consulta)
    public ArrayList<Lancamento> consultarPorPeriodo(LocalDate inicio, LocalDate fim) {
        ArrayList<Lancamento> resultado = new ArrayList<>();
        for (Lancamento l : lancamentos) {
            if (!l.getData().isBefore(inicio) && !l.getData().isAfter(fim)) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    // RF08
    public BigDecimal calcularTotalReceitas() {
        return somarPorTipo(TipoLancamento.RECEITA);
    }

    public BigDecimal calcularTotalDespesas() {
        return somarPorTipo(TipoLancamento.DESPESA);
    }

    // RN04
    public BigDecimal calcularSaldo() {
        return calcularTotalReceitas().subtract(calcularTotalDespesas());
    }

    private BigDecimal somarPorTipo(TipoLancamento tipo) {
        BigDecimal total = BigDecimal.ZERO;
        for (Lancamento l : lancamentos) {
            if (l.getTipo() == tipo) {
                total = total.add(l.getValor());
            }
        }
        return total;
    }
}