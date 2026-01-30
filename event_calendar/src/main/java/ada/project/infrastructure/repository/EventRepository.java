
package ada.project.infrastructure.repository;

import ada.project.infrastructure.entity.Event;
import ada.project.infrastructure.entity.PageResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class EventRepository implements PanacheRepositoryBase<Event, Long> {
    

    public PageResult<Event> getAllEvents(int page, int pageSize) {
        
        PanacheQuery<Event> query = findAll().page(Page.of(page, pageSize));
        List<Event> items = query.list();
        long totalItems = query.count();
        int pageCount = query.pageCount();
        
        return new PageResult<Event>(items,totalItems,pageCount,page,pageSize);
    }
    
    
    public PageResult<Event> getEventsByDate(LocalDate date, int page, int pageSize) {
        LocalDate start = date;
        LocalDate end = date.plusDays(1);
        PanacheQuery<Event> q = find(
                "eventDate >= ?1 AND eventDate < ?2 ORDER BY eventDate ASC",
                start, end
        );
        return toPageResult(q, page, pageSize);
    }
    
    public PageResult<Event> getEventsByMonth(int year, int month, int page, int pageSize) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1);
        PanacheQuery<Event> q = find(
                "eventDate >= ?1 AND eventDate < ?2 ORDER BY eventDate ASC",
                start, end
        );
        return toPageResult(q, page, pageSize);
    }
    
    public PageResult<Event> getEventsByYear(int year, int page, int pageSize) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = start.plusYears(1);
        PanacheQuery<Event> q = find(
                "eventDate >= ?1 AND eventDate < ?2 ORDER BY eventDate ASC",
                start, end
        );
        return toPageResult(q, page, pageSize);
    }
    
    
    private PageResult<Event> toPageResult(PanacheQuery<Event> query, int page, int pageSize) {
        long total = query.count();
        List<Event> items = query.page(Page.of(page, pageSize))
                                    .list();
        int pageCount = query.pageCount();
        return new PageResult<>(items, total, pageCount,page, pageSize);
    }
    
    
    // Pega o evento e persiste no banco
    public void persist(Event event) {
        getEntityManager().persist(event);
    }
}
