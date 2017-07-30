package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import java.util.List;
import javax.ws.rs.core.Response.Status;

/**
 * The definition of the Airport Data Service which will make operations on all airport's
 * data available on the system
 *
 * @author Victor Polanco
 *
 */
public interface AirportService {

  public AirportData findAirportData(String iataCode);

  public List<AirportData> getAirportDataInRadius(String iataCode, double radius);

  public List<AirportData> getAllAirportData();

  /**
   * Add a new known airport to our list.
   *
   * @param iataCode 3 letter code
   * @param latitude in degrees
   * @param longitude in degrees
   *
   * @return the status code of the operation
   */
  public Status addAirport(String iataCode, String latitude, String longitude);

  /**
   * Delete an Airport from our System
   *
   * @param iataCode 3 letter code
   *
   * @return the status code of the operation
   */
  public Status deleteAirport(String iataCode);
}
