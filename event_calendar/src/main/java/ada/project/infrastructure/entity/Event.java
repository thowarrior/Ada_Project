package ada.project.infrastructure.entity;

import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "events")
public class Event{
//! Tem que ser tudo público, ou tudo privado
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    @NotNull
    private Long id;
    
    

    @Column(name = "event_description", nullable = true, length = 500)
    private String eventDescription;
    
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;
    

    public Event() {}
    
    public Event(String eventDescription, LocalDate eventDate, EventType eventType) {
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventType = eventType;
    }
    
    // --- Getters e Setters ---
    public Long getId() { return id; }
    
    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    
  
    public enum EventType {
        WORKSHOP,
        CONFERENCIA,
        MEETUP,
        HACKATHON,
        WEBINAR,
        OUTRO
    }
}
