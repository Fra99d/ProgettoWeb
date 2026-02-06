package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.AbbonamentoDAO;
import it.gym.backend_gym.entity.Abbonamento;
import it.gym.backend_gym.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AbbonamentoDaoJDBC implements AbbonamentoDAO {

    private final DataSource dataSource;

    public AbbonamentoDaoJDBC(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public List<Abbonamento> findAll() {
        String sql = "SELECT id, durata, prezzo FROM abbonamenti ORDER BY id";
        List<Abbonamento> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Abbonamento a = new Abbonamento();
                a.setId(rs.getLong("id"));
                a.setDurata(rs.getInt("durata"));
                a.setPrezzo(rs.getBigDecimal("prezzo"));
                out.add(a);
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findAll abbonamenti", e);
        }
    }

    @Override
    public Abbonamento findById(long id) {
        String sql = "SELECT id, durata, prezzo FROM abbonamenti WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Abbonamento a = new Abbonamento();
                a.setId(rs.getLong("id"));
                a.setDurata(rs.getInt("durata"));
                a.setPrezzo(rs.getBigDecimal("prezzo"));
                return a;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById abbonamento " + id, e);
        }
    }

    @Override
    public Abbonamento create(Abbonamento a) {
        String sql = "INSERT INTO abbonamenti(durata, prezzo) VALUES (?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getDurata());
            ps.setBigDecimal(2, a.getPrezzo());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                a.setId(rs.getLong(1));
                return a;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create abbonamento", e);
        }
    }

    @Override
    public Abbonamento update(Abbonamento a) {
        String sql = "UPDATE abbonamenti SET durata = ?, prezzo = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, a.getDurata());
            ps.setBigDecimal(2, a.getPrezzo());
            ps.setLong(3, a.getId());
            ps.executeUpdate();
            return a;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update abbonamento " + a.getId(), e);
        }
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM abbonamenti WHERE id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsById abbonamento " + id, e);
        }
    }

    @Override
    public boolean existsInAbbonamentiAttivi(long id) {
        String sql = "SELECT 1 FROM abbonamenti_attivi WHERE abbonamento_id = ? AND end_date > NOW() LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsInAbbonamentiAttivi " + id, e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM abbonamenti WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete abbonamento " + id, e);
        }
    }
}

