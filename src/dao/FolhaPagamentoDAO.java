package dao;

import model.FolhaPagamento;
import util.ConexaoFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class FolhaPagamentoDAO {

    public boolean jaGerada(int funcionarioId, YearMonth mes) throws SQLException {
        String sql = "SELECT COUNT(*) FROM folha_pagamento WHERE funcionario_id = ? AND mes_referencia = ?";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, funcionarioId);
            ps.setString(2, mes.toString());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void salvar(FolhaPagamento f) throws SQLException {
        if (jaGerada(f.getFuncionarioId(), f.getMesReferencia())) {
            throw new IllegalStateException("Folha do mês " + f.getMesReferencia() + " já foi gerada para esse funcionário.");
        }
        String sql = """
                INSERT INTO folha_pagamento
                (funcionario_id, mes_referencia, salario_bruto, desconto_inss, desconto_irrf, desconto_vt, beneficio_va, salario_liquido, data_geracao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getFuncionarioId());
            ps.setString(2, f.getMesReferencia().toString());
            ps.setBigDecimal(3, f.getSalarioBruto());
            ps.setBigDecimal(4, f.getDescontoInss());
            ps.setBigDecimal(5, f.getDescontoIrrf());
            ps.setBigDecimal(6, f.getDescontoVt());
            ps.setBigDecimal(7, f.getBeneficioVa());
            ps.setBigDecimal(8, f.getSalarioLiquido());
            ps.setTimestamp(9, Timestamp.valueOf(f.getDataGeracao()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        }
    }

    public List<FolhaPagamento> listarPorMes(YearMonth mes) throws SQLException {
        String sql = """
                SELECT fp.*, f.nome AS nome_funcionario
                FROM folha_pagamento fp
                JOIN funcionario f ON f.id = fp.funcionario_id
                WHERE fp.mes_referencia = ?
                ORDER BY f.nome
                """;
        List<FolhaPagamento> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mes.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public BigDecimal custoTotalPorMes(YearMonth mes) throws SQLException {
        String sql = "SELECT COALESCE(SUM(salario_liquido), 0) AS total FROM folha_pagamento WHERE mes_referencia = ?";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mes.toString());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal("total");
            }
        }
    }

    private FolhaPagamento mapear(ResultSet rs) throws SQLException {
        FolhaPagamento f = new FolhaPagamento(
                rs.getInt("id"), rs.getInt("funcionario_id"),
                YearMonth.parse(rs.getString("mes_referencia")),
                rs.getBigDecimal("salario_bruto"), rs.getBigDecimal("desconto_inss"),
                rs.getBigDecimal("desconto_irrf"), rs.getBigDecimal("desconto_vt"),
                rs.getBigDecimal("beneficio_va"), rs.getBigDecimal("salario_liquido"),
                rs.getTimestamp("data_geracao").toLocalDateTime()
        );
        f.setNomeFuncionario(rs.getString("nome_funcionario"));
        return f;
    }
}
