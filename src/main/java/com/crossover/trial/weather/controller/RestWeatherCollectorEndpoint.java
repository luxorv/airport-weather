package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.service.*;
import com.crossover.trial.weather.utils.GsonFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */
@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

  /** Logger for all logs inside this service */
  public static final Logger LOGGER =
      Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

  /** Alive signal for the service */
  private static boolean alive = true;

  /** Airport service responsible for all operations on airports. */
  private AirportService airportService;

  /** Atmospheric information service responsible for all operations on atmospheric information. */
  private AtmosphericInformationService atmosphericInformationService;

  /**
   * Weather metrics provider, used to recollect data on all the services, this is used in all
   * operations and retrieve health and status information
   */
  private WeatherMetricsProvider weatherMetricsProvider;

  public RestWeatherCollectorEndpoint() {
    LOGGER.log(Level.INFO, "New Rest Weather Collector Endpoint instantiated");
    airportService = new AirportServiceImpl();
    atmosphericInformationService = new AtmosphericInformationServiceImpl();
    weatherMetricsProvider = WeatherMetricsProvider.getInstance();
  }

  /**
   * A liveliness check for the collection endpoint.
   *
   * @return 1 if the endpoint is alive functioning, 0 otherwise
   */
  @Override
  @GET
  @Path("/ping")
  public Response ping() {
    LOGGER.log(Level.INFO, "Retrieving connection status for the Weather Collector");
    // Depending on the live status we set OK or GONE to the response
    Response.Status status = alive ? Response.Status.OK : Response.Status.GONE;
    // And set "1" or "0" to the query
    String check = alive ? "1" : "0";
    return Response.status(status).entity(check).build();
  }

  /**
   * Update the airports atmospheric information for a particular pointType with json formatted data
   * point information.
   *
   * @param iataCode the 3 letter airport code
   * @param pointType the point type, {@link DataPointType} for a complete list
   * @param datapointJson a json dict containing mean, first, second, thrid and count keys
   * @return HTTP Response code
   */
  @Override
  @POST
  @Path("/weather/{iata}/{pointType}")
  public Response updateWeather(
      @PathParam("iata") String iataCode,
      @PathParam("pointType") String pointType,
      String datapointJson) {
    // Get the data point object from the provided Json.
    DataPoint dataPoint = GsonFactory.getGsonFromJsonString(datapointJson, DataPoint.class);
    // Update the atmospheric information on the given airport.
    return Response.status(
            atmosphericInformationService.updateAtmosphericInformationForAirport(
                iataCode, pointType, dataPoint))
        .build();
  }

  /**
   * Return a list of known airports as a json formatted list
   *
   * @return HTTP Response code and a json formatted list of IATA codes
   */
  @Override
  @GET
  @Path("/airports")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAirports() {
    return Response.status(Response.Status.OK).entity(airportService.getAllAirportData()).build();
  }

  /**
   * Retrieve airport data, including latitude and longitude for a particular airport
   *
   * @param iata the 3 letter airport code
   * @return an HTTP Response with a json representation of {@link AirportData}
   */
  @Override
  @GET
  @Path("/airport/{iata}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAirport(@PathParam("iata") String iata) {
    return Response.status(Response.Status.OK).entity(airportService.findAirportData(iata)).build();
  }

  /**
   * Add a new airport to the known airport list.
   *
   * @param iata the 3 letter airport code of the new airport
   * @param latString the airport's latitude in degrees as a string [-90, 90]
   * @param longString the airport's longitude in degrees as a string [-180, 180]
   * @return HTTP Response code for the add operation
   */
  @Override
  @POST
  @Path("/airport/{iata}/{lat}/{long}")
  public Response addAirport(
      @PathParam("iata") String iata,
      @PathParam("lat") String latString,
      @PathParam("long") String longString) {
    // Add a new airport entry on the airport data storage.
    Response.Status status = airportService.addAirport(iata, latString, longString);
    // If the status is OK we add a new entry into the atmospheric information for the corresponding airport.
    if (status == Response.Status.OK) {
      // Add an empty atmospheric information entry for the new airport
      atmosphericInformationService.addAtmosphericInformationForAirport(
          iata, new AtmosphericInformation());
    }
    return Response.status(status).build();
  }

  /**
   * Remove an airport from the known airport list
   *
   * @param iata the 3 letter airport code
   * @return HTTP Repsonse code for the delete operation
   */
  @Override
  @DELETE
  @Path("/airport/{iata}")
  public Response deleteAirport(@PathParam("iata") String iata) {
    return Response.status(airportService.deleteAirport(iata)).build();
  }

  /** Starts up the Weather Collector service. */
  @GET
  @Path("/start")
  public Response start() {
    RestWeatherCollectorEndpoint.alive = true;
    return Response.noContent().build();
  }

  /** Shuts down the Weather Collector service */
  @Override
  @GET
  @Path("/exit")
  public Response exit() {
    RestWeatherCollectorEndpoint.alive = false;
    return Response.noContent().build();
  }

  /** Dummy method to init the service, this will be called once the server starts running. */
  public static void init() {
    AirportService airportService = new AirportServiceImpl();
    airportService.addAirport("BOS", "42.364347", "-71.005181");
    airportService.addAirport("EWR", "40.6925", "-74.168667");
    airportService.addAirport("JFK", "40.639751", "-73.778925");
    airportService.addAirport("LGA", "40.777245", "-73.872608");
    airportService.addAirport("MMU", "40.79935", "-74.4148747");
    WeatherMetricsProvider.getInstance().initMetrics();
  }
}
