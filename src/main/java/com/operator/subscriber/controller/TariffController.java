package com.operator.subscriber.controller;

import com.operator.subscriber.payload.request.TariffRequest;
import com.operator.subscriber.payload.response.TariffResponse;
import com.operator.subscriber.service.TariffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {
    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public ResponseEntity<List<TariffResponse>> listActive() {
        return ResponseEntity.ok(tariffService.listActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffResponse> getById(@PathVariable long id) {
        return ResponseEntity.ok(tariffService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TariffResponse> create(@Valid @RequestBody TariffRequest request) {
        return ResponseEntity.ok(tariffService.create(request));
    }
}

