package it.gym.backend_gym.controller;

import it.gym.backend_gym.dto.ClienteDashboardDTO;
import it.gym.backend_gym.security.SecurityUser;
import it.gym.backend_gym.service.ClienteService;
import it.gym.backend_gym.service.UtenteService;
import it.gym.backend_gym.entity.Utente;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final UtenteService utenteService;
    public ClienteController(ClienteService clienteService, UtenteService utenteService) {
        this.clienteService = clienteService;
        this.utenteService = utenteService;
    }

    public record AbbonamentoRequest(Long abbonamentoId) {}
    public record AccountUpdateRequest(String email, String password) {}
    public record AccountResponse(Long id, String email, String ruolo) {}
    public record RecensioneRequest(String testo) {}

    @GetMapping("/dashboard")
    public ClienteDashboardDTO dashboard(Authentication authentication) {
        long userId = getUserId(authentication);
        return clienteService.getDashboard(userId);
    }

    @PostMapping("/abbonamento")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDashboardDTO attivaAbbonamento(@RequestBody AbbonamentoRequest req, Authentication authentication) {
        long userId = getUserId(authentication);
        if (req == null || req.abbonamentoId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Abbonamento non valido");
        }
        return clienteService.attivaAbbonamento(userId, req.abbonamentoId());
    }

    @DeleteMapping("/abbonamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviAbbonamento(Authentication authentication) {
        long userId = getUserId(authentication);
        clienteService.rimuoviAbbonamento(userId);
    }

    @PostMapping("/corsi/{corsoId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDashboardDTO iscriviCorso(@PathVariable long corsoId, Authentication authentication) {
        long userId = getUserId(authentication);
        return clienteService.iscriviCorso(userId, corsoId);
    }

    @DeleteMapping("/corsi/{corsoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rimuoviCorso(@PathVariable long corsoId, Authentication authentication) {
        long userId = getUserId(authentication);
        clienteService.rimuoviCorso(userId, corsoId);
    }

    @PostMapping("/prenotazioni/{dietaId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDashboardDTO prenota(@PathVariable long dietaId, Authentication authentication) {
        long userId = getUserId(authentication);
        return clienteService.prenotaDieta(userId, dietaId);
    }

    @DeleteMapping("/prenotazioni/{prenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annulla(@PathVariable long prenId, Authentication authentication) {
        long userId = getUserId(authentication);
        clienteService.annullaPrenotazione(userId, prenId);
    }

    @PostMapping("/corsi/{corsoId}/recensione")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDashboardDTO creaRecensioneCorso(
            @PathVariable long corsoId,
            @RequestBody RecensioneRequest req,
            Authentication authentication
    ) {
        long userId = getUserId(authentication);
        String testo = req != null ? req.testo() : null;
        return clienteService.creaRecensioneCorso(userId, corsoId, testo);
    }

    @PutMapping("/corsi/{corsoId}/recensione")
    public ClienteDashboardDTO aggiornaRecensioneCorso(
            @PathVariable long corsoId,
            @RequestBody RecensioneRequest req,
            Authentication authentication
    ) {
        long userId = getUserId(authentication);
        String testo = req != null ? req.testo() : null;
        return clienteService.aggiornaRecensioneCorso(userId, corsoId, testo);
    }

    @DeleteMapping("/corsi/{corsoId}/recensione")
    public ClienteDashboardDTO eliminaRecensioneCorso(@PathVariable long corsoId, Authentication authentication) {
        long userId = getUserId(authentication);
        return clienteService.eliminaRecensioneCorso(userId, corsoId);
    }

    @PostMapping("/diete/{dietaId}/recensione")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDashboardDTO creaRecensioneDieta(
            @PathVariable long dietaId,
            @RequestBody RecensioneRequest req,
            Authentication authentication
    ) {
        long userId = getUserId(authentication);
        String testo = req != null ? req.testo() : null;
        return clienteService.creaRecensioneDieta(userId, dietaId, testo);
    }

    @PutMapping("/diete/{dietaId}/recensione")
    public ClienteDashboardDTO aggiornaRecensioneDieta(
            @PathVariable long dietaId,
            @RequestBody RecensioneRequest req,
            Authentication authentication
    ) {
        long userId = getUserId(authentication);
        String testo = req != null ? req.testo() : null;
        return clienteService.aggiornaRecensioneDieta(userId, dietaId, testo);
    }

    @DeleteMapping("/diete/{dietaId}/recensione")
    public ClienteDashboardDTO eliminaRecensioneDieta(@PathVariable long dietaId, Authentication authentication) {
        long userId = getUserId(authentication);
        return clienteService.eliminaRecensioneDieta(userId, dietaId);
    }

    @PutMapping("/account")
    public AccountResponse updateAccount(@RequestBody AccountUpdateRequest req, Authentication authentication) {
        long userId = getUserId(authentication);
        String email = req != null ? req.email() : null;
        String password = req != null ? req.password() : null;
        Utente updated = utenteService.updateSelf(userId, email, password);
        return new AccountResponse(updated.getId(), updated.getEmail(), updated.getRuolo().name());
    }

    private long getUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Non autenticato");
        }
        return user.getId();
    }
}
