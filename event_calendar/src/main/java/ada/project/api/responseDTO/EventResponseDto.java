package ada.project.api.responseDTO;

import ada.project.infrastructure.entity.Event;

import java.time.LocalDate;

public record EventResponseDto(
        Long id,
        String eventDescription,
        LocalDate eventDate,
        Event.EventType eventType) {
    // Converter da entidade para DTO
    public static EventResponseDto fromEntity(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getEventType());
    }
}
