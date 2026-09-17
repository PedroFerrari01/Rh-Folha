package dao;

import model.Ausencia;
import model.TipoAusencia;
import util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AusenciaDAO {

    public void salvar(Ausencia a) throws SQLException {
        String sql = "INSERT INTO ausencia (funcionario_id, tipo, data_inicio, data_fim, observacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getFuncionarioId());
            ps.setString(2, a.getTipo().name());
            ps.setDate(3, Date.valueOf(a.getDataInicio()));
            ps.setDate(4, Date.valueOf(a.getDataFim()));
            ps.setString(5, a.getObservacao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        }
    }

    public List<Ausencia> listarPorFuncionario(int funcionarioId) throws SQLException {
        String sql = "SELECT id, funcionario_id, tipo, data_inicio, data_fim, observacao FROM ausencia WHERE funcionario_id = ? ORDER BY data_inicio DESC";
        List<Ausencia> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, funcionarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Ausencia(
                            rs.getInt("id"), rs.getInt("funcionario_id"),
                            TipoAusencia.valueOf(rs.getString("tipo")),
                            rs.getDate("data_inicio").toLocalDate(),
                            rs.getDate("data_fim").toLocalDate(),
                            rs.getString("observacao")
                    ));
                }
            }
        }
        return lista;
    }
}
