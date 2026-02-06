package it.gym.backend_gym.service;

import it.gym.backend_gym.dao.AbbonamentoDAO;
import it.gym.backend_gym.dto.AbbonamentoDTO;
import it.gym.backend_gym.entity.Abbonamento;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class AbbonamentoService {

    private final AbbonamentoDAO dao;

    public AbbonamentoService(AbbonamentoDAO dao) {
        this.dao = dao;
    }

    public List<AbbonamentoDTO> findAll() {
        return dao.findAll().stream().map(this::toDto).toList();
    }

    public AbbonamentoDTO create(Integer durata, BigDecimal prezzo) {
        validate(durata, prezzo);

        Abbonamento a = new Abbonamento(durata, prezzo);
        Abbonamento created = dao.create(a);   
        return toDto(created);
    }

    public AbbonamentoDTO update(long id, Integer durata, BigDecimal prezzo) {
        validate(durata, prezzo);

        Abbonamento a = dao.findById(id);      
        if (a == null) {
            throw new ResponseStatusException(NOT_FOUND, "Abbonamento non trovato: " + id);
        }

        a.setDurata(durata);
        a.setPrezzo(prezzo);

        Abbonamento updated = dao.update(a);   
        return toDto(updated);
    }

    public void delete(long id) {
        if (!dao.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Abbonamento non trovato: " + id);
        }
        if (dao.existsInAbbonamentiAttivi(id)) {
            throw new ResponseStatusException(CONFLICT, "Abbonamento attivo da un cliente");
        }
        dao.delete(id);
    }

    private void validate(Integer durata, BigDecimal prezzo) {
        if (durata == null || durata <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Durata non valida");
        }
        if (prezzo == null || prezzo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Prezzo non valido");
        }
    }

    private AbbonamentoDTO toDto(Abbonamento a) {
        return new AbbonamentoDTO(a.getId(), a.getDurata(), a.getPrezzo());
    }
}







