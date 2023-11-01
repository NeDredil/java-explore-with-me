package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.validation.Valid;
import java.util.List;

@Service
public class StatsClient {

    @Value("${STATS_SERVER_URL}")
    private String serverUrl;

    private final WebClient webClient = WebClient.create(serverUrl);

    public ResponseEntity<EndpointHitDto> createHit(@Valid EndpointHitDto endpointHitDto) {
        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(endpointHitDto)
                .retrieve()
                .toEntity(EndpointHitDto.class)
                .block();
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(StatsDto statsDto) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", statsDto.getStart())
                        .queryParam("end", statsDto.getEnd())
                        .queryParam("unique", statsDto.getUnique())
                        .queryParam("uris", statsDto.getUris())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntityList(ViewStatsDto.class)
                .block();
    }

}
