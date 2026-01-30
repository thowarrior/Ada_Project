package ada.project.useCases;

import ada.project.api.responseDTO.EventResponseDto;
import ada.project.infrastructure.entity.Event;
import ada.project.infrastructure.entity.PageResult;
import ada.project.infrastructure.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

@ApplicationScoped
public class GetEventsUseCase {
    @Inject
    EventRepository eventRepo;
    
    public PageResult<Event> getAllEvents(int page, int pageSize){
        try{
            PageResult<Event> eventsList = eventRepo.getAllEvents(page, pageSize);
            return eventsList;
        }catch(Exception e){
            System.out.println(e);
            throw e;
        }
    }
    
    
    public EventResponseDto getEventById(Long id) {
        try {
            Event event = eventRepo.findById(id);
            if (event == null) {
                throw new IllegalArgumentException("Event not found with id: " + id);
            }
            return EventResponseDto.fromEntity(event);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
    
    
    public PageResult<Event> getEventsByDate(LocalDate date, int page, int pageSize) {
        try {
            return eventRepo.getEventsByDate(date, page, pageSize);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
    
    public PageResult<Event> getEventsByMonth(int year, int month, int page, int pageSize) {
        try {
            return eventRepo.getEventsByMonth(year, month, page, pageSize);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
    
    public PageResult<Event> getEventsByYear(int year, int page, int pageSize) {
        try {
            return eventRepo.getEventsByYear(year, page, pageSize);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
}

    
    
    
