package ada.project.api.responseDTO;

import ada.project.infrastructure.entity.Event;

import java.time.LocalDate;

public record EventUpdateDto(
        String eventDescription,
        LocalDate eventDate,
        Event.EventType eventType) {
}
