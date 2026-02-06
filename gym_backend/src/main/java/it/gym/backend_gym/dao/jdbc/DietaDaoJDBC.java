package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.DietaDAO;
import it.gym.backend_gym.entity.Dieta;
import it.gym.backend_gym.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DietaDaoJDBC implements DietaDAO {

    private final DataSource dataSource;

    public DietaDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Dieta> findAll() {
        String sql = "SELECT id, nome, descrizione,appuntamento, foto_url FROM diete ORDER BY id";
        List<Dieta> out = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dieta d = new Dieta();
                d.setId(rs.getLong("id"));
                d.setNome(rs.getString("nome"));
                d.setDescrizione(rs.getString("descrizione"));
                d.setAppuntamento(rs.getString("appuntamento"));
                d.setFotoUrl(rs.getString("foto_url")); 
                out.add(d);
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findAll diete", e);
        }
    }

    @Override
    public Dieta findById(long id) {
        String sql = "SELECT id, nome, descrizione, appuntamento,foto_url FROM diete WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Dieta d = new Dieta();
                d.setId(rs.getLong("id"));
                d.setNome(rs.getString("nome"));
                d.setDescrizione(rs.getString("descrizione"));
                d.setAppuntamento(rs.getString("appuntamento"));
                d.setFotoUrl(rs.getString("foto_url")); 
                return d;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById dieta " + id, e);
        }
    }

    @Override
    public Dieta create(Dieta d) {
        String sql = "INSERT INTO diete(nome, descrizione, appuntamento, foto_url) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getNome());
            ps.setString(2, d.getDescrizione());
            ps.setString(3, d.getAppuntamento());
            ps.setString(4, d.getFotoUrl());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                d.setId(rs.getLong(1));
                return d;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create dieta", e);
        }
    }

    @Override
    public Dieta update(Dieta d) {
        String sql = "UPDATE diete SET nome = ?, descrizione = ?, appuntamento = ?, foto_url = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, d.getNome());
            ps.setString(2, d.getDescrizione());
            ps.setString(3, d.getAppuntamento());
            ps.setString(4, d.getFotoUrl()); 
            ps.setLong(5, d.getId());
            ps.executeUpdate();
            return d;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update dieta " + d.getId(), e);
        }
    }

    // resto invariato...
    @Override public boolean existsById(long id) { /* uguale */ 
        String sql = "SELECT 1 FROM diete WHERE id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new PersistenceException("Errore existsById dieta " + id, e); }
    }

    @Override public boolean existsByNomeIgnoreCase(String nome) { /* uguale */ 
        String sql = "SELECT 1 FROM diete WHERE lower(nome) = lower(?) LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new PersistenceException("Errore existsByNomeIgnoreCase dieta", e); }
    }

    @Override public boolean existsByNomeIgnoreCaseAndIdNot(String nome, long idNot) { /* uguale */
        String sql = "SELECT 1 FROM diete WHERE lower(nome) = lower(?) AND id <> ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setLong(2, idNot);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new PersistenceException("Errore existsByNomeIgnoreCaseAndIdNot dieta", e); }
    }

    @Override
    public boolean existsInPrenotazioni(long dietaId) {
        String sql = "SELECT 1 FROM prenotazioni_diete WHERE dieta_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, dietaId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsInPrenotazioni dieta " + dietaId, e);
        }
    }

    @Override
    public void delete(long id) { /* uguale */
        String sql = "DELETE FROM diete WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete dieta " + id, e);
        }
    }
}
