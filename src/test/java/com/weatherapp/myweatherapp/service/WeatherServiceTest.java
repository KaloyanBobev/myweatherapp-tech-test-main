package com.weatherapp.myweatherapp.service;

import static org.junit.jupiter.api.Assertions.*;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.repository.VisualcrossingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class WeatherServiceTest {

    @Mock
    VisualcrossingRepository weatherRepo;

    @InjectMocks
    WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateDaylightHours() {
        CityInfo cityInfo = new CityInfo("New York", LocalTime.of(6, 0), LocalTime.of(18, 0), "clear");
        
        // Mock the repository call
        when(weatherRepo.getByCity("New York")).thenReturn(cityInfo);

        long daylightHours = weatherService.calculateDaylightHours(cityInfo);

        assertEquals(12, daylightHours);
    }

    @Test
    public void testIsRaining() {
        CityInfo cityInfo = new CityInfo("London", LocalTime.of(6, 0), LocalTime.of(18, 0), "rainy");

        // Mock the repository call
        when(weatherRepo.getByCity("London")).thenReturn(cityInfo);

        boolean isRaining = weatherService.isRaining(cityInfo);

        assertTrue(isRaining);
    }

    @Test
    public void testIsNotRaining() {
        CityInfo cityInfo = new CityInfo("Paris", LocalTime.of(6, 0), LocalTime.of(18, 0), "clear");

        // Mock the repository call
        when(weatherRepo.getByCity("Paris")).thenReturn(cityInfo);

        boolean isRaining = weatherService.isRaining(cityInfo);

        assertFalse(isRaining);
    }
}