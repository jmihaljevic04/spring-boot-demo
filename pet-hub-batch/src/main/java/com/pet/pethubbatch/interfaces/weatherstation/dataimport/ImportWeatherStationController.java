package com.pet.pethubbatch.interfaces.weatherstation.dataimport;

import com.pet.pethubbatch.application.weatherstation.dataimport.ImportWeatherStationFacade;
import com.pet.pethubbatch.application.weatherstation.dataimport.ImportWeatherStationService;
import com.pet.pethubbatch.domain.weatherstation.dataimport.WeatherStationImportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather-station")
class ImportWeatherStationController {

    private final ImportWeatherStationService importWeatherStationService;
    private final ImportWeatherStationFacade importWeatherStationFacade;

    @GetMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherStationImportTriggerResponse> importWeatherStations() {
        final var importId = importWeatherStationService.triggerImport();

        final String status;
        if (ImportWeatherStationService.NOOP_IMPORT_ID.equals(importId)) {
            status = "NOOP";
        } else {
            status = "Import started successfully!";
            importWeatherStationFacade.importWeatherStations(importId);
        }

        final var responseBody = new WeatherStationImportTriggerResponse(importId, status);
        responseBody.add(linkTo(methodOn(ImportWeatherStationController.class).getImportStatus(importId)).withSelfRel());

        return ResponseEntity.accepted().body(responseBody);
    }

    @GetMapping(value = "/import/{importId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WeatherStationImportDTO> getImportStatus(@PathVariable Long importId) {
        return ResponseEntity.ok(importWeatherStationService.getImportStatus(importId));
    }

}
