package it.gym.backend_gym.controller;

import it.gym.backend_gym.dto.DietaDTO;
import it.gym.backend_gym.service.DietaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/diete")
public class DieteController {

    private final DietaService service;

    public DieteController(DietaService service) {
        this.service = service;
    }

    public record DietaRequest(String nome, String descrizione, String appuntamento, String fotoUrl) {}

    @GetMapping
    public List<DietaDTO> lista() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DietaDTO crea(@RequestBody DietaRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.create(req.nome(), req.descrizione(), req.appuntamento(),req.fotoUrl());
    }

    @PutMapping("/{id}")
    public DietaDTO modifica(@PathVariable long id, @RequestBody DietaRequest req) {
        if (req == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Richiesta non valida");
        }
        return service.update(id, req.nome(), req.descrizione(), req.appuntamento(), req.fotoUrl());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void elimina(@PathVariable long id) {
        service.delete(id);
    }
}


