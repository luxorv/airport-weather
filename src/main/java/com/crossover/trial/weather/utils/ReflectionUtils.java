package com.crossover.trial.weather.utils;

import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The implementation of the Reflection Utils where we set make dynamic operations on objects
 *
 * @author Victor Polanco
 *
 */
public class ReflectionUtils {

  /**
   * Get the method reference of a given object to avoid setting the property explicitly.
   *
   * @param object as a string
   * @param propertyName as a string
   *
   * @return {@link Method} reference of the object
   */
  public static Method getMethodFromInstance(Object object, String propertyName)
      throws NoSuchMethodException {
    // Get the Titled cased of the propertyName
    propertyName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
    String methodName = "setMeasured" + propertyName;

    return object.getClass().getMethod(methodName, DataPoint.class);
  }

  /**
   * Invoke the setMessured<property_name> of a given data point to set the property
   *
   * @param atmosphericInfo the atmospheric information of the given airport
   * @param propertyName as a string
   * @param dataPoint the data point object to be set
   *
   * @return {@link Method} reference of the object
   */
  public static void setPropertyToAtmosphericInfo(
      AtmosphericInformation atmosphericInfo, String propertyName, DataPoint dataPoint)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    // Have a special use case for cloud cover since there's no delimiters to get a right property name.
    if (propertyName.equalsIgnoreCase(DataPointType.CLOUDCOVER.name())) {
      atmosphericInfo.setMeasuredCloudCover(dataPoint);
    } else {
      // Get the method reference for the given data point
      Method method = ReflectionUtils.getMethodFromInstance(atmosphericInfo, propertyName);
      // Invoke the method for the data point
      method.invoke(atmosphericInfo, dataPoint);
    }
  }

}
