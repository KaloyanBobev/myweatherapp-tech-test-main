package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for handling weather-related requests.
 * Provides endpoints for retrieving weather forecasts, comparing daylight hours, and checking rain status.
 */
@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    /**
     * Home endpoint that provides basic information about the API.
     *
     * @return A welcome message with links to available features.
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the Weather App!<br>" +
                "Enter in command line your preferences for /forecast/{cityName} to get weather details.<br>" +
                "You can also compare daylight hours using <a href='/compare-daylight'>compare-daylight</a>.<br>" +
                "Check for rain using <a href='/check-rain'>check-rain</a>.");
    }

    /**
     * Fetches the weather forecast for a given city.
     *
     * @param cityName The name of the city.
     * @return A response containing the city's weather information or an error message.
     */
    @GetMapping("/forecast/{cityName}")
    public ResponseEntity<?> getWeatherForecast(@PathVariable("cityName") String cityName) {
        try {
            CityInfo cityWeatherInfo = weatherService.forecastByCity(cityName);
            return ResponseEntity.ok(cityWeatherInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An internal error occurred.");
        }
    }

    /**
     * Compares the daylight hours between two cities and returns the city with the longest daylight hours.
     *
     * @param firstCity The name of the first city.
     * @param secondCity The name of the second city.
     * @return A response indicating which city has the longest daylight hours.
     */
    @GetMapping("/compare-daylight")
    public ResponseEntity<String> compareDaylightHours(@RequestParam String firstCity, @RequestParam String secondCity) {
        logger.info("Received request to compare daylight hours for cities: {} and {}", firstCity, secondCity);

        // Validate city names
        if (firstCity == null || secondCity == null || firstCity.isBlank() || secondCity.isBlank()) {
            return ResponseEntity.badRequest().body("City names cannot be empty or null.");
        }

        try {
            logger.info("Fetching weather data for cities {} and {}", firstCity, secondCity);
            // Call the weather service for both cities
            CityInfo firstCityInfo = weatherService.forecastByCity(firstCity);
            CityInfo secondCityInfo = weatherService.forecastByCity(secondCity);

            if (firstCityInfo == null || secondCityInfo == null) {
                logger.error("City data not found for one or both cities: {} and {}", firstCity, secondCity);
                return ResponseEntity.badRequest().body("One or both city names could not be found.");
            }

            // Calculate daylight hours for both cities
            long firstCityDaylightHours = weatherService.calculateDaylightHours(firstCityInfo);
            long secondCityDaylightHours = weatherService.calculateDaylightHours(secondCityInfo);

            logger.info("Daylight hours for {}: {} and {}: {}", firstCity, firstCityDaylightHours, secondCity, secondCityDaylightHours);

            // Determine which city has longer daylight hours
            String cityWithLongestDay = firstCityDaylightHours > secondCityDaylightHours ? firstCity : secondCity;
            return ResponseEntity.ok("The city with the longest daylight hours is: " + cityWithLongestDay);

        } catch (Exception e) {
            logger.error("An unexpected error occurred while comparing daylight hours", e);
            return ResponseEntity.status(500).body("An internal error occurred. Please try again later.");
        }
    }

    /**
     * Checks which of the two given cities is currently experiencing rain.
     *
     * @param firstCity The name of the first city.
     * @param secondCity The name of the second city.
     * @return A response indicating which city (or both) is experiencing rain.
     */
    @GetMapping("/check-rain")
    public ResponseEntity<String> checkRainStatus(@RequestParam String firstCity, @RequestParam String secondCity) {
        logger.info("Received request for rain status of cities: {} and {}", firstCity, secondCity);

        try {
            // Fetch weather data for both cities
            CityInfo firstCityWeather = weatherService.forecastByCity(firstCity);
            CityInfo secondCityWeather = weatherService.forecastByCity(secondCity);

            // Log the fetched data to ensure it's being received correctly
            logger.info("Fetched weather data for {}: {}", firstCity, firstCityWeather);
            logger.info("Fetched weather data for {}: {}", secondCity, secondCityWeather);

            // Check if both cities have valid weather data
            if (firstCityWeather == null || secondCityWeather == null) {
                logger.error("Weather data not found for one or both cities: {} and {}", firstCity, secondCity);
                return ResponseEntity.badRequest().body("One or both city names could not be found.");
            }

            // Check rain status for both cities
            boolean isFirstCityRaining = weatherService.isRaining(firstCityWeather);
            boolean isSecondCityRaining = weatherService.isRaining(secondCityWeather);

            // Log the rain status
            logger.info("Rain status for {}: {}", firstCity, isFirstCityRaining);
            logger.info("Rain status for {}: {}", secondCity, isSecondCityRaining);

            // Determine the result based on the rain status
            if (isFirstCityRaining && isSecondCityRaining) {
                return ResponseEntity.ok("It is currently raining in both cities: " + firstCity + " and " + secondCity);
            } else if (isFirstCityRaining) {
                return ResponseEntity.ok("It is currently raining in " + firstCity);
            } else if (isSecondCityRaining) {
                return ResponseEntity.ok("It is currently raining in " + secondCity);
            } else {
                return ResponseEntity.ok("It is not raining in either city.");
            }

        } catch (IllegalArgumentException e) {
            logger.error("Invalid arguments for rain check: firstCity = {}, secondCity = {}", firstCity, secondCity, e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while checking rain status for cities: {} and {}", firstCity, secondCity, e);
            return ResponseEntity.status(500).body("An internal error occurred. Please try again later.");
        }
    }
}
