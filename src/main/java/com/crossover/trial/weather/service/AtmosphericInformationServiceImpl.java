package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.storage.ConcurrentAtmosphericInfoStorage;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.utils.ReflectionUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * The implementation of the Atmospheric Information Service which will make operations on all
 * atmospheric information available on the system, currently implements {@link
 * AtmosphericInformationService}
 *
 * @author Victor Polanco
 */
public class AtmosphericInformationServiceImpl implements AtmosphericInformationService {

  private static final Logger LOGGER =
      Logger.getLogger(AtmosphericInformationServiceImpl.class.getName());

  /** Concurrent Storage singleton for the Atmospheric Information */
  private ConcurrentAtmosphericInfoStorage<String, AtmosphericInformation>
      atmosphericInformationMap;

  public AtmosphericInformationServiceImpl() {
    atmosphericInformationMap = ConcurrentAtmosphericInfoStorage.getInstance();
  }

  /**
   * Given an iataCode find the airport data
   *
   * @param iataCode as a string
   * @return {@link AtmosphericInformation} or null if not found
   */
  @Override
  public AtmosphericInformation getAtmosphericInformationForAirport(String iataCode) {
    return atmosphericInformationMap.getOrDefault(iataCode, null);
  }

  /**
   * Retrieve the most up to date atmospheric information from the given airports
   *
   * @param airportData list of airports to retrieve the atmospheric information
   * @return a list of {@link AtmosphericInformation} from the requested airport and airports in the
   *     given radius
   */
  @Override
  public List<AtmosphericInformation> getAtmosphericInformationForAirports(
      List<AirportData> airportData) {
    // return a list of atmospheric information of the given airports
    return new ArrayList<AtmosphericInformation>() {
      {
        airportData
            .stream()
            .filter(airport -> atmosphericInformationMap.containsKey(airport.getIata()))
            .forEach(airport -> add(atmosphericInformationMap.get(airport.getIata())));
      }
    };
  }

  /**
   * Retrieve all the atmospheric information available
   *
   * @return a list of {@link AtmosphericInformation} on the system
   */
  @Override
  public List<AtmosphericInformation> getAllAtmosphericInformation() {
    return new ArrayList<>(atmosphericInformationMap.values());
  }

  /**
   * Retrieve the most up to date atmospheric information from the given airport and other airports
   * in the given radius.
   *
   * @param iataCode the three letter airport code
   * @param atmosphericInformation(optional) the atmospheric infomation of the airport
   */
  @Override
  public void addAtmosphericInformationForAirport(
      String iataCode, AtmosphericInformation atmosphericInformation) {
    /**
     * Insert a new association of airport iataCode with an Atmospheric information in the case the
     * atmospheric information is not given just create an empty association
     */
    atmosphericInformationMap.putIfAbsent(
        iataCode,
        atmosphericInformation != null ? atmosphericInformation : new AtmosphericInformation());
  }

  /**
   * Update atmospheric information with the given data point for the given point type
   *
   * @param iataCode the airport to update it's atmospheric information
   * @param pointType the data point type as a string
   * @param dataPoint the actual data point
   * @return Status code indicating the state of the update
   */
  @Override
  public Status updateAtmosphericInformationForAirport(
      String iataCode, String pointType, DataPoint dataPoint) {

    // Get the lower case of the pointType
    pointType = pointType.toLowerCase();
    // Set an OK status code
    Status responseStatus = Response.Status.OK;

    try {
      // Get the atmospheric information corresponding to the iataCode
      AtmosphericInformation atmosphericInformation =
          atmosphericInformationMap.getOrDefault(iataCode, new AtmosphericInformation());
      // Set the property to the atmospheric information.
      ReflectionUtils.setPropertyToAtmosphericInfo(atmosphericInformation, pointType, dataPoint);
      // Set the atmospheric information for the airport for the first time
      atmosphericInformationMap.putIfAbsent(iataCode, atmosphericInformation);
    } catch (NoSuchMethodException e) {
      // If there's a no such method exception the pointType is incorrect so we return bad request
      responseStatus = Status.BAD_REQUEST;
      LOGGER.info("Bad request of the data point type " + iataCode + " - " + pointType);
    } catch (IllegalAccessException e) {
      // The pointType is incorrect so we return bad request
      responseStatus = Status.BAD_REQUEST;
      LOGGER.info("Invalid data point type for atmospheric information " + pointType);
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      /**
       * If there's a null pointer exception it means that there's no atmospheric information for
       * the given airport code, a 404 not found is returned because there's no resource for the
       * given code
       */
      responseStatus = Status.NOT_FOUND;
      LOGGER.info("No such airport data found on the system " + iataCode);
    }
    return responseStatus;
  }
}
