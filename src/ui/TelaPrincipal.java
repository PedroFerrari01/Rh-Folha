package ui;

import javax.swing.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        super("RH Folha - Sistema de Gestão de RH e Folha de Pagamento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        PainelDepartamentos painelDepartamentos = new PainelDepartamentos();
        PainelCargos painelCargos = new PainelCargos();
        PainelFuncionarios painelFuncionarios = new PainelFuncionarios();
        PainelFolhaPagamento painelFolha = new PainelFolhaPagamento();
        PainelRelatorios painelRelatorios = new PainelRelatorios();

        abas.addTab("Departamentos", painelDepartamentos);
        abas.addTab("Cargos", painelCargos);
        abas.addTab("Funcionários", painelFuncionarios);
        abas.addTab("Folha de Pagamento", painelFolha);
        abas.addTab("Relatórios", painelRelatorios);

        abas.addChangeListener(e -> {
            if (abas.getSelectedComponent() == painelFuncionarios) {
                painelFuncionarios.recarregarCombos();
            }
        });

        add(abas);
    }
}
