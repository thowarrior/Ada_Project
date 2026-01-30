
package ada.project.useCases;

import ada.project.infrastructure.repository.EventRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class DeleteEventByIdUseCase {
    
    @Inject
    EventRepository eventRepository;
    
    @Transactional
    public void execute(Long id) {
        if (id == null || id <= 0) {
            throw badRequest("id inválido. Use um valor numérico positivo.");
        }
        
        boolean deleted = eventRepository.deleteById(id);
        if (!deleted) {
            throw notFound("Evento não encontrado para o id=" + id);
        }
    }
    
    private WebApplicationException badRequest(String msg) {
        return new WebApplicationException(msg, Response.Status.BAD_REQUEST);
    }
    
    private WebApplicationException notFound(String msg) {
        return new WebApplicationException(msg, Response.Status.NOT_FOUND);
    }
}
