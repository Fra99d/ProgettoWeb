package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.CorsoDAO;
import it.gym.backend_gym.dao.RecensioneCorsoDAO;
import it.gym.backend_gym.dao.proxy.CorsoProxy;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CorsoDaoJDBC implements CorsoDAO {

    private final DataSource dataSource;
    private final RecensioneCorsoDAO recensioneCorsoDAO;

    public CorsoDaoJDBC(DataSource dataSource, RecensioneCorsoDAO recensioneCorsoDAO) {
        this.dataSource = dataSource;
        this.recensioneCorsoDAO = recensioneCorsoDAO;
    }

    @Override
    public List<Corso> findAll() {
        String sql = "SELECT id, titolo, descrizione, lezione, foto_url FROM corsi ORDER BY id";
        List<Corso> out = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findAll corsi", e);
        }
    }

    @Override
    public Corso findById(long id) {
        String sql = "SELECT id, titolo, descrizione, lezione, foto_url FROM corsi WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return map(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById corso " + id, e);
        }
    }

    @Override
    public Corso create(Corso c) {
        String sql = "INSERT INTO corsi(titolo, descrizione, lezione, foto_url) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getTitolo());
            ps.setString(2, c.getDescrizione());
            ps.setString(3, c.getLezione());
            ps.setString(4, c.getFotoUrl());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                c.setId(rs.getLong(1));
                return c;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create corso", e);
        }
    }

    @Override
    public Corso update(Corso c) {
        String sql = "UPDATE corsi SET titolo = ?, descrizione = ?, lezione = ?, foto_url = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getTitolo());
            ps.setString(2, c.getDescrizione());
            ps.setString(3, c.getLezione());
            ps.setString(4, c.getFotoUrl());
            ps.setLong(5, c.getId());

            ps.executeUpdate();
            return c;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update corso " + c.getId(), e);
        }
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT 1 FROM corsi WHERE id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsById corso " + id, e);
        }
    }

    @Override
    public boolean existsByTitoloIgnoreCase(String titolo) {
        String sql = "SELECT 1 FROM corsi WHERE lower(titolo) = lower(?) LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titolo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsByTitoloIgnoreCase corso", e);
        }
    }

    @Override
    public boolean existsByTitoloIgnoreCaseAndIdNot(String titolo, long idNot) {
        String sql = "SELECT 1 FROM corsi WHERE lower(titolo) = lower(?) AND id <> ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, titolo);
            ps.setLong(2, idNot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsByTitoloIgnoreCaseAndIdNot corso", e);
        }
    }

    @Override
    public boolean existsInIscrizioni(long corsoId) {
        String sql = "SELECT 1 FROM iscrizioni_corsi WHERE corso_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsInIscrizioni corso " + corsoId, e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM corsi WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete corso " + id, e);
        }
    }

    private Corso map(ResultSet rs) throws SQLException {
        CorsoProxy c = new CorsoProxy(recensioneCorsoDAO);
        c.setId(rs.getLong("id"));
        c.setTitolo(rs.getString("titolo"));
        c.setDescrizione(rs.getString("descrizione"));
        c.setLezione(rs.getString("lezione"));
        c.setFotoUrl(rs.getString("foto_url"));
        return c;
    }
}


