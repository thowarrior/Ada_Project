package ada.project.api;

import ada.project.api.requestDTO.EventCreateDto;
import ada.project.api.responseDTO.EventResponseDto;
import ada.project.api.responseDTO.EventUpdateDto;
import ada.project.infrastructure.entity.Event;
import ada.project.infrastructure.entity.PageResult;
import ada.project.useCases.CreateEventUseCase;
import ada.project.useCases.DeleteEventByIdUseCase;
import ada.project.useCases.GetEventsUseCase;
import ada.project.useCases.UpdateEventUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;

@Path("/eventos")
//* o objetivo dessa rota, é retornar feriados existente num dado dia, mês e/ou ano
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class EventController {
    private final GetEventsUseCase eventsUseCase;
    private final CreateEventUseCase createEventUseCase;
    private final DeleteEventByIdUseCase deleteEventByIdUseCase;
    private final UpdateEventUseCase updateEventUseCase;

    @Inject
    public EventController(GetEventsUseCase eventsUseCase, CreateEventUseCase createEventUseCase, DeleteEventByIdUseCase deleteEventByIdUseCase, UpdateEventUseCase updateEventUseCase) {
        this.eventsUseCase = eventsUseCase;
        this.createEventUseCase = createEventUseCase;
        this.deleteEventByIdUseCase = deleteEventByIdUseCase;
        this.updateEventUseCase = updateEventUseCase;
    }

    @GET
    public Response listAllEvents(
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("date") String dateIso,
            @QueryParam("year") Integer year,
            @QueryParam("month") Integer month,
            @QueryParam("day") Integer day
    ) {
        try {
            PageResult<Event> response;
            System.out.println(month);

            if (page == null) {
                page = 0;
            }

            if (pageSize == null) {
                pageSize = 5;
            }

            if (dateIso != null && !dateIso.isBlank()) {
                LocalDate date = parseIsoDateOrThrow(dateIso);
                response = eventsUseCase.getEventsByDate(date, page, pageSize);
                return Response.ok(response).build();
            }

            if (year != null && month != null) {
                validateMonth(month);
                if (day != null) {
                    validateDay(day);
                    LocalDate date = buildDateOrThrow(year, month, day);
                    response = eventsUseCase.getEventsByDate(date, page, pageSize);
                    return Response.ok(response).build();
                } else {
                    response = eventsUseCase.getEventsByMonth(year, month, page, pageSize);
                    return Response.ok(response).build();
                }
            }

            if (year != null) {
                System.out.println("year !");

                response = eventsUseCase.getEventsByYear(year, page, pageSize);
                return Response.ok(response).build();
            }

            if (month != null) {
                System.out.println("month !");
                int currentYear = Year.now().getValue();
                response = eventsUseCase.getEventsByMonth(currentYear, month, page, pageSize);
                return Response.ok(response).build();
            }

            response = eventsUseCase.getAllEvents(page, pageSize);

            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error on getting events: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response listEventById(@PathParam("id") Long id) {
        try {
            EventResponseDto response = eventsUseCase.getEventById(id);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error on getting event: " + e.getMessage()).build();
        }
    }

    @POST
    public Response createEvent(EventCreateDto request) {
        try {
            Event created = createEventUseCase.execute(request);
            URI location = URI.create("/eventos/" + created.getId());
            return Response.created(location).entity(created).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error on creating event: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") Long id) {
        try {
            deleteEventByIdUseCase.execute(id);
            return Response.noContent().build(); // 204
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error on deleting event:" + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEvent(@PathParam("id") Long id, EventUpdateDto eventUpdateDto) {
        try {
            EventResponseDto updatedEvent = updateEventUseCase.updateEvent(id, eventUpdateDto);
            return Response.ok(updatedEvent).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating event").build();
        }
    }
    
    
    private LocalDate parseIsoDateOrThrow(String iso) {
        try {
            return LocalDate.parse(iso); // ISO-8601 yyyy-MM-dd
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Parâmetro 'date' inválido. Use o formato ISO: yyyy-MM-dd (ex: 2025-04-05).");
        }
    }
    
    private void validateMonth(Integer month) {
        if (month < 1 || month > 12) {
            throw new BadRequestException("Parâmetro 'month' inválido. Deve estar entre 1 e 12.");
        }
    }
    
    private void validateDay(Integer day) {
        if (day < 1 || day > 31) {
            throw new BadRequestException("Parâmetro 'day' inválido. Deve estar entre 1 e 31.");
        }
    }
    
    private LocalDate buildDateOrThrow(Integer year, Integer month, Integer day) {
        try {
            return LocalDate.of(year, month, day);
        } catch (Exception ex) {
            throw new BadRequestException("Data inválida: verifique combinações como 30/02 ou ano/mês/dia fora do intervalo.");
        }
    }
}


