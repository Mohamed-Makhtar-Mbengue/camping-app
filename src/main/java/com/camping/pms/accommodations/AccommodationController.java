package com.camping.pms.accommodations;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationRepository repository;

    public AccommodationController(AccommodationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Accommodation> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Accommodation findById(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Accommodation create(@RequestBody Accommodation accommodation) {
        return repository.save(accommodation);
    }

    @PutMapping("/{id}")
    public Accommodation update(@PathVariable UUID id, @RequestBody Accommodation updated) {
        Accommodation existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé"));
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setCapacity(updated.getCapacity());
        existing.setBasePrice(updated.getBasePrice());
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé");
        }
        repository.deleteById(id);
    }
}