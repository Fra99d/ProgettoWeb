package it.gym.backend_gym.service;

import it.gym.backend_gym.dao.DietaDAO;
import it.gym.backend_gym.dao.RecensioneDietaDAO;
import it.gym.backend_gym.dto.DietaDTO;
import it.gym.backend_gym.dto.RecensioneDietaDTO;
import it.gym.backend_gym.entity.Dieta;
import it.gym.backend_gym.entity.RecensioneDieta;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class DietaService {

    private final DietaDAO dao;
    private final RecensioneDietaDAO recensioneDietaDAO;

    public DietaService(DietaDAO dao, RecensioneDietaDAO recensioneDietaDAO) {
        this.dao = dao;
        this.recensioneDietaDAO = recensioneDietaDAO;
    }

    public List<DietaDTO> findAll() {
        return dao.findAll().stream().map(this::toDto).toList();
    }

    public DietaDTO create(String nome, String descrizione,String appuntamento, String fotoUrl) {
        String n = requireText(nome, "Nome");
        String d = requireText(descrizione, "Descrizione");
        String a = requireText(appuntamento, "Appuntamento");

        if (dao.existsByNomeIgnoreCase(n)) {
            throw new ResponseStatusException(CONFLICT, "Esiste gia' una dieta con questo nome");
        }
        Dieta saved = dao.create(new Dieta(n, d, a, cleanUrl(fotoUrl)));
        return toDto(saved);
    }

    public DietaDTO update(long id, String nome,String descrizione, String appuntamento, String fotoUrl) {
        Dieta d = dao.findById(id);
        if (d == null) {
            throw new ResponseStatusException(NOT_FOUND, "Dieta non trovata: " + id);
        }

        String n = requireText(nome, "Nome");
        String desc = requireText(descrizione, "Descrizione");
        String app = requireText(appuntamento, "Appuntamento");

        if (dao.existsByNomeIgnoreCaseAndIdNot(n, d.getId())) {
            throw new ResponseStatusException(CONFLICT, "Esiste gia' una dieta con questo nome");
        }

        d.setNome(n);
        d.setDescrizione(desc);
        d.setAppuntamento(app);
        d.setFotoUrl(cleanUrl(fotoUrl)); 
        return toDto(dao.update(d));
    }

    public void delete(long id) {
        if (!dao.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Dieta non trovata: " + id);
        }
        if (dao.existsInPrenotazioni(id)) {
            throw new ResponseStatusException(CONFLICT, "Dieta prenotata da un cliente");
        }
        dao.delete(id);
    }

    private DietaDTO toDto(Dieta d) {
        List<RecensioneDietaDTO> recensioni = recensioneDietaDAO.findByDietaId(d.getId())
                .stream()
                .map(this::toRecensioneDto)
                .toList();
        return new DietaDTO(d.getId(), d.getNome(), d.getDescrizione(), d.getAppuntamento(), d.getFotoUrl(), recensioni);
    }

    private RecensioneDietaDTO toRecensioneDto(RecensioneDieta r) {
        String dietaNome = r.getDieta() != null ? r.getDieta().getNome() : null;
        String utenteEmail = r.getUtente() != null ? r.getUtente().getEmail() : null;
        Long dietaId = r.getDieta() != null ? r.getDieta().getId() : null;
        Long utenteId = r.getUtente() != null ? r.getUtente().getId() : null;
        return new RecensioneDietaDTO(
                r.getId(),
                dietaId,
                dietaNome,
                utenteId,
                utenteEmail,
                r.getTesto(),
                toIso(r.getCreatedAt()),
                toIso(r.getUpdatedAt())
        );
    }

    private String toIso(LocalDateTime dateTime) {
        return Objects.requireNonNull(dateTime, "dateTime")
                .truncatedTo(ChronoUnit.MILLIS)
                .toString();
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " obbligatorio.");
        }
        return value.trim();
    }

    private String cleanUrl(String url) {
        if (url == null) return null;
        String trimmed = url.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}



