package com.crossover.trial.weather.model.storage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of the Airport Data Storage which will serve as a cache of the data of the
 * airports
 *
 * @author Victor Polanco
 */
public class ConcurrentAirportDataStorage<String, AirportData>
    extends ConcurrentHashMap<String, AirportData> {

  /** Private constructor preventing other classes to instantiate the class */

  private ConcurrentAirportDataStorage() {}

  /**
   * Get instance method
   *
   * @return the instance of the ConcurrentAirportDataStorage.
   *
   * */

  public static ConcurrentAirportDataStorage getInstance() {
    return StorageHolder.INSTANCE;
  }

  /**
   *  Private static inner class to serve as a holder for the instance this pattern makes
   * a thread safe environment to hold the instance of the class.
   *
   * */

  private static class StorageHolder {
    private static ConcurrentAirportDataStorage INSTANCE = new ConcurrentAirportDataStorage<>();
  }
}
