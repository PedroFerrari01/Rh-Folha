package ui;

import dao.FolhaPagamentoDAO;
import dao.FuncionarioDAO;
import model.FolhaPagamento;
import model.Funcionario;
import util.CalculadoraFolha;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class PainelFolhaPagamento extends JPanel {

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final FolhaPagamentoDAO folhaDAO = new FolhaPagamentoDAO();

    private final JTextField campoMes = new JTextField(7);
    private final JTextField campoVa = new JTextField(8);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Funcionário", "Bruto", "INSS", "IRRF", "VT", "VA", "Líquido"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private final JLabel labelTotal = new JLabel("R$ 0,00");

    public PainelFolhaPagamento() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoMes.setText(YearMonth.now().toString());
        campoVa.setText("600.00");

        topo.add(new JLabel("Mês de referência (aaaa-mm):"));
        topo.add(campoMes);
        topo.add(new JLabel("Vale-alimentação (R$):"));
        topo.add(campoVa);

        JButton botaoGerarTodos = new JButton("Gerar folha de TODOS os ativos");
        botaoGerarTodos.addActionListener(e -> gerarParaTodos());
        topo.add(botaoGerarTodos);

        JButton botaoVisualizar = new JButton("Visualizar holerite selecionado");
        botaoVisualizar.addActionListener(e -> visualizarHolerite());
        topo.add(botaoVisualizar);

        JButton botaoAtualizar = new JButton("Atualizar lista do mês");
        botaoAtualizar.addActionListener(e -> atualizarTabela());
        topo.add(botaoAtualizar);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.add(new JLabel("Custo total da folha no mês:"));
        labelTotal.setFont(labelTotal.getFont().deriveFont(Font.BOLD, 15f));
        rodape.add(labelTotal);

        add(topo, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        atualizarTabela();
    }

    private void gerarParaTodos() {
        try {
            YearMonth mes = YearMonth.parse(campoMes.getText().trim());
            BigDecimal va = new BigDecimal(campoVa.getText().trim().replace(",", "."));

            List<Funcionario> ativos = funcionarioDAO.listarAtivos();
            if (ativos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum funcionário ativo cadastrado.");
                return;
            }

            int geradas = 0, jaExistiam = 0;
            for (Funcionario f : ativos) {
                if (folhaDAO.jaGerada(f.getId(), mes)) {
                    jaExistiam++;
                    continue;
                }
                BigDecimal bruto = f.getSalario();
                BigDecimal inss = CalculadoraFolha.calcularInss(bruto);
                BigDecimal baseIrrf = bruto.subtract(inss);
                BigDecimal irrf = CalculadoraFolha.calcularIrrf(baseIrrf);
                BigDecimal vt = CalculadoraFolha.calcularDescontoVt(bruto);
                BigDecimal liquido = bruto.subtract(inss).subtract(irrf).subtract(vt).add(va);

                FolhaPagamento folha = new FolhaPagamento(0, f.getId(), mes, bruto, inss, irrf, vt, va, liquido, LocalDateTime.now());
                folhaDAO.salvar(folha);
                geradas++;
            }

            atualizarTabela();
            JOptionPane.showMessageDialog(this, geradas + " folha(s) gerada(s). " +
                    (jaExistiam > 0 ? jaExistiam + " já existiam e foram ignoradas." : ""));
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Mês inválido. Use o formato aaaa-mm, ex: 2026-09.");
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void visualizarHolerite() {
        try {
            int linha = tabela.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma folha na tabela primeiro.");
                return;
            }
            YearMonth mes = YearMonth.parse(campoMes.getText().trim());
            List<FolhaPagamento> folhas = folhaDAO.listarPorMes(mes);
            FolhaPagamento folha = folhas.get(linha);

            JTextArea area = new JTextArea(folha.gerarHolerite());
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Holerite", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void atualizarTabela() {
        try {
            YearMonth mes = YearMonth.parse(campoMes.getText().trim());
            List<FolhaPagamento> folhas = folhaDAO.listarPorMes(mes);

            modelo.setRowCount(0);
            for (FolhaPagamento f : folhas) {
                modelo.addRow(new Object[]{
                        f.getId(), f.getNomeFuncionario(),
                        "R$ " + f.getSalarioBruto(), "R$ " + f.getDescontoInss(), "R$ " + f.getDescontoIrrf(),
                        "R$ " + f.getDescontoVt(), "R$ " + f.getBeneficioVa(), "R$ " + f.getSalarioLiquido()
                });
            }
            labelTotal.setText("R$ " + folhaDAO.custoTotalPorMes(mes));
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Mês inválido. Use o formato aaaa-mm, ex: 2026-09.");
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void mostrarErro(Exception ex) {
        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
