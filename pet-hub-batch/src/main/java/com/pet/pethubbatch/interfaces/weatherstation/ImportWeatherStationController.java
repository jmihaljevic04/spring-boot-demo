package com.pet.pethubbatch.interfaces.weatherstation;

import com.pet.pethubbatch.application.weatherstation.ImportWeatherStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather-station")
class ImportWeatherStationController {

    private final ImportWeatherStationService importWeatherStationService;

    @GetMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> importWeatherStations() {
        final var importId = importWeatherStationService.triggerImport();

        final Map<String, Object> responseBody = new HashMap<>(Map.of("importId", importId));
        final String status;
        if (ImportWeatherStationService.NOOP_IMPORT_ID.equals(importId)) {
            status = "NOOP";
        } else {
            status = "Import started successfully!";
        }
        responseBody.put("status", status);

        return ResponseEntity.accepted().body(responseBody);
    }

}
