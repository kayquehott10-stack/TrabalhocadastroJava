import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ControleFinanceiro controle = new ControleFinanceiro();
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public static void main(String[] args) {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");
            System.out.println();

            switch (opcao) {
                case 1:
                    cadastrar();
                    break;
                case 2:
                    listar();
                    break;
                case 3:
                    consultarPorId();
                    break;
                case 4:
                    alterar();
                    break;
                case 5:
                    excluir();
                    break;
                case 6:
                    filtrarPorTipo();
                    break;
                case 7:
                    consultarPorPeriodo();
                    break;
                case 8:
                    exibirResumo();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema. Ate logo!");
                    break;
                default:
                    System.out.println("Opcao invalida! Escolha um numero do menu.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    // ---------------------------------------------------------------- MENU

    private static void exibirMenu() {
        System.out.println();
        System.out.println("===== CONTROLE FINANCEIRO PESSOAL =====");
        System.out.println("1 - Cadastrar lancamento");
        System.out.println("2 - Listar lancamentos");
        System.out.println("3 - Consultar por identificador");
        System.out.println("4 - Alterar lancamento");
        System.out.println("5 - Excluir lancamento");
        System.out.println("6 - Filtrar por tipo");
        System.out.println("7 - Consultar por periodo");
        System.out.println("8 - Exibir resumo financeiro");
        System.out.println("0 - Sair");
        System.out.println("=======================================");
    }

    // ---------------------------------------------------------- OPERACOES

    private static void cadastrar() {
        System.out.println("--- Cadastrar lancamento ---");
        String descricao = lerDescricao();
        BigDecimal valor = lerValor();
        LocalDate data = lerData("Data (ex.: 25/11/2026): ");
        TipoLancamento tipo = lerTipo();
        Categoria categoria = lerCategoria(tipo);

        Lancamento novo = controle.adicionarLancamento(descricao, valor, data, tipo, categoria);
        if (novo != null) {
            System.out.println("Lancamento cadastrado com sucesso! ID: " + novo.getId());
        } else {
            System.out.println("Nao foi possivel cadastrar o lancamento (dados invalidos).");
        }
    }

    private static void listar() {
        System.out.println("--- Lista de lancamentos ---");
        exibirLista(controle.listarLancamentos());
    }

    private static void consultarPorId() {
        System.out.println("--- Consultar por identificador ---");
        int id = lerInteiro("Informe o ID: ");
        Lancamento l = controle.buscarPorId(id);
        if (l == null) {
            System.out.println("Nenhum lancamento encontrado com o ID " + id + ".");
        } else {
            System.out.println(l);
        }
    }

    private static void alterar() {
        System.out.println("--- Alterar lancamento ---");
        int id = lerInteiro("Informe o ID do lancamento: ");
        Lancamento atual = controle.buscarPorId(id);
        if (atual == null) {
            System.out.println("O ID " + id + " nao existe.");
            return;
        }

        System.out.println("Dados atuais: " + atual);
        System.out.println("Informe os novos dados:");
        String descricao = lerDescricao();
        BigDecimal valor = lerValor();
        LocalDate data = lerData("Data (ex.: 25/11/2026): ");
        TipoLancamento tipo = lerTipo();
        Categoria categoria = lerCategoria(tipo);

        if (controle.alterarLancamento(id, descricao, valor, data, tipo, categoria)) {
            System.out.println("Lancamento alterado com sucesso!");
            System.out.println(controle.buscarPorId(id));
        } else {
            System.out.println("Nao foi possivel alterar o lancamento (dados invalidos).");
        }
    }

    private static void excluir() {
        System.out.println("--- Excluir lancamento ---");
        int id = lerInteiro("Informe o ID do lancamento: ");
        if (controle.excluirLancamento(id)) {
            System.out.println("Lancamento excluido com sucesso!");
        } else {
            System.out.println("O ID " + id + " nao existe.");
        }
    }

    private static void filtrarPorTipo() {
        System.out.println("--- Filtrar por tipo ---");
        TipoLancamento tipo = lerTipo();
        exibirLista(controle.filtrarPorTipo(tipo));
    }

    private static void consultarPorPeriodo() {
        System.out.println("--- Consultar por periodo ---");
        LocalDate inicio = lerData("Data inicial (dd/mm/aaaa): ");
        LocalDate fim = lerData("Data final (dd/mm/aaaa): ");

        if (inicio.isAfter(fim)) {
            System.out.println("Periodo invalido: a data inicial nao pode ser posterior a data final.");
            return;
        }
        exibirLista(controle.consultarPorPeriodo(inicio, fim));
    }

    private static void exibirResumo() {
        System.out.println("--- Resumo financeiro ---");
        System.out.println(String.format(PT_BR, "Total de receitas: R$ %,.2f", controle.calcularTotalReceitas()));
        System.out.println(String.format(PT_BR, "Total de despesas: R$ %,.2f", controle.calcularTotalDespesas()));
        System.out.println(String.format(PT_BR, "Saldo:             R$ %,.2f", controle.calcularSaldo()));
    }

    private static void exibirLista(ArrayList<Lancamento> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum lancamento encontrado.");
            return;
        }
        for (Lancamento l : lista) {
            System.out.println(l);
        }
    }

    // ------------------------------------------------------------ LEITURAS

    private static int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        String texto = scanner.nextLine().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1; // valor invalido: o chamador trata como opcao/ID inexistente
        }
    }

    private static String lerDescricao() {
        String descricao;
        do {
            System.out.print("Descricao: ");
            descricao = scanner.nextLine().trim();
            if (descricao.isEmpty()) {
                System.out.println("A descricao nao pode ser vazia.");
            }
        } while (descricao.isEmpty());
        return descricao;
    }

    private static BigDecimal lerValor() {
        while (true) {
            System.out.print("Valor (ex.: 150,50): ");
            String texto = scanner.nextLine().trim().toUpperCase().replace("R$", "").replace(" ", "");
            if (texto.contains(",")) {
                // formato brasileiro: 1.500,50 -> 1500.50
                texto = texto.replace(".", "").replace(",", ".");
            }
            try {
                BigDecimal valor = new BigDecimal(texto);
                if (valor.compareTo(BigDecimal.ZERO) > 0) {
                    return valor;
                }
                System.out.println("O valor deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Digite apenas numeros (ex.: 150.50).");
            }
        }
    }

    private static LocalDate lerData(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = scanner.nextLine().trim().replace("-", "/").replace(".", "/").replace(" ", "");
            try {
                return LocalDate.parse(texto, Lancamento.FORMATO_ENTRADA);
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida. Use o formato dd/mm/aaaa com uma data real.");
            }
        }
    }

    private static TipoLancamento lerTipo() {
        while (true) {
            System.out.println("Tipo: 1 - Receita | 2 - Despesa");
            int opcao = lerInteiro("Escolha o tipo: ");
            if (opcao == 1) {
                return TipoLancamento.RECEITA;
            } else if (opcao == 2) {
                return TipoLancamento.DESPESA;
            }
            System.out.println("Tipo invalido. Digite 1 ou 2.");
        }
    }

    private static Categoria lerCategoria(TipoLancamento tipo) {
        // Mostra apenas as categorias coerentes com o tipo escolhido
        ArrayList<Categoria> opcoes = new ArrayList<>();
        for (Categoria c : Categoria.values()) {
            if (c.aceita(tipo)) {
                opcoes.add(c);
            }
        }

        while (true) {
            System.out.println("Categorias disponiveis:");
            for (int i = 0; i < opcoes.size(); i++) {
                System.out.println((i + 1) + " - " + opcoes.get(i).getNome());
            }
            int opcao = lerInteiro("Escolha a categoria: ");
            if (opcao >= 1 && opcao <= opcoes.size()) {
                return opcoes.get(opcao - 1);
            }
            System.out.println("Categoria invalida.");
        }
    }
}