package ui;

import dao.CargoDAO;
import model.Cargo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PainelCargos extends JPanel {

    private final CargoDAO dao = new CargoDAO();
    private final JTextField campoNome = new JTextField(18);
    private final JTextField campoSalario = new JTextField(10);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Salário base"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelCargos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Novo cargo"));
        form.add(new JLabel("Nome:"));
        form.add(campoNome);
        form.add(new JLabel("Salário base (R$):"));
        form.add(campoSalario);
        JButton botao = new JButton("Cadastrar");
        botao.addActionListener(e -> salvar());
        form.add(botao);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        atualizar();
    }

    private void salvar() {
        try {
            if (campoNome.getText().isBlank() || campoSalario.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Preencha nome e salário.");
                return;
            }
            BigDecimal salario = new BigDecimal(campoSalario.getText().trim().replace(",", "."));
            dao.salvar(new Cargo(campoNome.getText().trim(), salario));
            campoNome.setText("");
            campoSalario.setText("");
            atualizar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salário inválido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            modelo.setRowCount(0);
            List<Cargo> lista = dao.listarTodos();
            for (Cargo c : lista) {
                modelo.addRow(new Object[]{c.getId(), c.getNome(), "R$ " + c.getSalarioBase()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
