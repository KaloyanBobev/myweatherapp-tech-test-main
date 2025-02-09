package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

  @Autowired
  VisualcrossingRepository weatherRepo;

  public CityInfo forecastByCity(String city) {

    return weatherRepo.getByCity(city);
  }

  /**
   * Calculates the daylight hours for the given city.
   * @param city The name of the city.
   * @return The number of daylight hours.
   */
  public long calculateDaylightHours(CityInfo cityInfo) {
    

if (cityInfo == null || cityInfo.getSunrise() == null || cityInfo.getSunset() == null) {
    throw new IllegalArgumentException("Missing or invalid sunrise/sunset data for city: " + (cityInfo != null ? cityInfo.getCityName() : "Unknown"));
}


    LocalTime sunrise = cityInfo.getSunrise();
    LocalTime sunset = cityInfo.getSunset();

    Duration daylightDuration = Duration.between(sunrise, sunset);
    return daylightDuration.toHours();
  }

  /**
   * Checks if the given city is currently experiencing rain.
   * @param city The name of the city.
   * @return true if it's raining, false otherwise.
   */
  public boolean isRaining(CityInfo cityInfo) {
   

    if (cityInfo == null || cityInfo.getWeather() == null) {
    throw new IllegalArgumentException("Missing or invalid weather data for city: " + (cityInfo != null ? cityInfo.getCityName() : "Unknown"));
}

    // Check for rain in the weather data (you can adjust this based on the actual data structure)
    return cityInfo.getWeather().contains("rain");
  }
}
