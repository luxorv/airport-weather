package com.crossover.trial.weather.model.storage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The implementation of the Atmospheric Information Storage which will serve as a cache of the
 * Atmospheric information data of the airports
 *
 * @author Victor Polanco
 */
public class ConcurrentAtmosphericInfoStorage<String, AtmosphericInformation>
    extends ConcurrentHashMap<String, AtmosphericInformation> {

  /** Private constructor preventing other classes to instantiate the class */

  private ConcurrentAtmosphericInfoStorage() {}

  /**
   * Get instance method
   *
   * @return the instance of the ConcurrentAtmosphericInformationStorage.
   *
   * */

  public static ConcurrentAtmosphericInfoStorage getInstance() {
    return StorageHolder.INSTANCE;
  }

  /**
   *  Private static inner class to serve as a holder for the instance this pattern makes
   * a thread safe environment to hold the instance of the class.
   *
   * */

  private static class StorageHolder {
    // Actual instance of the storage
    private static ConcurrentAtmosphericInfoStorage INSTANCE =
        new ConcurrentAtmosphericInfoStorage<>();
  }
}
