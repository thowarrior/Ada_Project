package ada.project.useCases;

import ada.project.api.responseDTO.EventResponseDto;
import ada.project.api.responseDTO.EventUpdateDto;
import ada.project.infrastructure.entity.Event;
import ada.project.infrastructure.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
@ApplicationScoped
public class UpdateEventUseCase {
    @Inject
    EventRepository eventRepo;
    
    @Transactional
    public EventResponseDto updateEvent(Long id, EventUpdateDto eventUpdateDto) {
        try {
            Event event = eventRepo.findById(id);
            if (event == null) {
                throw new IllegalArgumentException("Event not found with id: " + id);
            }
            
            // Atualizar apenas os campos que foram fornecidos
            if (eventUpdateDto.eventDescription() != null) {
                event.setEventDescription(eventUpdateDto.eventDescription());
            }
            if (eventUpdateDto.eventDate() != null) {
                event.setEventDate(eventUpdateDto.eventDate());
            }
            if (eventUpdateDto.eventType() != null) {
                event.setEventType(eventUpdateDto.eventType());
            }
            
            eventRepo.persist(event);
            return EventResponseDto.fromEntity(event);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
}
