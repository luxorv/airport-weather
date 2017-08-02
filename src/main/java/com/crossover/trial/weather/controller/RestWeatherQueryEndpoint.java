package com.crossover.trial.weather.controller;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.service.*;
import com.crossover.trial.weather.utils.ConstantHelper;
import com.crossover.trial.weather.utils.GsonFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently,
 * all data is held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

  /** Logger for all logs inside this service */
  public static final Logger LOGGER = Logger.getLogger("WeatherQuery");

  /** Airport service responsible for all operations on airports. */
  private AirportService airportService;

  /** Atmospheric information service responsible for all operations on atmospheric information. */
  private AtmosphericInformationService atmosphericInformationService;

  /**
   * Weather metrics provider, used to recollect data on all the services, this is used in all
   * operations and retrieve health and status information
   */
  private WeatherMetricsProvider weatherMetricsProvider;

  public RestWeatherQueryEndpoint() {
    airportService = new AirportServiceImpl();
    atmosphericInformationService = new AtmosphericInformationServiceImpl();
    weatherMetricsProvider = WeatherMetricsProvider.getInstance();
  }

  /**
   * Retrieve health and status information for the the query api. Returns information about how the
   * number of data points currently held in memory, the frequency of requests for each IATA code
   * and the frequency of requests for each radius.
   *
   * @return a JSON formatted dict with health information.
   */
  @Override
  @GET
  @Path("/ping")
  public String ping() {
    Map<String, Object> metrics = new HashMap<>();
    // Get the data size of the atmospheric information
    metrics.put("datasize", weatherMetricsProvider.getAtmosphericInfoDataSize());
    // Get the airport metrics
    metrics.put("iata_freq", weatherMetricsProvider.getAirportMetrics());
    // Get the radius metrics
    metrics.put("radius_freq", weatherMetricsProvider.getRadiusMetrics());
    // Log the metrics data.
    LOGGER.log(Level.INFO, "Getting the metrics information " + metrics);
    // Return a new Json file.
    return GsonFactory.toJson(metrics);
  }

  /**
   * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport
   * information and return a list of matching atmosphere information.
   *
   * @param iata the iataCode
   * @param radiusString the radius in km
   * @return a list of atmospheric information
   */
  @GET
  @Path("/weather/{iata}/{radius}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response weather(
      @PathParam("iata") String iata, @PathParam("radius") String radiusString) {
    Double radius = ConstantHelper.getValidDouble(radiusString);
    if (radius == Double.NaN) {
      // Log a warning that a invalid radius was introduced.
      LOGGER.log(
          Level.WARNING,
          "Invalid radius trying to get the weather data for the airport "
              + iata
              + " with radius "
              + radiusString);
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid radius string").build();
    }
    // Update the request frequency
    updateRequestFrequency(iata, radius);
    // Get all airports in the given radius.
    List<AirportData> airportData = airportService.getAirportDataInRadius(iata, radius);
    // Return all airports' atmospheric information for the given radius.
    return Response.status(Response.Status.OK)
        .entity(atmosphericInformationService.getAtmosphericInformationForAirports(airportData))
        .build();
  }

  /**
   * Records information about how often requests are made
   *
   * @param iata an iata code
   * @param radius query radius
   */
  private void updateRequestFrequency(String iata, Double radius) {
    weatherMetricsProvider.updateAirportMetrics(iata);
    weatherMetricsProvider.updateRadiusMetrics(radius);
  }
}
