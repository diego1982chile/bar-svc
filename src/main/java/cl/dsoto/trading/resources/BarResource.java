package cl.dsoto.trading.resources;

import cl.dsoto.trading.entities.BarEntity;
import cl.dsoto.trading.model.TimeFrame;
import cl.dsoto.trading.services.BarService;
import lombok.extern.log4j.Log4j;
import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Produces(APPLICATION_JSON)
@Path("bars")
//@RolesAllowed({"ADMIN","USER"})
@PermitAll
@Log4j
public class BarResource {

    @Inject
    BarService barService;

    @GET
    @Produces("application/json")
    public Response getBars(@QueryParam("timeFrame") TimeFrame timeFrame,
                            @QueryParam("start") String start,
                            @QueryParam("id") String end) {
        try {
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);

            ZonedDateTime zonedStartDate = startDate.atStartOfDay(ZoneId.of("UTC"));
            ZonedDateTime zonedEndDate = endDate.atStartOfDay(ZoneId.of("UTC"));

            List<Bar> bars = barService.getBars(timeFrame, zonedStartDate, zonedEndDate);
            Jsonb jsonb = JsonbBuilder.create();
            return Response.ok(jsonb.toJson(bars)).build();
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
        return Response.serverError().build();
    }


}
