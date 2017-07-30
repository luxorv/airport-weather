package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.service.AirportService;
import com.crossover.trial.weather.service.AirportServiceImpl;
import com.crossover.trial.weather.service.AtmosphericInformationService;
import com.crossover.trial.weather.service.AtmosphericInformationServiceImpl;
import com.crossover.trial.weather.utils.GsonFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */
@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
  public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

  private AirportService airportService;

  private AtmosphericInformationService atmosphericInformationService;

  public RestWeatherCollectorEndpoint() {
    airportService = new AirportServiceImpl();
    atmosphericInformationService = new AtmosphericInformationServiceImpl();
  }

  @Override
  @GET
  @Path("/ping")
  public Response ping() {
    return Response.status(Response.Status.OK).entity("ready").build();
  }

  @Override
  @POST
  @Path("/weather/{iata}/{pointType}")
  public Response updateWeather(@PathParam("iata") String iataCode,
      @PathParam("pointType") String pointType,
      String datapointJson) {
    DataPoint dataPoint = GsonFactory.getGsonFromJsonString(datapointJson, DataPoint.class);
    return Response.status(atmosphericInformationService
        .updateAtmosphericInformationForAirport(iataCode, pointType, dataPoint))
        .build();
  }


  @Override
  @GET
  @Path("/airports")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAirports() {
    return Response.status(Response.Status.OK).entity(airportService.getAllAirportData()).build();
  }


  @Override
  @GET
  @Path("/airport/{iata}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAirport(@PathParam("iata") String iata) {
    return Response.status(Response.Status.OK).entity(airportService.findAirportData(iata)).build();
  }


  @Override
  @POST
  @Path("/airport/{iata}/{lat}/{long}")
  public Response addAirport(@PathParam("iata") String iata,
      @PathParam("lat") String latString,
      @PathParam("long") String longString) {
    return Response.status(airportService
        .addAirport(iata, latString, longString))
        .build();
  }


  @Override
  @DELETE
  @Path("/airport/{iata}")
  public Response deleteAirport(@PathParam("iata") String iata) {
    return Response.status(airportService.deleteAirport(iata)).build();
  }

  @Override
  @GET
  @Path("/exit")
  public Response exit() {
    System.exit(0);
    return Response.noContent().build();
  }
}
