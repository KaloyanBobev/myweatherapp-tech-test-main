package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

  @GetMapping("/")
  public ResponseEntity<String> home() {
      return ResponseEntity.ok("Welcome to the Weather App!<br>" +
        "Enter in command line your preferences for /forecast/{city} to get weather details.<br>" +
        "You can also compare daylight hours using <a href='/compare-daylight'>compare-daylight</a>.<br>" +
        "Check for rain using <a href='/check-rain'>check-rain</a>.");
  }
  @GetMapping("/forecast/{city}")
  public ResponseEntity<?> forecastByCity(@PathVariable("city") String city) {

   try {
        CityInfo ci = weatherService.forecastByCity(city);
        return ResponseEntity.ok(ci);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(500).body("An internal error occurred.");
    }
  }

 @GetMapping("/compare-daylight")
public ResponseEntity<String> compareDaylightHours(@RequestParam String city1, @RequestParam String city2) {
    logger.info("Received request to compare daylight hours for cities: {} and {}", city1, city2);

    // Validate city names
    if (city1 == null || city2 == null || city1.isBlank() || city2.isBlank()) {
        return ResponseEntity.badRequest().body("City names cannot be empty or null.");
    }

    try {
        logger.info("Fetching weather data for cities {} and {}", city1, city2);
        // Call the weather service for both cities
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        if (cityInfo1 == null || cityInfo2 == null) {
            logger.error("City data not found for one or both cities: {} and {}", city1, city2);
            return ResponseEntity.badRequest().body("One or both city names could not be found.");
        }

        // Calculate daylight hours for both cities
        long daylightHours1 = weatherService.calculateDaylightHours(cityInfo1);
        long daylightHours2 = weatherService.calculateDaylightHours(cityInfo2);

        logger.info("Daylight hours for {}: {} and {}: {}", city1, daylightHours1, city2, daylightHours2);

        // Determine which city has longer daylight hours
        String result = daylightHours1 > daylightHours2 ? city1 : city2;
        return ResponseEntity.ok("The city with the longest daylight hours is: " + result);

    } catch (Exception e) {
        logger.error("An unexpected error occurred while comparing daylight hours", e);
        return ResponseEntity.status(500).body("An internal error occurred. Please try again later.");
    }
}



   @GetMapping("/check-rain")
public ResponseEntity<String> checkRainStatus(@RequestParam String city1, @RequestParam String city2) {
    logger.info("Received request for rain status of cities: {} and {}", city1, city2);

    try {
        // Fetch weather data for both cities
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        // Log the fetched data to ensure it's being received correctly
        logger.info("Fetched weather data for {}: {}", city1, cityInfo1);
        logger.info("Fetched weather data for {}: {}", city2, cityInfo2);

        // Check if both cities have valid weather data
        if (cityInfo1 == null || cityInfo2 == null) {
            logger.error("Weather data not found for one or both cities: {} and {}", city1, city2);
            return ResponseEntity.badRequest().body("One or both city names could not be found.");
        }

        // Check rain status for both cities
        boolean isRainingCity1 = weatherService.isRaining(cityInfo1);
        boolean isRainingCity2 = weatherService.isRaining(cityInfo2);

        // Log the rain status
        logger.info("Rain status for {}: {}", city1, isRainingCity1);
        logger.info("Rain status for {}: {}", city2, isRainingCity2);

        // Determine the result based on the rain status
        if (isRainingCity1 && isRainingCity2) {
            return ResponseEntity.ok("It is currently raining in both cities: " + city1 + " and " + city2);
        } else if (isRainingCity1) {
            return ResponseEntity.ok("It is currently raining in " + city1);
        } else if (isRainingCity2) {
            return ResponseEntity.ok("It is currently raining in " + city2);
        } else {
            return ResponseEntity.ok("It is not raining in either city.");
        }

    } catch (IllegalArgumentException e) {
        logger.error("Invalid arguments for rain check: city1 = {}, city2 = {}", city1, city2, e);
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    } catch (Exception e) {
        logger.error("Unexpected error while checking rain status for cities: {} and {}", city1, city2, e);
        return ResponseEntity.status(500).body("An internal error occurred. Please try again later.");
    }
}


}
