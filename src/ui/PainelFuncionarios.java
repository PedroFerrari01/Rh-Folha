package ui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelFuncionarios extends JPanel {

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final CargoDAO cargoDAO = new CargoDAO();
    private final DepartamentoDAO departamentoDAO = new DepartamentoDAO();
    private final AusenciaDAO ausenciaDAO = new AusenciaDAO();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField campoNome = new JTextField(18);
    private final JTextField campoCpf = new JTextField(14);
    private final JTextField campoAdmissao = new JTextField(10);
    private final JComboBox<Cargo> comboCargo = new JComboBox<>();
    private final JComboBox<Departamento> comboDepartamento = new JComboBox<>();
    private final JTextField campoSalario = new JTextField(10);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "CPF", "Cargo", "Departamento", "Salário", "Ativo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelFuncionarios() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(criarBarraAcoes(), BorderLayout.SOUTH);

        carregarCombos();
        atualizar();

        comboCargo.addActionListener(e -> preencherSalarioSugerido());
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Novo funcionário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; painel.add(campoNome, gbc);
        gbc.gridx = 2; painel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 3; painel.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painel.add(new JLabel("Admissão (dd/mm/aaaa):"), gbc);
        gbc.gridx = 1; painel.add(campoAdmissao, gbc);
        gbc.gridx = 2; painel.add(new JLabel("Salário (R$):"), gbc);
        gbc.gridx = 3; painel.add(campoSalario, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painel.add(new JLabel("Cargo:"), gbc);
        gbc.gridx = 1; painel.add(comboCargo, gbc);
        gbc.gridx = 2; painel.add(new JLabel("Departamento:"), gbc);
        gbc.gridx = 3; painel.add(comboDepartamento, gbc);

        JButton botao = new JButton("Cadastrar funcionário");
        botao.addActionListener(e -> salvar());
        gbc.gridx = 3; gbc.gridy = 3;
        painel.add(botao, gbc);

        return painel;
    }

    private JPanel criarBarraAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botaoAtivos = new JButton("Listar somente ativos");
        botaoAtivos.addActionListener(e -> atualizarAtivos());
        JButton botaoTodos = new JButton("Listar todos");
        botaoTodos.addActionListener(e -> atualizar());
        JButton botaoDesativar = new JButton("Desativar selecionado");
        botaoDesativar.addActionListener(e -> desativarSelecionado());
        JButton botaoAusencia = new JButton("Registrar férias/ausência");
        botaoAusencia.addActionListener(e -> registrarAusencia());
        JButton botaoHistorico = new JButton("Ver ausências do selecionado");
        botaoHistorico.addActionListener(e -> verAusencias());

        painel.add(botaoAtivos);
        painel.add(botaoTodos);
        painel.add(botaoDesativar);
        painel.add(botaoAusencia);
        painel.add(botaoHistorico);
        return painel;
    }

    private void carregarCombos() {
        try {
            comboCargo.removeAllItems();
            for (Cargo c : cargoDAO.listarTodos()) comboCargo.addItem(c);
            comboDepartamento.removeAllItems();
            for (Departamento d : departamentoDAO.listarTodos()) comboDepartamento.addItem(d);
            preencherSalarioSugerido();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void preencherSalarioSugerido() {
        Cargo cargo = (Cargo) comboCargo.getSelectedItem();
        if (cargo != null) {
            campoSalario.setText(cargo.getSalarioBase().toPlainString());
        }
    }

    private void salvar() {
        try {
            Cargo cargo = (Cargo) comboCargo.getSelectedItem();
            Departamento departamento = (Departamento) comboDepartamento.getSelectedItem();
            if (cargo == null || departamento == null) {
                JOptionPane.showMessageDialog(this, "Cadastre ao menos um cargo e um departamento antes.");
                return;
            }
            if (campoNome.getText().isBlank() || campoCpf.getText().isBlank() || campoAdmissao.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Nome, CPF e data de admissão são obrigatórios.");
                return;
            }
            LocalDate admissao = LocalDate.parse(campoAdmissao.getText().trim(), FMT);
            BigDecimal salario = new BigDecimal(campoSalario.getText().trim().replace(",", "."));

            Funcionario f = new Funcionario(campoNome.getText().trim(), campoCpf.getText().trim(),
                    admissao, cargo.getId(), departamento.getId(), salario);
            funcionarioDAO.salvar(f);

            campoNome.setText("");
            campoCpf.setText("");
            campoAdmissao.setText("");
            atualizar();
            JOptionPane.showMessageDialog(this, "Funcionário cadastrado!");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/mm/aaaa.");
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void atualizar() {
        try {
            carregarNaTabela(funcionarioDAO.listarTodos());
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void atualizarAtivos() {
        try {
            carregarNaTabela(funcionarioDAO.listarAtivos());
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void carregarNaTabela(List<Funcionario> lista) {
        modelo.setRowCount(0);
        for (Funcionario f : lista) {
            modelo.addRow(new Object[]{
                    f.getId(), f.getNome(), f.getCpf(), f.getNomeCargo(), f.getNomeDepartamento(),
                    "R$ " + f.getSalario(), f.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    private int obterFuncionarioSelecionadoId() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela primeiro.");
            return -1;
        }
        return (int) modelo.getValueAt(linha, 0);
    }

    private void desativarSelecionado() {
        try {
            int id = obterFuncionarioSelecionadoId();
            if (id == -1) return;
            funcionarioDAO.desativar(id);
            atualizar();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void registrarAusencia() {
        try {
            int id = obterFuncionarioSelecionadoId();
            if (id == -1) return;

            String[] tipos = {"FERIAS", "ATESTADO", "FALTA"};
            String tipoEscolhido = (String) JOptionPane.showInputDialog(this, "Tipo:", "Ausência",
                    JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipoEscolhido == null) return;

            String inicioStr = JOptionPane.showInputDialog(this, "Data início (dd/mm/aaaa):");
            if (inicioStr == null) return;
            String fimStr = JOptionPane.showInputDialog(this, "Data fim (dd/mm/aaaa):");
            if (fimStr == null) return;
            String obs = JOptionPane.showInputDialog(this, "Observação (opcional):");

            LocalDate inicio = LocalDate.parse(inicioStr.trim(), FMT);
            LocalDate fim = LocalDate.parse(fimStr.trim(), FMT);

            ausenciaDAO.salvar(new Ausencia(id, TipoAusencia.valueOf(tipoEscolhido), inicio, fim, obs));
            JOptionPane.showMessageDialog(this, "Ausência registrada!");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/mm/aaaa.");
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void verAusencias() {
        try {
            int id = obterFuncionarioSelecionadoId();
            if (id == -1) return;
            List<Ausencia> lista = ausenciaDAO.listarPorFuncionario(id);
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma ausência registrada para esse funcionário.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            lista.forEach(a -> sb.append(a).append("\n"));
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Histórico de ausências", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void mostrarErro(Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public void recarregarCombos() {
        carregarCombos();
    }
}
