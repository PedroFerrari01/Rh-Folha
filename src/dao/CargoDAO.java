package dao;

import model.Cargo;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CargoDAO {

    public void salvar(Cargo cargo) throws SQLException {
        String sql = "INSERT INTO cargo (nome, salario_base) VALUES (?, ?)";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cargo.getNome());
            ps.setBigDecimal(2, cargo.getSalarioBase());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cargo.setId(rs.getInt(1));
            }
        }
    }

    public List<Cargo> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, salario_base FROM cargo ORDER BY nome";
        List<Cargo> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Cargo buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, salario_base FROM cargo WHERE id = ?";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private Cargo mapear(ResultSet rs) throws SQLException {
        return new Cargo(rs.getInt("id"), rs.getString("nome"), rs.getBigDecimal("salario_base"));
    }
}
