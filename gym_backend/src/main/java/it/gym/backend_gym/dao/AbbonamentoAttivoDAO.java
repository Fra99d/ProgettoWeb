package it.gym.backend_gym.dao;

import it.gym.backend_gym.entity.AbbonamentoAttivo;

public interface AbbonamentoAttivoDAO {
    AbbonamentoAttivo findByUtenteId(long utenteId);
    AbbonamentoAttivo upsert(AbbonamentoAttivo a);
    void deleteByUtenteId(long utenteId);
}
