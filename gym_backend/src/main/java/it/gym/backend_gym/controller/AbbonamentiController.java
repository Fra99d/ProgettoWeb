package it.gym.backend_gym.controller;

import it.gym.backend_gym.dto.AbbonamentoDTO;
import it.gym.backend_gym.service.AbbonamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/abbonamenti")
public class AbbonamentiController {

    private final AbbonamentoService service;

    public AbbonamentiController(AbbonamentoService service) {
        this.service = service;
    }

    public record AbbonamentoRequest(Integer durata, BigDecimal prezzo) {}

    @GetMapping
    public List<AbbonamentoDTO> lista() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbbonamentoDTO crea(@RequestBody AbbonamentoRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.create(req.durata(), req.prezzo());
    }

    @PutMapping("/{id}")
    public AbbonamentoDTO modifica(@PathVariable long id, @RequestBody AbbonamentoRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.update(id, req.durata(), req.prezzo());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(@PathVariable long id) {
        service.delete(id);
    }
}
