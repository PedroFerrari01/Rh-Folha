package dao;

import model.Funcionario;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void salvar(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cpf, data_admissao, cargo_id, departamento_id, salario, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCpf());
            ps.setDate(3, Date.valueOf(f.getDataAdmissao()));
            ps.setInt(4, f.getCargoId());
            ps.setInt(5, f.getDepartamentoId());
            ps.setBigDecimal(6, f.getSalario());
            ps.setBoolean(7, f.isAtivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setId(rs.getInt(1));
            }
        }
    }

    public void desativar(int id) throws SQLException {
        String sql = "UPDATE funcionario SET ativo = FALSE WHERE id = ?";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Funcionario> listarTodos() throws SQLException {
        return listarComFiltro(null);
    }

    public List<Funcionario> listarAtivos() throws SQLException {
        return listarComFiltro(Boolean.TRUE);
    }

    private List<Funcionario> listarComFiltro(Boolean apenasAtivos) throws SQLException {
        String sql = """
                SELECT f.id, f.nome, f.cpf, f.data_admissao, f.cargo_id, f.departamento_id, f.salario, f.ativo,
                       c.nome AS nome_cargo, d.nome AS nome_departamento
                FROM funcionario f
                JOIN cargo c ON c.id = f.cargo_id
                JOIN departamento d ON d.id = f.departamento_id
                """ + (apenasAtivos != null ? "WHERE f.ativo = TRUE " : "") + "ORDER BY f.nome";
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Funcionario> listarPorDepartamento(int departamentoId) throws SQLException {
        String sql = """
                SELECT f.id, f.nome, f.cpf, f.data_admissao, f.cargo_id, f.departamento_id, f.salario, f.ativo,
                       c.nome AS nome_cargo, d.nome AS nome_departamento
                FROM funcionario f
                JOIN cargo c ON c.id = f.cargo_id
                JOIN departamento d ON d.id = f.departamento_id
                WHERE f.departamento_id = ? AND f.ativo = TRUE
                ORDER BY f.nome
                """;
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        String sql = """
                SELECT f.id, f.nome, f.cpf, f.data_admissao, f.cargo_id, f.departamento_id, f.salario, f.ativo,
                       c.nome AS nome_cargo, d.nome AS nome_departamento
                FROM funcionario f
                JOIN cargo c ON c.id = f.cargo_id
                JOIN departamento d ON d.id = f.departamento_id
                WHERE f.id = ?
                """;
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario(
                rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                rs.getDate("data_admissao").toLocalDate(),
                rs.getInt("cargo_id"), rs.getInt("departamento_id"),
                rs.getBigDecimal("salario"), rs.getBoolean("ativo")
        );
        f.setNomeCargo(rs.getString("nome_cargo"));
        f.setNomeDepartamento(rs.getString("nome_departamento"));
        return f;
    }
}
