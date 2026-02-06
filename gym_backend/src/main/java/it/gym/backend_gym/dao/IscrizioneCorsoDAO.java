package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.IscrizioneCorso;

import java.util.List;

public interface IscrizioneCorsoDAO {
    List<IscrizioneCorso> findByUtenteId(long utenteId);
    boolean exists(long utenteId, long corsoId);
    IscrizioneCorso create(IscrizioneCorso iscrizione);
    void delete(long utenteId, long corsoId);
    void deleteByUtenteId(long utenteId);
}
