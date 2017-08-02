package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.storage.ConcurrentAirportDataStorage;
import com.crossover.trial.weather.utils.ConstantHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response.Status;

/**
 * The implementation of the Airport Data Service which will make operations on all airport's data
 * available on the system, currently implements {@link AirportService}
 *
 * @author Victor Polanco
 */
public class AirportServiceImpl implements AirportService {

  /** Concurrent Storage singleton for the airport data */
  private ConcurrentAirportDataStorage<String, AirportData> airportDataMap;

  public static final Logger LOGGER = Logger.getLogger(AirportServiceImpl.class.getName());

  public AirportServiceImpl() {
    airportDataMap = ConcurrentAirportDataStorage.getInstance();
  }

  /**
   * Get an airport data for a given code.
   *
   * @param iataCode 3 letter code
   * @return the {@link AirportData} for the given code or null if not found
   */
  public AirportData findAirportData(String iataCode) {
    return airportDataMap.getOrDefault(iataCode, null);
  }

  /**
   * For a given airport get all airports around a given radius including the given airport.
   *
   * @param iataCode 3 letter code
   * @param radius in km
   * @return a list of {@link AirportData} of all the airports around the given code.
   */
  @Override
  public List<AirportData> getAirportDataInRadius(String iataCode, double radius) {
    // Find the airport with the given code
    AirportData airportDataCenter = this.findAirportData(iataCode);
    // If there's an airport data with the given code, return it
    // else return an empty list
    if (airportDataCenter != null) {
      return airportDataMap
          .values()
          .stream()
          .filter(airport -> calculateDistance(airportDataCenter, airport) <= radius)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * Add a new known airport to our list.
   *
   * @return a list of all {@link AirportData} currently in the system.
   */
  public List<AirportData> getAllAirportData() {
    return new ArrayList<>(airportDataMap.values());
  }

  /**
   * Add a new known airport to our list.
   *
   * @param iataCode 3 letter code
   * @param latitude in degrees
   * @param longitude in degrees
   * @return the status code of the operation
   */
  @Override
  public Status addAirport(String iataCode, String latitude, String longitude) {
    double convertedLatitude, convertedLongitude;
    Status responseStatus = Status.OK;

    try {
      // Try to parse the latitude and longitude
      convertedLatitude = Double.valueOf(latitude);
      convertedLongitude = Double.valueOf(longitude);

      // If it was parsed correctly add a new airport to the system
      airportDataMap.putIfAbsent(
          iataCode,
          new AirportData() {
            {
              setIata(iataCode);
              setLatitude(convertedLatitude);
              setLongitude(convertedLongitude);
            }
          });
    } catch (NumberFormatException e) {
      // If there's a number format exception there's a problem with the request!
      responseStatus = Status.BAD_REQUEST;
      LOGGER.info("Invalid latitude " + latitude + " or invalid longitude" + longitude);
    }

    return responseStatus;
  }

  /**
   * Delete an Airport from our System
   *
   * @param iataCode 3 letter code
   * @return the status code of the operation
   */
  @Override
  public Status deleteAirport(String iataCode) {
    Status responseStatus = Status.OK;

    if (airportDataMap.containsKey(iataCode)) {
      airportDataMap.remove(iataCode);
    } else {
      responseStatus = Status.NOT_FOUND;
    }

    return responseStatus;
  }

  /**
   * Haversine distance between two airports.
   *
   * @param ad1 airport 1
   * @param ad2 airport 2
   * @return the distance in KM
   */
  private double calculateDistance(AirportData ad1, AirportData ad2) {
    double deltaLat = Math.toRadians(ad2.latitude - ad1.latitude);
    double deltaLon = Math.toRadians(ad2.longitude - ad1.longitude);
    double a =
        Math.pow(Math.sin(deltaLat / 2), 2)
            + Math.pow(Math.sin(deltaLon / 2), 2) * Math.cos(ad1.latitude) * Math.cos(ad2.latitude);
    double c = 2 * Math.asin(Math.sqrt(a));
    return ConstantHelper.R * c;
  }
}
