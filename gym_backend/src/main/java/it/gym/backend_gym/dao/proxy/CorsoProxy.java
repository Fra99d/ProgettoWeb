package it.gym.backend_gym.dao.proxy;

import it.gym.backend_gym.dao.RecensioneCorsoDAO;
import it.gym.backend_gym.entity.Corso;
import it.gym.backend_gym.entity.RecensioneCorso;

import java.util.List;

public class CorsoProxy extends Corso {

    private final RecensioneCorsoDAO recensioneCorsoDAO;
    private boolean recensioniLoaded = false;

    public CorsoProxy(RecensioneCorsoDAO recensioneCorsoDAO) {
        this.recensioneCorsoDAO = recensioneCorsoDAO;
    }

    @Override
    public List<RecensioneCorso> getRecensioni() {
        if (!recensioniLoaded) {
            recensioniLoaded = true;
            if (getId() == null) {
                setRecensioni(List.of());
            } else {
                setRecensioni(recensioneCorsoDAO.findByCorsoId(getId()));
            }
        }
        return super.getRecensioni();
    }

    @Override
    public void setRecensioni(List<RecensioneCorso> recensioni) {
        super.setRecensioni(recensioni);
        recensioniLoaded = true;
    }
}
