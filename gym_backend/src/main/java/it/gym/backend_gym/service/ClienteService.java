package it.gym.backend_gym.service;

import it.gym.backend_gym.dao.AbbonamentoAttivoDAO;
import it.gym.backend_gym.dao.AbbonamentoDAO;
import it.gym.backend_gym.dao.CorsoDAO;
import it.gym.backend_gym.dao.DietaDAO;
import it.gym.backend_gym.dao.IscrizioneCorsoDAO;
import it.gym.backend_gym.dao.PrenotazioneDietaDAO;
import it.gym.backend_gym.dao.RecensioneCorsoDAO;
import it.gym.backend_gym.dao.RecensioneDietaDAO;
import it.gym.backend_gym.dto.AbbonamentoAttivoDTO;
import it.gym.backend_gym.dto.ClienteDashboardDTO;
import it.gym.backend_gym.dto.CorsoDTO;
import it.gym.backend_gym.dto.DietaDTO;
import it.gym.backend_gym.dto.PrenotazioneDTO;
import it.gym.backend_gym.dto.RecensioneCorsoDTO;
import it.gym.backend_gym.dto.RecensioneDietaDTO;
import it.gym.backend_gym.entity.Abbonamento;
import it.gym.backend_gym.entity.AbbonamentoAttivo;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.entity.Dieta;
import it.gym.backend_gym.entity.IscrizioneCorso;
import it.gym.backend_gym.entity.PrenotazioneDieta;
import it.gym.backend_gym.entity.RecensioneCorso;
import it.gym.backend_gym.entity.RecensioneDieta;
import it.gym.backend_gym.entity.Utente;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ClienteService {

    private final AbbonamentoAttivoDAO abbonamentoAttivoDAO;
    private final AbbonamentoDAO abbonamentoDAO;
    private final IscrizioneCorsoDAO iscrizioneCorsoDAO;
    private final PrenotazioneDietaDAO prenotazioneDietaDAO;
    private final CorsoDAO corsoDAO;
    private final DietaDAO dietaDAO;
    private final RecensioneCorsoDAO recensioneCorsoDAO;
    private final RecensioneDietaDAO recensioneDietaDAO;

    public ClienteService(
            AbbonamentoAttivoDAO abbonamentoAttivoDAO,
            AbbonamentoDAO abbonamentoDAO,
            IscrizioneCorsoDAO iscrizioneCorsoDAO,
            PrenotazioneDietaDAO prenotazioneDietaDAO,
            CorsoDAO corsoDAO,
            DietaDAO dietaDAO,
            RecensioneCorsoDAO recensioneCorsoDAO,
            RecensioneDietaDAO recensioneDietaDAO
    ) {
        this.abbonamentoAttivoDAO = abbonamentoAttivoDAO;
        this.abbonamentoDAO = abbonamentoDAO;
        this.iscrizioneCorsoDAO = iscrizioneCorsoDAO;
        this.prenotazioneDietaDAO = prenotazioneDietaDAO;
        this.corsoDAO = corsoDAO;
        this.dietaDAO = dietaDAO;
        this.recensioneCorsoDAO = recensioneCorsoDAO;
        this.recensioneDietaDAO = recensioneDietaDAO;
    }

    public ClienteDashboardDTO getDashboard(long utenteId) {
        AbbonamentoAttivo attivo = getActiveOrClearExpired(utenteId);
        AbbonamentoAttivoDTO abbonamentoDTO = null;
        if (attivo != null) {
            Abbonamento abb = attivo.getAbbonamento();
            if (abb != null) {
                abbonamentoDTO = new AbbonamentoAttivoDTO(
                        attivo.getId(),
                        abb.getId(),
                        abb.getDurata(),
                        abb.getPrezzo(),
                        toIso(attivo.getStartDate()),
                        toIso(attivo.getEndDate())
                );
            }
        }

        List<CorsoDTO> corsi = new ArrayList<>();
        for (IscrizioneCorso i : iscrizioneCorsoDAO.findByUtenteId(utenteId)) {
            Corso c = i.getCorso();
            if (c != null) {
                corsi.add(new CorsoDTO(c.getId(), c.getTitolo(), c.getDescrizione(), c.getLezione(), c.getFotoUrl(), List.of()));
            }
        }

        List<PrenotazioneDTO> prenotazioni = new ArrayList<>();
        for (PrenotazioneDieta p : prenotazioneDietaDAO.findByUtenteId(utenteId)) {
            Dieta d = p.getDieta();
            if (d != null) {
                DietaDTO dietaDTO = new DietaDTO(d.getId(), d.getNome(), d.getDescrizione(), d.getAppuntamento(), d.getFotoUrl(), List.of());
                prenotazioni.add(new PrenotazioneDTO(p.getId(), dietaDTO, toIso(p.getCreatedAt())));
            }
        }

        List<RecensioneCorsoDTO> recensioniCorsi = recensioneCorsoDAO.findByUtenteId(utenteId)
                .stream()
                .map(this::toRecensioneCorsoDto)
                .toList();
        List<RecensioneDietaDTO> recensioniDiete = recensioneDietaDAO.findByUtenteId(utenteId)
                .stream()
                .map(this::toRecensioneDietaDto)
                .toList();

        return new ClienteDashboardDTO(
                abbonamentoDTO,
                corsi,
                prenotazioni,
                recensioniCorsi,
                recensioniDiete
        );
    }

    public ClienteDashboardDTO attivaAbbonamento(long utenteId, long abbonamentoId) {
        AbbonamentoAttivo current = getActiveOrClearExpired(utenteId);
        if (current != null) {
            throw new ResponseStatusException(CONFLICT, "Abbonamento gia' attivo");
        }
        Abbonamento abb = abbonamentoDAO.findById(abbonamentoId);
        if (abb == null) {
            throw new ResponseStatusException(NOT_FOUND, "Abbonamento non trovato: " + abbonamentoId);
        }

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMonths(abb.getDurata());
        Utente u = new Utente();
        u.setId(utenteId);
        AbbonamentoAttivo attivo = new AbbonamentoAttivo(u, abb, start, end);
        abbonamentoAttivoDAO.upsert(attivo);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO rimuoviAbbonamento(long utenteId) {
        clearAll(utenteId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO iscriviCorso(long utenteId, long corsoId) {
        ensureAbbonato(utenteId);
        Corso c = corsoDAO.findById(corsoId);
        if (c == null) {
            throw new ResponseStatusException(NOT_FOUND, "Corso non trovato: " + corsoId);
        }
        if (!iscrizioneCorsoDAO.exists(utenteId, corsoId)) {
            Utente u = new Utente();
            u.setId(utenteId);
            iscrizioneCorsoDAO.create(new IscrizioneCorso(u, c, LocalDateTime.now()));
        }
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO rimuoviCorso(long utenteId, long corsoId) {
        iscrizioneCorsoDAO.delete(utenteId, corsoId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO prenotaDieta(long utenteId, long dietaId) {
        ensureAbbonato(utenteId);
        Dieta d = dietaDAO.findById(dietaId);
        if (d == null) {
            throw new ResponseStatusException(NOT_FOUND, "Dieta non trovata: " + dietaId);
        }
        if (!prenotazioneDietaDAO.exists(utenteId, dietaId)) {
            Utente u = new Utente();
            u.setId(utenteId);
            prenotazioneDietaDAO.create(new PrenotazioneDieta(u, d, LocalDateTime.now()));
        }
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO annullaPrenotazione(long utenteId, long prenotazioneId) {
        prenotazioneDietaDAO.deleteByIdAndUtenteId(prenotazioneId, utenteId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO creaRecensioneCorso(long utenteId, long corsoId, String testo) {
        ensureIscrittoCorso(utenteId, corsoId);
        String clean = requireText(testo, "Testo");

        RecensioneCorso existing = recensioneCorsoDAO.findByUtenteIdAndCorsoId(utenteId, corsoId);
        if (existing != null) {
            throw new ResponseStatusException(CONFLICT, "Recensione gia' presente");
        }

        Utente u = new Utente();
        u.setId(utenteId);
        Corso c = new Corso();
        c.setId(corsoId);
        LocalDateTime now = LocalDateTime.now();
        recensioneCorsoDAO.create(new RecensioneCorso(u, c, clean, now, now));
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO aggiornaRecensioneCorso(long utenteId, long corsoId, String testo) {
        ensureIscrittoCorso(utenteId, corsoId);
        String clean = requireText(testo, "Testo");

        RecensioneCorso existing = recensioneCorsoDAO.findByUtenteIdAndCorsoId(utenteId, corsoId);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        existing.setTesto(clean);
        existing.setUpdatedAt(LocalDateTime.now());
        recensioneCorsoDAO.update(existing);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO eliminaRecensioneCorso(long utenteId, long corsoId) {
        RecensioneCorso existing = recensioneCorsoDAO.findByUtenteIdAndCorsoId(utenteId, corsoId);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        recensioneCorsoDAO.deleteByUtenteIdAndCorsoId(utenteId, corsoId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO creaRecensioneDieta(long utenteId, long dietaId, String testo) {
        ensurePrenotatoDieta(utenteId, dietaId);
        String clean = requireText(testo, "Testo");

        RecensioneDieta existing = recensioneDietaDAO.findByUtenteIdAndDietaId(utenteId, dietaId);
        if (existing != null) {
            throw new ResponseStatusException(CONFLICT, "Recensione gia' presente");
        }

        Utente u = new Utente();
        u.setId(utenteId);
        Dieta d = new Dieta();
        d.setId(dietaId);
        LocalDateTime now = LocalDateTime.now();
        recensioneDietaDAO.create(new RecensioneDieta(u, d, clean, now, now));
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO aggiornaRecensioneDieta(long utenteId, long dietaId, String testo) {
        ensurePrenotatoDieta(utenteId, dietaId);
        String clean = requireText(testo, "Testo");

        RecensioneDieta existing = recensioneDietaDAO.findByUtenteIdAndDietaId(utenteId, dietaId);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        existing.setTesto(clean);
        existing.setUpdatedAt(LocalDateTime.now());
        recensioneDietaDAO.update(existing);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO eliminaRecensioneDieta(long utenteId, long dietaId) {
        RecensioneDieta existing = recensioneDietaDAO.findByUtenteIdAndDietaId(utenteId, dietaId);
        if (existing == null) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        recensioneDietaDAO.deleteByUtenteIdAndDietaId(utenteId, dietaId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO rimuoviRecensioneCorsoAdmin(long utenteId, long recensioneId) {
        RecensioneCorso rec = recensioneCorsoDAO.findById(recensioneId);
        if (rec == null || rec.getUtente() == null || !Objects.equals(rec.getUtente().getId(), utenteId)) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        recensioneCorsoDAO.deleteById(recensioneId);
        return getDashboard(utenteId);
    }

    public ClienteDashboardDTO rimuoviRecensioneDietaAdmin(long utenteId, long recensioneId) {
        RecensioneDieta rec = recensioneDietaDAO.findById(recensioneId);
        if (rec == null || rec.getUtente() == null || !Objects.equals(rec.getUtente().getId(), utenteId)) {
            throw new ResponseStatusException(NOT_FOUND, "Recensione non trovata");
        }
        recensioneDietaDAO.deleteById(recensioneId);
        return getDashboard(utenteId);
    }

    private void ensureAbbonato(long utenteId) {
        AbbonamentoAttivo attivo = getActiveOrClearExpired(utenteId);
        if (attivo == null) {
            throw new ResponseStatusException(FORBIDDEN, "Abbonamento non attivo");
        }
    }

    private void ensureIscrittoCorso(long utenteId, long corsoId) {
        if (!corsoDAO.existsById(corsoId)) {
            throw new ResponseStatusException(NOT_FOUND, "Corso non trovato: " + corsoId);
        }
        if (!iscrizioneCorsoDAO.exists(utenteId, corsoId)) {
            throw new ResponseStatusException(FORBIDDEN, "Non iscritto al corso");
        }
    }

    private void ensurePrenotatoDieta(long utenteId, long dietaId) {
        if (!dietaDAO.existsById(dietaId)) {
            throw new ResponseStatusException(NOT_FOUND, "Dieta non trovata: " + dietaId);
        }
        if (!prenotazioneDietaDAO.exists(utenteId, dietaId)) {
            throw new ResponseStatusException(FORBIDDEN, "Dieta non prenotata");
        }
    }

    private AbbonamentoAttivo getActiveOrClearExpired(long utenteId) {
        AbbonamentoAttivo attivo = abbonamentoAttivoDAO.findByUtenteId(utenteId);
        if (attivo == null) return null;

        LocalDateTime endDate = Objects.requireNonNull(attivo.getEndDate(), "endDate");
        LocalDateTime now = LocalDateTime.now();
        if (!endDate.isAfter(now)) {
            clearAll(utenteId);
            return null;
        }
        return attivo;
    }

    private void clearAll(long utenteId) {
        abbonamentoAttivoDAO.deleteByUtenteId(utenteId);
        iscrizioneCorsoDAO.deleteByUtenteId(utenteId);
        prenotazioneDietaDAO.deleteByUtenteId(utenteId);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " obbligatorio.");
        }
        return value.trim();
    }

    private RecensioneCorsoDTO toRecensioneCorsoDto(RecensioneCorso r) {
        String corsoTitolo = r.getCorso() != null ? r.getCorso().getTitolo() : null;
        String utenteEmail = r.getUtente() != null ? r.getUtente().getEmail() : null;
        Long corsoId = r.getCorso() != null ? r.getCorso().getId() : null;
        Long utenteId = r.getUtente() != null ? r.getUtente().getId() : null;
        return new RecensioneCorsoDTO(
                r.getId(),
                corsoId,
                corsoTitolo,
                utenteId,
                utenteEmail,
                r.getTesto(),
                toIso(r.getCreatedAt()),
                toIso(r.getUpdatedAt())
        );
    }

    private RecensioneDietaDTO toRecensioneDietaDto(RecensioneDieta r) {
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
}
