package com.operator.subscriber.controller;

import com.operator.subscriber.payload.request.AmountRequest;
import com.operator.subscriber.payload.request.SubscriberRequest;
import com.operator.subscriber.payload.response.SubscriberResponse;
import com.operator.subscriber.service.PhotoStorageService;
import com.operator.subscriber.service.SubscriberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/subscribers")
@Validated
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final PhotoStorageService photoStorageService;

    public SubscriberController(SubscriberService subscriberService, PhotoStorageService photoStorageService) {
        this.subscriberService = subscriberService;
        this.photoStorageService = photoStorageService;
    }

    @PostMapping
    public ResponseEntity<SubscriberResponse> create(@Valid @RequestBody SubscriberRequest request) {
        return ResponseEntity.ok(subscriberService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriberResponse> getById(@PathVariable long id) {
        return ResponseEntity.ok(subscriberService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> listOrFind(@RequestParam(required = false) String msisdn) {
        if (msisdn != null && !msisdn.isBlank()) {
            return ResponseEntity.ok(subscriberService.getByMsisdn(msisdn));
        }
        List<SubscriberResponse> list = subscriberService.listAll();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriberResponse> update(@PathVariable long id, @Valid @RequestBody SubscriberRequest request) {
        return ResponseEntity.ok(subscriberService.update(id, request));
    }

    @PatchMapping("/{id}/tariff/{tariffId}")
    public ResponseEntity<SubscriberResponse> changeTariff(@PathVariable long id, @PathVariable long tariffId) {
        return ResponseEntity.ok(subscriberService.changeTariff(id, tariffId));
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<SubscriberResponse> topUp(@PathVariable long id, @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(subscriberService.topUp(id, request.getAmount(), request.getDescription()));
    }

    @PostMapping("/{id}/charge")
    public ResponseEntity<SubscriberResponse> charge(@PathVariable long id, @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(subscriberService.charge(id, request.getAmount(), request.getDescription()));
    }

    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubscriberResponse> uploadPhoto(
            @PathVariable long id,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(subscriberService.uploadPhoto(id, file));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getPhoto(@PathVariable long id) {
        Resource resource = subscriberService.getPhoto(id);
        String contentType = photoStorageService.detectContentType(resource);
        MediaType mediaType = (contentType == null) ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    @GetMapping("/validate/msisdn")
    public ResponseEntity<Void> validateMsisdn(@RequestParam @NotBlank String msisdn) {
        subscriberService.getByMsisdn(msisdn);
        return ResponseEntity.ok().build();
    }
}

