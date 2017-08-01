package com.crossover.trial.weather;

import com.crossover.trial.weather.controller.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.controller.RestWeatherQueryEndpoint;
import com.crossover.trial.weather.controller.WeatherCollectorEndpoint;
import com.crossover.trial.weather.controller.WeatherQueryEndpoint;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

  private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();

  private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

  private Gson _gson = new Gson();

  private DataPoint _dp;

  @Before
  public void setUp() throws Exception {
    RestWeatherCollectorEndpoint.init();
    _dp =
        new DataPoint.Builder()
            .withCount(10)
            .withFirst(10)
            .withMedian(20)
            .withLast(30)
            .withMean(22)
            .build();
    _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
    _query.weather("BOS", "0").getEntity();
  }

  @Test
  public void testPing() throws Exception {
    String ping = _query.ping();
    JsonElement pingResult = new JsonParser().parse(ping);
    assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
    assertEquals(
        5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
  }

  @Test
  public void testGet() throws Exception {
    List<AtmosphericInformation> ais =
        (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
    System.out.println(ais.get(0));
    assertEquals(ais.get(0).getWind(), _dp);
  }

  /**
   * Test NearBy
   *
   * <p>Acceptance criteria: The service should return the atmospheric information for all airports
   * near by a given airport code and radius.
   *
   * <p>Request definition: <host_name>:<port>/query/{iataCode}/{radius}
   *
   * <p>This should be 3 since the airports in a radius of 200km of JFK are:
   *
   * <p>MMU - LGA - EWR
   */
  @Test
  public void testGetNearby() throws Exception {
    // check datasize response
    _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
    _dp.setMean(40);
    _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
    _dp.setMean(30);
    _update.updateWeather("LGA", "wind", _gson.toJson(_dp));

    List<AtmosphericInformation> ais =
        (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
    assertEquals(3, ais.size());
  }

  /**
   * Test Update
   *
   * <p>Acceptance criteria: The service should return the atmospheric information for all airports
   * near by a given airport code and radius.
   *
   * <p>MMU - LGA - EWR
   */
  @Test
  public void testUpdate() throws Exception {

    DataPoint windDp =
        new DataPoint.Builder()
            .withCount(10)
            .withFirst(10)
            .withMedian(20)
            .withLast(30)
            .withMean(22)
            .build();
    _update.updateWeather("BOS", "wind", _gson.toJson(windDp));
    _query.weather("BOS", "0").getEntity();

    String ping = _query.ping();
    JsonElement pingResult = new JsonParser().parse(ping);
    System.out.println(ping);
    // The data size should be 4 instead of 1 [assertEquals(1, pingResult)]
    assertEquals(4, pingResult.getAsJsonObject().get("datasize").getAsInt());

    DataPoint cloudCoverDp =
        new DataPoint.Builder()
            .withCount(4)
            .withFirst(10)
            .withMedian(60)
            .withLast(100)
            .withMean(50)
            .build();
    _update.updateWeather("BOS", "cloudcover", _gson.toJson(cloudCoverDp));

    List<AtmosphericInformation> ais =
        (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
    assertEquals(ais.get(0).getWind(), windDp);
    assertEquals(ais.get(0).getCloudCover(), cloudCoverDp);
  }
}
