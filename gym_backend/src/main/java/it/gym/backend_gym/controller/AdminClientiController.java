package it.gym.backend_gym.controller;

import it.gym.backend_gym.dto.ClienteDashboardDTO;
import it.gym.backend_gym.dto.UtenteDTO;
import it.gym.backend_gym.service.ClienteService;
import it.gym.backend_gym.service.UtenteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clienti")
public class AdminClientiController {

    private final UtenteService utenteService;
    private final ClienteService clienteService;
    public AdminClientiController(UtenteService utenteService, ClienteService clienteService) {
        this.utenteService = utenteService;
        this.clienteService = clienteService;
    }

    public record UpdateUtenteRequest(String email, String password, String ruolo) {}

    @GetMapping
    public List<UtenteDTO> list() {
        return utenteService.findAll();
    }

    @PutMapping("/{id}")
    public UtenteDTO update(@PathVariable long id, @RequestBody UpdateUtenteRequest req) {
        String email = req != null ? req.email() : null;
        String password = req != null ? req.password() : null;
        String ruolo = req != null ? req.ruolo() : null;
        return utenteService.update(id, email, password, ruolo);
    }

    @GetMapping("/{id}/dashboard")
    public ClienteDashboardDTO dashboard(@PathVariable long id) {
        return clienteService.getDashboard(id);
    }

    @DeleteMapping("/{id}/abbonamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviAbbonamento(@PathVariable long id) {
        clienteService.rimuoviAbbonamento(id);
    }

    @DeleteMapping("/{id}/corsi/{corsoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviCorso(@PathVariable long id, @PathVariable long corsoId) {
        clienteService.rimuoviCorso(id, corsoId);
    }

    @DeleteMapping("/{id}/prenotazioni/{prenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviPrenotazione(@PathVariable long id, @PathVariable long prenId) {
        clienteService.annullaPrenotazione(id, prenId);
    }

    @DeleteMapping("/{id}/recensioni/corsi/{recensioneId}")
    public ClienteDashboardDTO rimuoviRecensioneCorso(@PathVariable long id, @PathVariable long recensioneId) {
        return clienteService.rimuoviRecensioneCorsoAdmin(id, recensioneId);
    }

    @DeleteMapping("/{id}/recensioni/diete/{recensioneId}")
    public ClienteDashboardDTO rimuoviRecensioneDieta(@PathVariable long id, @PathVariable long recensioneId) {
        return clienteService.rimuoviRecensioneDietaAdmin(id, recensioneId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        utenteService.delete(id);
    }
}
