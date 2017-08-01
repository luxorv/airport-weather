package com.crossover.trial.weather.model;

/** encapsulates sensor information for a particular location */
public class AtmosphericInformation {

  /** temperature in degrees celsius */
  private DataPoint temperature;

  /** wind speed in km/h */
  private DataPoint wind;

  /** humidity in percent */
  private DataPoint humidity;

  /** precipitation in cm */
  private DataPoint precipitation;

  /** pressure in mmHg */
  private DataPoint pressure;

  /** cloud cover percent from 0 - 100 (integer) */
  private DataPoint cloudCover;

  /** the last time this data was updated, in milliseconds since UTC epoch */
  private long lastUpdateTime;

  public AtmosphericInformation() {}

  protected AtmosphericInformation(
      DataPoint temperature,
      DataPoint wind,
      DataPoint humidity,
      DataPoint percipitation,
      DataPoint pressure,
      DataPoint cloudCover) {
    this.temperature = temperature;
    this.wind = wind;
    this.humidity = humidity;
    this.precipitation = percipitation;
    this.pressure = pressure;
    this.cloudCover = cloudCover;
    this.lastUpdateTime = System.currentTimeMillis();
  }

  public DataPoint getTemperature() {
    return temperature;
  }

  public void setTemperature(DataPoint temperature) {
    this.temperature = temperature;
  }

  public DataPoint getWind() {
    return wind;
  }

  public void setWind(DataPoint wind) {
    this.wind = wind;
  }

  public DataPoint getHumidity() {
    return humidity;
  }

  public void setHumidity(DataPoint humidity) {
    this.humidity = humidity;
  }

  public DataPoint getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(DataPoint precipitation) {
    this.precipitation = precipitation;
  }

  public DataPoint getPressure() {
    return pressure;
  }

  public void setPressure(DataPoint pressure) {
    this.pressure = pressure;
  }

  public DataPoint getCloudCover() {
    return cloudCover;
  }

  public void setCloudCover(DataPoint cloudCover) {
    this.cloudCover = cloudCover;
  }

  public void setMeasuredTemperature(DataPoint temperature) {
    if (temperature.getMean() >= -50 && temperature.getMean() < 100) {
      this.temperature = temperature;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public void setMeasuredWind(DataPoint wind) {
    if (wind.getMean() >= 0) {
      this.wind = wind;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public void setMeasuredHumidity(DataPoint humidity) {
    if (humidity.getMean() >= 0 && humidity.getMean() < 100) {
      this.humidity = humidity;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public void setMeasuredPrecipitation(DataPoint precipitation) {
    if (precipitation.getMean() >= 0 && precipitation.getMean() < 100) {
      this.precipitation = precipitation;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public void setMeasuredPressure(DataPoint pressure) {
    if (pressure.getMean() >= 650 && pressure.getMean() < 800) {
      this.pressure = pressure;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public void setMeasuredCloudCover(DataPoint cloudCover) {
    if (cloudCover.getMean() >= 0 && cloudCover.getMean() < 100) {
      this.cloudCover = cloudCover;
      this.setLastUpdateTime(System.currentTimeMillis());
    }
  }

  public long getLastUpdateTime() {
    return this.lastUpdateTime;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  @Override
  public String toString() {
    return "AtmosphericInformation{"
        + "temperature="
        + temperature
        + ", wind="
        + wind
        + ", humidity="
        + humidity
        + ", precipitation="
        + precipitation
        + ", pressure="
        + pressure
        + ", cloudCover="
        + cloudCover
        + ", lastUpdateTime="
        + lastUpdateTime
        + '}';
  }
}
