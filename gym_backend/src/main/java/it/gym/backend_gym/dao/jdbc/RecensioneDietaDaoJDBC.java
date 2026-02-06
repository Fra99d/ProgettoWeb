package it.gym.backend_gym.dao.jdbc;

import it.gym.backend_gym.dao.RecensioneDietaDAO;
import it.gym.backend_gym.entity.Dieta;
import it.gym.backend_gym.entity.RecensioneDieta;
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
public class RecensioneDietaDaoJDBC implements RecensioneDietaDAO {

    private final DataSource dataSource;

    public RecensioneDietaDaoJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<RecensioneDieta> findByDietaId(long dietaId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "d.id AS d_id, d.nome AS d_nome, d.descrizione AS d_descrizione, d.appuntamento AS d_appuntamento, d.foto_url AS d_foto_url " +
                "FROM recensioni_diete r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN diete d ON d.id = r.dieta_id " +
                "WHERE r.dieta_id = ? ORDER BY r.updated_at DESC";
        List<RecensioneDieta> out = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, dietaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(map(rs));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByDietaId recensioni diete", e);
        }
    }

    @Override
    public List<RecensioneDieta> findByUtenteId(long utenteId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "d.id AS d_id, d.nome AS d_nome, d.descrizione AS d_descrizione, d.appuntamento AS d_appuntamento, d.foto_url AS d_foto_url " +
                "FROM recensioni_diete r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN diete d ON d.id = r.dieta_id " +
                "WHERE r.utente_id = ? ORDER BY r.updated_at DESC";
        List<RecensioneDieta> out = new ArrayList<>();
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
            throw new PersistenceException("Errore findByUtenteId recensioni diete", e);
        }
    }

    @Override
    public RecensioneDieta findByUtenteIdAndDietaId(long utenteId, long dietaId) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "d.id AS d_id, d.nome AS d_nome, d.descrizione AS d_descrizione, d.appuntamento AS d_appuntamento, d.foto_url AS d_foto_url " +
                "FROM recensioni_diete r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN diete d ON d.id = r.dieta_id " +
                "WHERE r.utente_id = ? AND r.dieta_id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, dietaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findByUtenteIdAndDietaId recensioni diete", e);
        }
    }

    @Override
    public RecensioneDieta findById(long id) {
        String sql = "SELECT r.id AS r_id, r.testo AS r_testo, r.created_at AS r_created_at, r.updated_at AS r_updated_at, " +
                "u.id AS u_id, u.email AS u_email, u.password_hash AS u_password_hash, u.ruolo AS u_ruolo, " +
                "d.id AS d_id, d.nome AS d_nome, d.descrizione AS d_descrizione, d.appuntamento AS d_appuntamento, d.foto_url AS d_foto_url " +
                "FROM recensioni_diete r " +
                "JOIN utenti u ON u.id = r.utente_id " +
                "JOIN diete d ON d.id = r.dieta_id " +
                "WHERE r.id = ? LIMIT 1";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore findById recensione dieta", e);
        }
    }

    @Override
    public RecensioneDieta create(RecensioneDieta recensione) {
        String sql = "INSERT INTO recensioni_diete(utente_id, dieta_id, testo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, recensione.getUtente().getId());
            ps.setLong(2, recensione.getDieta().getId());
            ps.setString(3, recensione.getTesto());
            ps.setTimestamp(4, Timestamp.valueOf(recensione.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.valueOf(recensione.getUpdatedAt()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                recensione.setId(rs.getLong(1));
                return recensione;
            }
        } catch (SQLException e) {
            throw new PersistenceException("Errore create recensione dieta", e);
        }
    }

    @Override
    public RecensioneDieta update(RecensioneDieta recensione) {
        String sql = "UPDATE recensioni_diete SET testo = ?, updated_at = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, recensione.getTesto());
            ps.setTimestamp(2, Timestamp.valueOf(recensione.getUpdatedAt()));
            ps.setLong(3, recensione.getId());
            ps.executeUpdate();
            return recensione;
        } catch (SQLException e) {
            throw new PersistenceException("Errore update recensione dieta", e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM recensioni_diete WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete recensione dieta", e);
        }
    }

    @Override
    public void deleteByUtenteIdAndDietaId(long utenteId, long dietaId) {
        String sql = "DELETE FROM recensioni_diete WHERE utente_id = ? AND dieta_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, utenteId);
            ps.setLong(2, dietaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("Errore delete recensione dieta utente", e);
        }
    }

    private RecensioneDieta map(ResultSet rs) throws SQLException {
        RecensioneDieta r = new RecensioneDieta();
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

        Dieta d = new Dieta();
        d.setId(rs.getLong("d_id"));
        d.setNome(rs.getString("d_nome"));
        d.setDescrizione(rs.getString("d_descrizione"));
        d.setAppuntamento(rs.getString("d_appuntamento"));
        d.setFotoUrl(rs.getString("d_foto_url"));
        r.setDieta(d);
        return r;
    }
}
