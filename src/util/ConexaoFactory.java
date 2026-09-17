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
            if (in != null) {
                props.load(in);
            } else {
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/rh_folha?useSSL=false&serverTimezone=UTC");
                props.setProperty("db.user", "rh_app");
                props.setProperty("db.password", "rh123");
                props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            }
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
