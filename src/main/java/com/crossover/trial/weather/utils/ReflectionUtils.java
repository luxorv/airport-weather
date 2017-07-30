package com.crossover.trial.weather.utils;

import com.crossover.trial.weather.model.DataPoint;
import java.lang.reflect.Method;

/**
 * The implementation of the Reflection Utils where we set make dynamic operations on objects
 *
 * @author Victor Polanco
 *
 */
public class ReflectionUtils {

  /**
   * Given an iataCode find the airport data
   *
   * @param object as a string
   * @param propertyName as a string
   *
   * @return {@link Method} reference of the object
   */
  public static Method getMethodFromInstance(Object object, String propertyName)
      throws NoSuchMethodException {
    propertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    String methodName = "setMessured" + propertyName;

    return object.getClass().getMethod(methodName, DataPoint.class);

  }

}
