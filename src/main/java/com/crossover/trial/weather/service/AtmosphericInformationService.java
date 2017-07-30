package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import java.util.List;
import javax.ws.rs.core.Response.Status;

/**
 * The definition of the Atmospheric Information Service which will make operations on all atmospheric
 * information available on the system
 *
 * @author Victor Polanco
 *
 */
public interface AtmosphericInformationService {

  /**
   * Given an iataCode find the airport data
   *
   * @param iataCode as a string
   *
   * @return {@link AtmosphericInformation} or null if not found
   */
  public AtmosphericInformation getAtmosphericInformationForAirport(String iataCode);

  /**
   * Retrieve the most up to date atmospheric information from the given airport and other airports in the given
   * radius.
   *
   * @param iataCode the three letter airport code
   * @param radius the radius, in km, from which to collect weather data
   *
   * @return a list of {@link AtmosphericInformation} from the requested airport and
   * airports in the given radius
   */
  public List<AtmosphericInformation> getAtmosphericInformationAroundAirportInRadius(
      String iataCode, double radius);

  /**
   * Retrieve all the atmospheric information available
   *
   * @return a list of {@link AtmosphericInformation} on the system
   */
  public List<AtmosphericInformation> getAllAtmosphericInformation();

  /**
   * Retrieve the most up to date atmospheric information from the given airport and other airports in the given
   * radius.
   *
   * @param iataCode the three letter airport code
   * @param atmosphericInformation(optional) the atmospheric infomation of the airport
   *
   */
  public void addAtmosphericInformationForAirport
      (String iataCode, AtmosphericInformation atmosphericInformation);

  /**
   * Update atmospheric information with the given data point for the given point type
   *
   * @param iataCode the airport to update it's atmospheric information
   * @param pointType the data point type as a string
   * @param dataPoint the actual data point
   *
   * @return Status code indicating the state of the update
   */
  public Status updateAtmosphericInformationForAirport
      (String iataCode, String pointType, DataPoint dataPoint);

}
