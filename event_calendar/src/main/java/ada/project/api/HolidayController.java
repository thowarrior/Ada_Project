package ada.project.api;

import ada.project.api.responseDTO.HolidayDTO;
import ada.project.clients.holiday.HolidaysService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.List;

@Path("/feriados")
@Produces(MediaType.APPLICATION_JSON)
public class HolidayController {
    @Inject
    HolidaysService holidaysService;

    @GET
    @Path("/{year}")
    public Response listHolidayByYear(@PathParam("year") Integer year, @QueryParam("month") Integer month) {
        try {
            int _yaer = year == null ? LocalDate.now().getYear() : year;
            List<HolidayDTO> holidayDTOList = holidaysService.holidayDTOListByYear(_yaer);

            if(month != null) {
                holidayDTOList = holidayDTOList.stream().filter(holiday -> holiday.date.getMonth().getValue() == month).toList();
            }

            return Response.ok(holidayDTOList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error on getting holiday by year" + year + ": " + e.getMessage()).build();
        }
    }
}
