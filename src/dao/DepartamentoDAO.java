package dao;

import model.Departamento;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoDAO {

    public void salvar(Departamento departamento) throws SQLException {
        String sql = "INSERT INTO departamento (nome) VALUES (?)";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, departamento.getNome());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) departamento.setId(rs.getInt(1));
            }
        }
    }

    public List<Departamento> listarTodos() throws SQLException {
        String sql = "SELECT id, nome FROM departamento ORDER BY nome";
        List<Departamento> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Departamento(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return lista;
    }

    public Departamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome FROM departamento WHERE id = ?";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Departamento(rs.getInt("id"), rs.getString("nome")) : null;
            }
        }
    }
}
