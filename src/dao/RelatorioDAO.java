package dao;

import util.ConexaoFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RelatorioDAO {

    public Map<String, Long> headcountPorDepartamento() throws SQLException {
        String sql = """
                SELECT d.nome AS departamento, COUNT(*) AS total
                FROM funcionario f
                JOIN departamento d ON d.id = f.departamento_id
                WHERE f.ativo = TRUE
                GROUP BY d.nome
                ORDER BY total DESC
                """;
        return executarAgregacao(sql);
    }

    public Map<String, BigDecimal> custoFolhaPorDepartamento(String mesReferencia) throws SQLException {
        String sql = """
                SELECT d.nome AS departamento, COALESCE(SUM(fp.salario_liquido), 0) AS total
                FROM funcionario f
                JOIN departamento d ON d.id = f.departamento_id
                LEFT JOIN folha_pagamento fp ON fp.funcionario_id = f.id AND fp.mes_referencia = ?
                GROUP BY d.nome
                ORDER BY total DESC
                """;
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mesReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.put(rs.getString("departamento"), rs.getBigDecimal("total"));
                }
            }
        }
        return resultado;
    }

    private Map<String, Long> executarAgregacao(String sql) throws SQLException {
        Map<String, Long> resultado = new LinkedHashMap<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.put(rs.getString(1), rs.getLong("total"));
            }
        }
        return resultado;
    }
}
