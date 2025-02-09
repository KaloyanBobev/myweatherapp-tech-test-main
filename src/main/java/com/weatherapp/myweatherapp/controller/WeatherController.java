package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/")
  public ResponseEntity<String> home() {
      return ResponseEntity.ok("Welcome to the Weather App! Enter in command line your perfferences for /forecast/{city} to get weather details.");
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
    try {
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        long daylightHours1 = weatherService.calculateDaylightHours(cityInfo1);
        long daylightHours2 = weatherService.calculateDaylightHours(cityInfo2);

        String result = daylightHours1 > daylightHours2 ? city1 : city2;
        return ResponseEntity.ok("The city with the longest daylight hours is: " + result);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(500).body("An internal error occurred.");
    }
}

   @GetMapping("/check-rain")
   public ResponseEntity<String> checkRainStatus(@RequestParam String city1, @RequestParam String city2) {
    try {
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        boolean isRainingCity1 = weatherService.isRaining(cityInfo1);
        boolean isRainingCity2 = weatherService.isRaining(cityInfo2);

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
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(500).body("An internal error occurred.");
    }
}

}
