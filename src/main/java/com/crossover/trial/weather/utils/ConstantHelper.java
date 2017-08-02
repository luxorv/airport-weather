package com.crossover.trial.weather.utils;

/** Constant helper class, contains static properties and methods used across the system */
public class ConstantHelper {

  /** earth radius in KM */
  public static final double R = 6372.8;

  /** One day expressed in miliseconds. */
  public static final int ONE_DAY = 86400000;

  /**
   * Validate if a given string is a actual double and returns the parsed double.
   *
   * @param radiusString the double to be validated
   * @return the converted double
   */
  public static Double getValidDouble(String radiusString) {
    // If the radius is empty return 0.0
    if (radiusString == null || radiusString.trim().isEmpty()) {
      return 0.0;
    }
    // Else try to parse the given radius.
    try {
      // Try to parse the given radius string.
      return Double.valueOf(radiusString);
    } catch (NumberFormatException e) {
      // Return NaN.
      return Double.NaN;
    }
  }
}
