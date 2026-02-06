package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.UtenteDAO;
import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import it.gym.backend_gym.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UtenteDaoJDBC implements UtenteDAO {

    private final DataSource dataSource;

    public UtenteDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Utente findByEmail(String email) {
        String sql = "SELECT id, email, password_hash, ruolo FROM utenti WHERE email = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Utente u = new Utente();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                return u;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByEmail utente " + email, e);
        }
    }

    @Override
    public Utente findById(long id) {
        String sql = "SELECT id, email, password_hash, ruolo FROM utenti WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Utente u = new Utente();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                return u;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById utente " + id, e);
        }
    }

    @Override
    public Utente create(Utente u) {
        String sql = "INSERT INTO utenti(email, password_hash, ruolo) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRuolo().name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                u.setId(rs.getLong(1));
                return u;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create utente", e);
        }
    }

    @Override
    public Utente update(Utente u) {
        String sql = "UPDATE utenti SET email = ?, password_hash = ?, ruolo = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRuolo().name());
            ps.setLong(4, u.getId());
            ps.executeUpdate();
            return u;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update utente " + u.getId(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM utenti WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete utente " + id, e);
        }
    }

    @Override
    public List<Utente> findAll() {
        String sql = "SELECT id, email, password_hash, ruolo FROM utenti ORDER BY id";
        List<Utente> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Utente u = new Utente();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRuolo(Ruolo.valueOf(rs.getString("ruolo")));
                out.add(u);
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findAll utenti", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM utenti WHERE email = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsByEmail utente", e);
        }
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, long idNot) {
        String sql = "SELECT 1 FROM utenti WHERE email = ? AND id <> ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setLong(2, idNot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore existsByEmailAndIdNot utente", e);
        }
    }
}
