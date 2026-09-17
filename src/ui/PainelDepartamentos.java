package ui;

import dao.DepartamentoDAO;
import model.Departamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelDepartamentos extends JPanel {

    private final DepartamentoDAO dao = new DepartamentoDAO();
    private final JTextField campoNome = new JTextField(20);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabela = new JTable(modelo);

    public PainelDepartamentos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Novo departamento"));
        form.add(new JLabel("Nome:"));
        form.add(campoNome);
        JButton botao = new JButton("Cadastrar");
        botao.addActionListener(e -> salvar());
        form.add(botao);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        atualizar();
    }

    private void salvar() {
        try {
            if (campoNome.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do departamento.");
                return;
            }
            dao.salvar(new Departamento(campoNome.getText().trim()));
            campoNome.setText("");
            atualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            modelo.setRowCount(0);
            List<Departamento> lista = dao.listarTodos();
            for (Departamento d : lista) {
                modelo.addRow(new Object[]{d.getId(), d.getNome()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
