import ui.TelaPrincipal;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // se o Look and Feel do sistema não estiver disponível, usa o padrão do Swing
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}
