package com.crossover.trial.weather.model.storage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of the Atmospheric Information Storage which will serve as a cache
 * of the Atmospheric information data of the airports
 *
 * @author Victor Polanco
 */
public class ConcurrentAtmosphericInfoStorage <String, AtmosphericInformation>
    extends ConcurrentHashMap <String, AtmosphericInformation> {

  private ConcurrentAtmosphericInfoStorage() {}

  public static ConcurrentAtmosphericInfoStorage getInstance() {
    return StorageHolder.INSTANCE;
  }

  private static class StorageHolder {
    private static ConcurrentAtmosphericInfoStorage INSTANCE =
        new ConcurrentAtmosphericInfoStorage<>();
  }
}
