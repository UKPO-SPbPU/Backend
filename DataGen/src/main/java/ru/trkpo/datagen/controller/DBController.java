package ru.trkpo.datagen.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.trkpo.datagen.service.DBService;

@RestController
@RequestMapping("/database")
@AllArgsConstructor
public class DBController {

    private final DBService dbService;

    @PostMapping("/reset")
    public ResponseEntity<Boolean> reset() {
        try {
            dbService.reset();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/populate")
    public ResponseEntity<Boolean> populate() {
        try {
            dbService.populate();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/truncate")
    public ResponseEntity<Boolean> truncate() {
        try {
            dbService.truncate();
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
