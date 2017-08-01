package com.crossover.trial.weather.utils;

import com.google.gson.Gson;
import java.lang.reflect.Type;

/**
 * The implementation of the Gson Factory where we instantiate any Gson Object from a given type
 *
 * @author Victor Polanco
 *
 */
public class GsonFactory {

  public static <T> T getGsonFromJsonString(String jsonString, Type typeOfT) {
    return new Gson().fromJson(jsonString, typeOfT);
  }

  public static String toJson(Object object) {
    return new Gson().toJson(object);
  }

}
