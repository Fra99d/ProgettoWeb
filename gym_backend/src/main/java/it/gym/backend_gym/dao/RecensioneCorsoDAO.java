package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.RecensioneCorso;

import java.util.List;

public interface RecensioneCorsoDAO {
    List<RecensioneCorso> findByCorsoId(long corsoId);
    List<RecensioneCorso> findByUtenteId(long utenteId);
    RecensioneCorso findByUtenteIdAndCorsoId(long utenteId, long corsoId);
    RecensioneCorso findById(long id);
    RecensioneCorso create(RecensioneCorso recensione);
    RecensioneCorso update(RecensioneCorso recensione);
    void deleteById(long id);
    void deleteByUtenteIdAndCorsoId(long utenteId, long corsoId);
}
