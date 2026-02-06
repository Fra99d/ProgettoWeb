package it.gym.backend_gym.controller;

import it.gym.backend_gym.dto.CorsoDTO;
import it.gym.backend_gym.service.CorsoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/corsi")
public class CorsiController {

    private final CorsoService service;

    public CorsiController(CorsoService service) {
        this.service = service;
    }

    public record CorsoRequest(String titolo, String descrizione, String lezione, String fotoUrl) {}

    @GetMapping
    public List<CorsoDTO> lista() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CorsoDTO crea(@RequestBody CorsoRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.create(req.titolo(), req.descrizione(), req.lezione(), req.fotoUrl());
    }

    @PutMapping("/{id}")
    public CorsoDTO modifica(@PathVariable long id, @RequestBody CorsoRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.update(id, req.titolo(), req.descrizione(), req.lezione(), req.fotoUrl());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(@PathVariable long id) {
        service.delete(id);
    }
}


