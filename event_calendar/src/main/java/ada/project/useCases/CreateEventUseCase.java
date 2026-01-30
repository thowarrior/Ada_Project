package ada.project.useCases;

import ada.project.api.requestDTO.EventCreateDto;
import ada.project.clients.holiday.HolidaysService;
import ada.project.infrastructure.entity.Event;
import ada.project.infrastructure.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.Clock;
import java.time.LocalDate;

@ApplicationScoped
public class CreateEventUseCase {
    
    
    @Inject
    EventRepository eventRepository;
    
    @Inject
    HolidaysService holidaysService;
    
    private final Clock clock = Clock.systemDefaultZone();
    // para validação da data
    
    @Transactional
    public Event execute(EventCreateDto request) {
        
        if (request == null) {
            throw badRequest("Payload ausente.");
        }
        if (request.eventDescription == null || request.eventDescription.isBlank()) {
            throw badRequest("eventDescription é obrigatório.");
        }
        if (request.eventDate == null) {
            throw badRequest("eventDate é obrigatório (yyyy-MM-dd).");
        }
        if (request.eventType == null) {
            throw badRequest("eventType é obrigatório (ex.: WORKSHOP).");
        }
        
        validateNotPast(request.eventDate);
        validateNotHoliday(request.eventDate);
        
        Event entity = new Event();
        entity.setEventDescription(request.eventDescription);
        entity.setEventDate(request.eventDate);
        entity.setEventType(request.eventType);
        
        eventRepository.persist(entity);
        return entity;
    }
    
    
    private void validateNotPast(LocalDate eventDate) {
        LocalDate today = LocalDate.now(clock);
        if (eventDate.isBefore(today)) {
            throw badRequest("eventDate não pode estar no passado.");
        }
        // classe localDate já valida automaticamente valores impossiveis e retorna 400
    }
    
    
    private void validateNotHoliday(LocalDate eventDate) {
        try {
            if (holidaysService.isHoliday(eventDate)) {
                throw badRequest("eventDate não pode ser em um feriado nacional.");
            }
        } catch (WebApplicationException e) {
            throw new WebApplicationException(
                "Erro ao tentar realizar requisição: " + e.getMessage(),
                    e,
                    e.getResponse().getStatus()
            );
        } catch (Exception e) {
            // Qualquer falha ao consultar a API de feriados → 503 (fail-closed)
            throw e;
        }
    }
    
    
    private WebApplicationException badRequest(String msg) {
        return new WebApplicationException(msg, Response.Status.BAD_REQUEST);
    }
}


