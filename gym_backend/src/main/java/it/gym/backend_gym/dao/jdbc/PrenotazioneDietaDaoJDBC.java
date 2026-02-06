package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.PrenotazioneDietaDAO;
import it.gym.backend_gym.entity.Dieta;
import it.gym.backend_gym.entity.PrenotazioneDieta;
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
public class PrenotazioneDietaDaoJDBC implements PrenotazioneDietaDAO {

    private final DataSource dataSource;

    public PrenotazioneDietaDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<PrenotazioneDieta> findByUtenteId(long utenteId) {
        String sql = "SELECT p.id AS p_id, p.created_at AS p_created_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "d.id AS d_id, d.nome AS d_nome, d.descrizione AS d_descrizione, d.appuntamento AS d_appuntamento, d.foto_url AS d_foto_url " +
                "FROM prenotazioni_diete p " +
                "JOIN utenti u ON u.id = p.utente_id " +
                "JOIN diete d ON d.id = p.dieta_id " +
                "WHERE p.utente_id = ? ORDER BY p.created_at DESC";
        List<PrenotazioneDieta> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrenotazioneDieta p = new PrenotazioneDieta();
                    p.setId(rs.getLong("p_id"));
                    p.setCreatedAt(rs.getTimestamp("p_created_at").toLocalDateTime());

                    Utente u = new Utente();
                    u.setId(rs.getLong("u_id"));
                    u.setEmail(rs.getString("u_email"));
                    u.setPasswordHash(rs.getString("u_password_hash"));
                    u.setRuolo(Ruolo.valueOf(rs.getString("u_ruolo")));
                    p.setUtente(u);

                    Dieta d = new Dieta();
                    d.setId(rs.getLong("d_id"));
                    d.setNome(rs.getString("d_nome"));
                    d.setDescrizione(rs.getString("d_descrizione"));
                    d.setAppuntamento(rs.getString("d_appuntamento"));
                    d.setFotoUrl(rs.getString("d_foto_url"));
                    p.setDieta(d);
                    out.add(p);
                }
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteId prenotazioni", e);
        }
    }

    @Override
    public PrenotazioneDieta create(PrenotazioneDieta prenotazione) {
        String sql = "INSERT INTO prenotazioni_diete(utente_id, dieta_id, created_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, prenotazione.getUtente().getId());
            ps.setLong(2, prenotazione.getDieta().getId());
            ps.setTimestamp(3, Timestamp.valueOf(prenotazione.getCreatedAt()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                prenotazione.setId(rs.getLong(1));
                return prenotazione;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create prenotazione", e);
        }
    }

    @Override
    public boolean exists(long utenteId, long dietaId) {
        String sql = "SELECT 1 FROM prenotazioni_diete WHERE utente_id = ? AND dieta_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, dietaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore exists prenotazione dieta", e);
        }
    }

    @Override
    public void deleteById(long prenotazioneId) {
        String sql = "DELETE FROM prenotazioni_diete WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, prenotazioneId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete prenotazione", e);
        }
    }

    @Override
    public void deleteByIdAndUtenteId(long prenotazioneId, long utenteId) {
        String sql = "DELETE FROM prenotazioni_diete WHERE id = ? AND utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, prenotazioneId);
            ps.setLong(2, utenteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete prenotazione utente", e);
        }
    }

    @Override
    public void deleteByUtenteId(long utenteId) {
        String sql = "DELETE FROM prenotazioni_diete WHERE utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore deleteByUtenteId prenotazioni", e);
        }
    }
}
