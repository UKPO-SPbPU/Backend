package ru.trkpo.datagen.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.trkpo.datagen.service.DataGenService;

@RestController
@RequestMapping("/generate")
@AllArgsConstructor
public class DataGenController {

    private final DataGenService dataGenService;

    @PostMapping("/cdr")
    public ResponseEntity<Boolean> generateCDRs() {
        try {
            dataGenService.generateCDRs();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
