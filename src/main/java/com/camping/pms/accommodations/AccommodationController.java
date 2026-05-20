package com.camping.pms.accommodations;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public Accommodation create(@RequestBody Accommodation accommodation) {
        return repository.save(accommodation);
    }
}
