package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoFactory {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConexaoFactory.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "Arquivo src/config.properties não encontrado. Copie o config.properties.example e preencha com os dados do seu MySQL.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível carregar config.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(props.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC não encontrado no classpath: " + props.getProperty("db.driver"), e);
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}
