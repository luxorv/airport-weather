package com.crossover.trial.weather.service;

import com.crossover.trial.weather.model.AirportData;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Luxorv on 7/30/17.
 */
public class WeatherMetricsProvider {

  private ConcurrentHashMap<AirportData, Integer> airportDataMetrics;

  private WeatherMetricsProvider() {
    this.airportDataMetrics = new ConcurrentHashMap<AirportData, Integer>();
  }

  private static class MonitorHolder {
    private static WeatherMetricsProvider INSTANCE = new WeatherMetricsProvider();
  }

  public static WeatherMetricsProvider getInstance() {
    return MonitorHolder.INSTANCE;
  }

}
