package com.crossover.trial.weather.model.storage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of the Airport Data Storage which will serve as a cache
 * of the data of the airports
 *
 * @author Victor Polanco
 */
public class ConcurrentAirportDataStorage <String, AirportData>
    extends ConcurrentHashMap <String, AirportData> {

  private ConcurrentAirportDataStorage() {}

  public static ConcurrentAirportDataStorage getInstance() {
    return StorageHolder.INSTANCE;
  }

  private static class StorageHolder {
    private static ConcurrentAirportDataStorage INSTANCE =
        new ConcurrentAirportDataStorage<>();
  }
}
