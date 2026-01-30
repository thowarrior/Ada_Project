package ada.project.clients.holiday;

import ada.project.api.responseDTO.HolidayDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class HolidaysService {
    
    // HttpClient do JDK (sem libs externas)
    private final HttpClient http = HttpClient.newBuilder()
                                            .connectTimeout(Duration.ofSeconds(3))
                                            .build();
    
    @Inject
    ObjectMapper objectMapper; // já disponível por causa do quarkus-rest-jackson
    
    /**
     * Base URL configurável no application.properties, por exemplo:
     * feriados.api.base-url=http://localhost:9090/feriados/v1
     */
    @ConfigProperty(name = "feriados.api.base-url")
    String feriadosBaseUrl;
    
    // Cache simples por ano (em memória)
    private final Map<Integer, Set<LocalDate>> cacheByYear = new ConcurrentHashMap<>();
    
    
    public boolean isHoliday(LocalDate date) {
        int year = date.getYear();
        Set<LocalDate> holidays = cacheByYear.computeIfAbsent(year, this::fetchHolidaysForYear);
        return holidays.contains(date);
    }

    public List<HolidayDTO> holidayDTOListByYear(int year) {

        try {
            HttpResponse<String> response = getResponseHolidayByYear(year);
            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<HolidayDTO>>() {}
            );

        } catch (IllegalStateException | IOException e) {
            throw new IllegalStateException("Falha ao tentar listar os feriados! " + e.getMessage());
        }

    }

    private HttpResponse<String> getResponseHolidayByYear(int year) {
        String url = String.format("%s/%d", feriadosBaseUrl, year);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta de feriados interrompida.", ie);
        } catch (IOException e) {
            throw new IllegalStateException("Erro de I/O ao consultar feriados: " + e.getMessage(), e);
        }
    }


    
    // Limpa cache
    public void evictYear(int year) {
        cacheByYear.remove(year);
    }
    
    private Set<LocalDate> fetchHolidaysForYear(int year) {
        String url = String.format("%s/%d", feriadosBaseUrl, year);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            int sc = response.statusCode();
            if (sc >= 200 && sc < 300) {
                List<HolidayDTO> list = objectMapper.readValue(
                        response.body(),
                        new TypeReference<List<HolidayDTO>>() {}
                );
                
                // Regra: considerar apenas feriados "nacionais"
                return list == null ? Set.of() :
                               list.stream()
                                       .filter(h -> h != null && h.date != null)
                                       .filter(h -> "national".equalsIgnoreCase(h.type))
                                       .map(h -> h.date)
                                       .collect(Collectors.toCollection(LinkedHashSet::new));
            } else {
                throw new IllegalStateException("Falha ao consultar feriados: HTTP " + sc);
            }
            
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta de feriados interrompida.", ie);
            
        } catch (IOException e) {
            throw new IllegalStateException("Erro de I/O ao consultar feriados: " + e.getMessage(), e);
        }
    }
}
