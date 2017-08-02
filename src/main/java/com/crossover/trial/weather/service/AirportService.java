package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import java.util.List;
import javax.ws.rs.core.Response.Status;
import org.jvnet.hk2.annotations.Contract;

/**
 * The definition of the Airport Data Service which will make operations on all airport's data
 * available on the system
 *
 * @author Victor Polanco
 */
public interface AirportService {

  /**
   * Get an airport data for a given code.
   *
   * @param iataCode 3 letter code
   * @return the {@link AirportData} for the given code or null if not found
   */
  public AirportData findAirportData(String iataCode);

  /**
   * For a given airport get all airports around a given radius including the given airport.
   *
   * @param iataCode 3 letter code
   * @param radius in km
   * @return a list of {@link AirportData} of all the airports around the given code.
   */
  public List<AirportData> getAirportDataInRadius(String iataCode, double radius);

  /**
   * Add a new known airport to our list.
   *
   * @return a list of all {@link AirportData} currently in the system.
   */
  public List<AirportData> getAllAirportData();

  /**
   * Add a new known airport to our list.
   *
   * @param iataCode 3 letter code
   * @param latitude in degrees
   * @param longitude in degrees
   * @return the status code of the operation
   */
  public Status addAirport(String iataCode, String latitude, String longitude);

  /**
   * Delete an Airport from our System
   *
   * @param iataCode 3 letter code
   * @return the status code of the operation
   */
  public Status deleteAirport(String iataCode);
}
