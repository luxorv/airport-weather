package com.crossover.trial.weather.utils;

import com.crossover.trial.weather.controller.RestWeatherQueryEndpoint;
import com.crossover.trial.weather.service.AtmosphericInformationServiceImpl;
import javax.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Created by Luxorv on 7/29/17.
 */
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(RestWeatherQueryEndpoint.class).in(Singleton.class);;
    bind(AtmosphericInformationServiceImpl.class).in(Singleton.class);;
  }
}
