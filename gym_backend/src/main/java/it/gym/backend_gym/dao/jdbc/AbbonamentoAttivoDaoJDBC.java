package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.AbbonamentoAttivoDAO;
import it.gym.backend_gym.entity.Abbonamento;
import it.gym.backend_gym.entity.AbbonamentoAttivo;
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
import java.util.Objects;

@Repository
public class AbbonamentoAttivoDaoJDBC implements AbbonamentoAttivoDAO {

    private final DataSource dataSource;

    public AbbonamentoAttivoDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AbbonamentoAttivo findByUtenteId(long utenteId) {
        String sql = "SELECT aa.id AS aa_id, aa.start_date AS aa_start_date, aa.end_date AS aa_end_date, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "ab.id AS ab_id, ab.durata AS ab_durata, ab.prezzo AS ab_prezzo " +
                "FROM abbonamenti_attivi aa " +
                "JOIN utenti u ON u.id = aa.utente_id " +
                "JOIN abbonamenti ab ON ab.id = aa.abbonamento_id " +
                "WHERE aa.utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, utenteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                AbbonamentoAttivo a = new AbbonamentoAttivo();
                a.setId(rs.getLong("aa_id"));
                Timestamp startTs = Objects.requireNonNull(rs.getTimestamp("aa_start_date"), "startDate");
                Timestamp endTs = Objects.requireNonNull(rs.getTimestamp("aa_end_date"), "endDate");
                a.setStartDate(startTs.toLocalDateTime());
                a.setEndDate(endTs.toLocalDateTime());

                Utente u = new Utente();
                u.setId(rs.getLong("u_id"));
                u.setEmail(rs.getString("u_email"));
                u.setPasswordHash(rs.getString("u_password_hash"));
                u.setRuolo(Ruolo.valueOf(rs.getString("u_ruolo")));
                a.setUtente(u);

                Abbonamento ab = new Abbonamento();
                ab.setId(rs.getLong("ab_id"));
                ab.setDurata(rs.getInt("ab_durata"));
                ab.setPrezzo(rs.getBigDecimal("ab_prezzo"));
                a.setAbbonamento(ab);
                return a;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteId abbonamento attivo", e);
        }
    }

    @Override
    public AbbonamentoAttivo upsert(AbbonamentoAttivo a) {
        AbbonamentoAttivo existing = findByUtenteId(a.getUtente().getId());
        if (existing == null) {
            String sql = "INSERT INTO abbonamenti_attivi(utente_id, abbonamento_id, start_date, end_date) VALUES (?, ?, ?, ?) RETURNING id";
            try (Connection con = dataSource.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, a.getUtente().getId());
                ps.setLong(2, a.getAbbonamento().getId());
                ps.setTimestamp(3, Timestamp.valueOf(Objects.requireNonNull(a.getStartDate(), "startDate")));
                ps.setTimestamp(4, Timestamp.valueOf(Objects.requireNonNull(a.getEndDate(), "endDate")));
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    a.setId(rs.getLong(1));
                    return a;
                }
            } catch (SQLException e) {
                throw new PersistenceException("Errore insert abbonamento attivo", e);
            }
        }

        String sql = "UPDATE abbonamenti_attivi SET abbonamento_id = ?, start_date = ?, end_date = ? WHERE utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, a.getAbbonamento().getId());
            ps.setTimestamp(2, Timestamp.valueOf(Objects.requireNonNull(a.getStartDate(), "startDate")));
            ps.setTimestamp(3, Timestamp.valueOf(Objects.requireNonNull(a.getEndDate(), "endDate")));
            ps.setLong(4, a.getUtente().getId());
            ps.executeUpdate();
            a.setId(existing.getId());
            return a;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update abbonamento attivo", e);
        }
    }

    @Override
    public void deleteByUtenteId(long utenteId) {
        String sql = "DELETE FROM abbonamenti_attivi WHERE utente_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete abbonamento attivo", e);
        }
    }
}
