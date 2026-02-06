package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.PrenotazioneDieta;

import java.util.List;

public interface PrenotazioneDietaDAO {
    List<PrenotazioneDieta> findByUtenteId(long utenteId);
    boolean exists(long utenteId, long dietaId);
    PrenotazioneDieta create(PrenotazioneDieta prenotazione);
    void deleteById(long prenotazioneId);
    void deleteByIdAndUtenteId(long prenotazioneId, long utenteId);
    void deleteByUtenteId(long utenteId);
}
