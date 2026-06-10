package ru.yandex.practicum.rs.resource;

import ru.yandex.practicum.common.web.HttpConstants;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(HttpConstants.PAYMENTS_PATH)
public class ResourceController {

    @GetMapping
    public ResponseEntity<?> getPayments() {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createPayment() {
        return ResponseEntity.ok().build();
    }

}
