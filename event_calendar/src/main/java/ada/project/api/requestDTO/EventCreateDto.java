package ada.project.api.requestDTO;


import ada.project.infrastructure.entity.Event;

import java.time.LocalDate;

public class EventCreateDto {
    public String eventDescription;
    public LocalDate eventDate;
    public Event.EventType eventType;
}
