package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.storage.ConcurrentAirportDataStorage;
import com.crossover.trial.weather.model.storage.ConcurrentAtmosphericInfoStorage;
import com.crossover.trial.weather.utils.ConstantHelper;
import com.crossover.trial.weather.utils.GsonFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Weather metrics provider, used to recollect data on all the services, this is used in all
 * operations and retrieve health and status information.
 *
 * @author Victor Polanco.
 */
public class WeatherMetricsProvider {

  // Logger for the metrics class.
  public static final Logger LOGGER = Logger.getLogger("Weather Metrics");

  /** Concurrent mappings of the frequencies of {@link AirportData} and radius */
  private ConcurrentHashMap<String, Integer> airportDataMetrics;

  private ConcurrentHashMap<Integer, Integer> radiusMetrics;

  private WeatherMetricsProvider() {
    LOGGER.log(Level.INFO, "Instantiating a new metrics object");
    this.airportDataMetrics = new ConcurrentHashMap<>();
    this.radiusMetrics = new ConcurrentHashMap<>();
  }

  /** Dummy method to initiate the metrics object with the hard coded airports */
  public void initMetrics() {
    ConcurrentAirportDataStorage<String, Integer> storage =
        ConcurrentAirportDataStorage.getInstance();
    LOGGER.log(Level.INFO, "Refreshing the metrics with all airports: \n" + storage.keySet());
    storage.keySet().forEach(entry -> airportDataMetrics.put(entry, 0));
  }

  /**
   * Get the metrics of all radius that were requested during the history of the airport weather
   * application.
   *
   * @return {@link HashMap<Integer, Integer>} of the metrics.
   */
  public HashMap<Integer, Integer> getRadiusMetrics() {
    LOGGER.log(Level.INFO, "Getting the radius metrics: \n" + GsonFactory.toJson(radiusMetrics));
    return new HashMap<>(radiusMetrics);
  }

  /**
   * Get the metrics of all airport that were requested/modified during the history of the airport
   * weather application.
   *
   * @return {@link HashMap<String, Integer>} of the metrics.
   */
  public HashMap<String, Integer> getAirportMetrics() {
    LOGGER.log(
        Level.INFO, "Getting the airport metrics: \n" + GsonFactory.toJson(airportDataMetrics));
    return new HashMap<>(airportDataMetrics);
  }

  /**
   * Get the data size of all atmospheric information held in the system.
   *
   * @return the data size of the atmospheric information list.
   */
  public long getAtmosphericInfoDataSize() {
    // Get the instance of the concurrent atmospheric information storage.
    ConcurrentAtmosphericInfoStorage<String, AtmosphericInformation> storage =
        ConcurrentAtmosphericInfoStorage.getInstance();
    LOGGER.log(Level.INFO, "Getting the atmospheric data size " + storage.entrySet());
    // Get the count of the updated atmospheric information that were updated no later than one day
    return storage
        .entrySet()
        .stream()
        .filter(
            entry ->
                entry.getValue().getLastUpdateTime()
                    > System.currentTimeMillis() - ConstantHelper.ONE_DAY)
        .count();
  }

  /**
   * Updates the metrics of each given airport code getting requested on the application by
   * increasing the frequency in the given code by one
   *
   * @param iataCode 3 digit code of the airport.
   */
  public void updateAirportMetrics(String iataCode) {
    LOGGER.log(Level.INFO, "Updating airport metrics\n");
    Integer newValue = airportDataMetrics.getOrDefault(iataCode, 0) + 1;
    airportDataMetrics.put(iataCode, newValue);
  }

  /**
   * Updates the metrics of each radius getting requested on the application by increasing the
   * frequency in the given radius by one
   *
   * @param radius in km.
   */
  public void updateRadiusMetrics(Double radius) {
    LOGGER.log(Level.INFO, "Updating radius metrics\n");
    Integer newValue = radiusMetrics.getOrDefault(radius, 0) + 1;
    radiusMetrics.put(radius.intValue(), newValue);
  }

  /** Private inner class holder of the instance of the {@link WeatherMetricsProvider} */
  private static class MetricsHolder {
    private static WeatherMetricsProvider INSTANCE = new WeatherMetricsProvider();
  }

  /**
   * Get the metrics provider instance currently active on the system.
   *
   * @return {@link WeatherMetricsProvider} instance.
   */
  public static WeatherMetricsProvider getInstance() {
    return MetricsHolder.INSTANCE;
  }
}
