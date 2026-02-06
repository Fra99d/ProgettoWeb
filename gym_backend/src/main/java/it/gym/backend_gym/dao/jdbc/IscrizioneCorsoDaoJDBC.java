package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.IscrizioneCorsoDAO;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.entity.IscrizioneCorso;
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
public class IscrizioneCorsoDaoJDBC implements IscrizioneCorsoDAO {

    private final DataSource dataSource;

    public IscrizioneCorsoDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<IscrizioneCorso> findByUtenteId(long utenteId) {
        String sql = "SELECT ic.id AS ic_id, ic.created_at AS ic_created_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "c.id AS c_id, c.titolo AS c_titolo, c.descrizione AS c_descrizione, c.lezione AS c_lezione, c.foto_url AS c_foto_url " +
                "FROM iscrizioni_corsi ic " +
                "JOIN utenti u ON u.id = ic.utente_id " +
                "JOIN corsi c ON c.id = ic.corso_id " +
                "WHERE ic.utente_id = ? ORDER BY ic.id";
        List<IscrizioneCorso> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    IscrizioneCorso i = new IscrizioneCorso();
                    i.setId(rs.getLong("ic_id"));
                    i.setCreatedAt(rs.getTimestamp("ic_created_at").toLocalDateTime());

                    Utente u = new Utente();
                    u.setId(rs.getLong("u_id"));
                    u.setEmail(rs.getString("u_email"));
                    u.setPasswordHash(rs.getString("u_password_hash"));
                    u.setRuolo(Ruolo.valueOf(rs.getString("u_ruolo")));
                    i.setUtente(u);

                    Corso c = new Corso();
                    c.setId(rs.getLong("c_id"));
                    c.setTitolo(rs.getString("c_titolo"));
                    c.setDescrizione(rs.getString("c_descrizione"));
                    c.setLezione(rs.getString("c_lezione"));
                    c.setFotoUrl(rs.getString("c_foto_url"));
                    i.setCorso(c);
                    out.add(i);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteId iscrizioni", e);
        }
    }

    @Override
    public boolean exists(long utenteId, long corsoId) {
        String sql = "SELECT 1 FROM iscrizioni_corsi WHERE utente_id = ? AND corso_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, corsoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore exists iscrizione corso", e);
        }
    }

    @Override
    public IscrizioneCorso create(IscrizioneCorso iscrizione) {
        String sql = "INSERT INTO iscrizioni_corsi(utente_id, corso_id, created_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, iscrizione.getUtente().getId());
            ps.setLong(2, iscrizione.getCorso().getId());
            ps.setTimestamp(3, Timestamp.valueOf(iscrizione.getCreatedAt()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                iscrizione.setId(rs.getLong(1));
                return iscrizione;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create iscrizione corso", e);
        }
    }

    @Override
    public void delete(long utenteId, long corsoId) {
        String sql = "DELETE FROM iscrizioni_corsi WHERE utente_id = ? AND corso_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, corsoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete iscrizione corso", e);
        }
    }

    @Override
    public void deleteByUtenteId(long utenteId) {
        String sql = "DELETE FROM iscrizioni_corsi WHERE utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore deleteByUtenteId iscrizioni", e);
        }
    }
}
