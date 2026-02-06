package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.RecensioneCorsoDAO;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.entity.RecensioneCorso;
import it.gym.backend_gym.entity.Ruolo;
import it.gym.backend_gym.entity.Utente;
import it.gym.backend_gym.persistence.PersistenceException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecensioneCorsoDaoJDBC implements RecensioneCorsoDAO {

    private final DataSource dataSource;

    public RecensioneCorsoDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<RecensioneCorso> findByCorsoId(long corsoId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.lezione AS c_lezione, c.foto_url AS c_foto_url " +
                "FROM recensioni_corsi r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN corsi c ON c.id = r.corso_id " +
                "WHERE r.corso_id = ? ORDER BY r.updated_at DESC";
        List<RecensioneCorso> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByCorsoId recensioni corsi", e);
        }
    }

    @Override
    public List<RecensioneCorso> findByUtenteId(long utenteId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.lezione AS c_lezione, c.foto_url AS c_foto_url " +
                "FROM recensioni_corsi r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN corsi c ON c.id = r.corso_id " +
                "WHERE r.utente_id = ? ORDER BY r.updated_at DESC";
        List<RecensioneCorso> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteId recensioni corsi", e);
        }
    }

    @Override
    public RecensioneCorso findByUtenteIdAndCorsoId(long utenteId, long corsoId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.lezione AS c_lezione, c.foto_url AS c_foto_url " +
                "FROM recensioni_corsi r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN corsi c ON c.id = r.corso_id " +
                "WHERE r.utente_id = ? AND r.corso_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteIdAndCorsoId recensioni corsi", e);
        }
    }

    @Override
    public RecensioneCorso findById(long id) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.lezione AS c_lezione, c.foto_url AS c_foto_url " +
                "FROM recensioni_corsi r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN corsi c ON c.id = r.corso_id " +
                "WHERE r.id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById recensione corso", e);
        }
    }

    @Override
    public RecensioneCorso create(RecensioneCorso recensione) {
        String sql = "INSERT INTO recensioni_corsi(utente_id, corso_id, testo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, recensione.getUtente().getId());
            ps.setLong(2, recensione.getCorso().getId());
            ps.setString(3, recensione.getTesto());
            ps.setTimestamp(4, Timestamp.valueOf(recensione.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.valueOf(recensione.getUpdatedAt()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                recensione.setId(rs.getLong(1));
                return recensione;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create recensione corso", e);
        }
    }

    @Override
    public RecensioneCorso update(RecensioneCorso recensione) {
        String sql = "UPDATE recensioni_corsi SET testo = ?, updated_at = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recensione.getTesto());
            ps.setTimestamp(2, Timestamp.valueOf(recensione.getUpdatedAt()));
            ps.setLong(3, recensione.getId());
            ps.executeUpdate();
            return recensione;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update recensione corso", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM recensioni_corsi WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete recensione corso", e);
        }
    }

    @Override
    public void deleteByUtenteIdAndCorsoId(long utenteId, long corsoId) {
        String sql = "DELETE FROM recensioni_corsi WHERE utente_id = ? AND corso_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, corsoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete recensione corso utente", e);
        }
    }

    private RecensioneCorso map(ResultSet rs) throws SQLException {
        RecensioneCorso r = new RecensioneCorso();
        r.setId(rs.getLong("r_id"));
        r.setTesto(rs.getString("r_testo"));
        r.setCreatedAt(rs.getTimestamp("r_created_at").toLocalDateTime());
        r.setUpdatedAt(rs.getTimestamp("r_updated_at").toLocalDateTime());

        Utente u = new Utente();
        u.setId(rs.getLong("u_id"));
        u.setEmail(rs.getString("u_email"));
        u.setPasswordHash(rs.getString("u_password_hash"));
        u.setRuolo(Ruolo.valueOf(rs.getString("u_ruolo")));
        r.setUtente(u);

        Corso c = new Corso();
        c.setId(rs.getLong("c_id"));
        c.setTitolo(rs.getString("c_titolo"));
        c.setDescrizione(rs.getString("c_descrizione"));
        c.setLezione(rs.getString("c_lezione"));
        c.setFotoUrl(rs.getString("c_foto_url"));
        r.setCorso(c);
        return r;
    }
}
