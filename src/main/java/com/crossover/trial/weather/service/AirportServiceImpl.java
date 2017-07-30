package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.utils.ConstantHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * The implementation of the Airport Data Service which will make operations on all airport's
 * data available on the system
 *
 * @author Victor Polanco
 *
 */
public class AirportServiceImpl implements AirportService {

  /** all known airports */
  private Map<String, AirportData> airportDataMap = new ConcurrentHashMap<String, AirportData>();

  public final static Logger LOGGER = Logger.getLogger(AirportServiceImpl.class.getName());

  public AirportServiceImpl() { this.init(); }

  private void init() {
    addAirport("BOS", "42.364347", "-71.005181");
    addAirport("EWR", "40.6925", "-74.168667");
    addAirport("JFK", "40.639751", "-73.778925");
    addAirport("LGA", "40.777245", "-73.872608");
    addAirport("MMU", "40.79935", "-74.4148747");
  }

  /**
   * Given an iataCode find the airport data
   *
   * @param iataCode as a string
   * @return airport data or null if not found
   */
  public AirportData findAirportData(String iataCode) {
    return airportDataMap.getOrDefault(iataCode, null);
  }

  @Override
  public List<AirportData> getAirportDataInRadius(String iataCode, double radius) {
    // Find the airport with the given code
    AirportData airportDataCenter = this.findAirportData(iataCode);
    // If there's an airport data with the given code, return it
    // else return an empty list
    if (airportDataCenter != null) {
      return airportDataMap.values().stream()
          .filter(airport -> calculateDistance(airportDataCenter, airport) <= radius)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  public List<AirportData> getAllAirportData() {
    return new ArrayList<>(airportDataMap.values());
  }

  /**
   * Add a new known airport to our list.
   *
   * @param iataCode 3 letter code
   * @param latitude in degrees
   * @param longitude in degrees
   *
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
      airportDataMap.put(iataCode, new AirportData() {{
        setIata(iataCode);
        setLatitude(convertedLatitude);
        setLongitude(convertedLongitude);
      }});
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
   *
   * @return the status code of the operation
   */
  @Override
  public Status deleteAirport(String iataCode) {
    Status responseStatus = Status.OK;


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
    double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
        * Math.cos(ad1.latitude) * Math.cos(ad2.latitude);
    double c = 2 * Math.asin(Math.sqrt(a));
    return ConstantHelper.R * c;
  }

}
