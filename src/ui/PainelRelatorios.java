package ui;

import dao.RelatorioDAO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public class PainelRelatorios extends JPanel {

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final JTextField campoMes = new JTextField(7);
    private final JTextArea areaHeadcount = new JTextArea(10, 30);
    private final JTextArea areaCusto = new JTextArea(10, 30);

    public PainelRelatorios() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        campoMes.setText(YearMonth.now().toString());
        topo.add(new JLabel("Mês de referência (aaaa-mm):"));
        topo.add(campoMes);
        JButton botao = new JButton("Gerar relatórios");
        botao.addActionListener(e -> gerar());
        topo.add(botao);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 10));
        areaHeadcount.setEditable(false);
        areaCusto.setEditable(false);

        JPanel painelHead = new JPanel(new BorderLayout());
        painelHead.setBorder(BorderFactory.createTitledBorder("Funcionários ativos por departamento"));
        painelHead.add(new JScrollPane(areaHeadcount), BorderLayout.CENTER);

        JPanel painelCusto = new JPanel(new BorderLayout());
        painelCusto.setBorder(BorderFactory.createTitledBorder("Custo da folha por departamento (mês)"));
        painelCusto.add(new JScrollPane(areaCusto), BorderLayout.CENTER);

        centro.add(painelHead);
        centro.add(painelCusto);

        add(topo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        gerar();
    }

    private void gerar() {
        try {
            Map<String, Long> headcount = relatorioDAO.headcountPorDepartamento();
            StringBuilder sbHead = new StringBuilder();
            headcount.forEach((dep, qtd) -> sbHead.append(String.format("%-25s %d%n", dep, qtd)));
            areaHeadcount.setText(sbHead.length() > 0 ? sbHead.toString() : "Nenhum funcionário ativo.");

            Map<String, BigDecimal> custo = relatorioDAO.custoFolhaPorDepartamento(campoMes.getText().trim());
            StringBuilder sbCusto = new StringBuilder();
            custo.forEach((dep, total) -> sbCusto.append(String.format("%-25s R$ %.2f%n", dep, total)));
            areaCusto.setText(sbCusto.length() > 0 ? sbCusto.toString() : "Nenhum custo registrado nesse mês.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
