package com.crossover.trial.weather.utils;

import com.google.gson.Gson;
import java.lang.reflect.Type;

/**
 * Created by Luxorv on 7/30/17.
 */
public class GsonFactory {

  public static <T> T getGsonFromJsonString(String jsonString, Type typeOfT) {
    return new Gson().fromJson(jsonString, typeOfT);
  }

}
