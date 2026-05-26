package com.camping.pms.accommodations;

import com.camping.pms.accommodations.dto.AccommodationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationRepository repository;

    public AccommodationController(AccommodationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<AccommodationDto> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable).map(AccommodationDto::from);
    }

    @GetMapping("/{id}")
    public AccommodationDto findById(@PathVariable UUID id) {
        return AccommodationDto.from(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccommodationDto create(@RequestBody Accommodation accommodation) {
        return AccommodationDto.from(repository.save(accommodation));
    }

    @PutMapping("/{id}")
    public AccommodationDto update(@PathVariable UUID id, @RequestBody Accommodation updated) {
        Accommodation existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hébergement non trouvé"));
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setCapacity(updated.getCapacity());
        existing.setBasePrice(updated.getBasePrice());
        existing.setDescription(updated.getDescription());
        return AccommodationDto.from(repository.save(existing));
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