package com.pet.pethubapi.interfaces.tvshow;

import com.pet.pethubapi.application.tvshow.TvShowDetailsService;
import com.pet.pethubapi.domain.tvshow.TvShowDTO;
import com.pet.pethubapi.domain.tvshow.TvShowSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tv-show")
public class TvShowDetailsController {

    private final TvShowDetailsService showDetailsService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TvShowDTO>> getAllTvShows(@PageableDefault final Pageable pageable) {
        final var result = showDetailsService.getAllShows(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/details", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TvShowSearchResponse>> getTvShowDetailsByName(@RequestParam("showName") final String showName) {
        final var result = showDetailsService.getShowDetailsByName(showName);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(result);
    }

}
