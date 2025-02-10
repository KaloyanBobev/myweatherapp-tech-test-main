package com.weatherapp.myweatherapp.service;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalTime;

@SpringBootTest
public class WeatherControllerTest {

    @Mock
    WeatherService weatherService;

    @InjectMocks
    WeatherController weatherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    /**
     * Test for comparing daylight hours between two cities.
     */
    @Test
    public void testCompareDaylightHours() throws Exception {
        CityInfo cityInfo1 = new CityInfo("New York", LocalTime.of(6, 0), LocalTime.of(18, 0), "clear");
        CityInfo cityInfo2 = new CityInfo("London", LocalTime.of(7, 0), LocalTime.of(19, 0), "clear");

        when(weatherService.forecastByCity("New York")).thenReturn(cityInfo1);
        when(weatherService.forecastByCity("London")).thenReturn(cityInfo2);

        mockMvc.perform(get("/compare-daylight?city1=New York&city2=London"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("The city with the longest daylight hours is: London"));
    }

    /**
     * Test for comparing daylight hours when both cities have the same daylight duration.
     */
    @Test
    public void testCompareDaylightHoursEqualDuration() throws Exception {
        CityInfo cityInfo1 = new CityInfo("Tokyo", LocalTime.of(6, 30), LocalTime.of(18, 30), "clear");
        CityInfo cityInfo2 = new CityInfo("Sydney", LocalTime.of(7, 0), LocalTime.of(19, 0), "clear");

        when(weatherService.forecastByCity("Tokyo")).thenReturn(cityInfo1);
        when(weatherService.forecastByCity("Sydney")).thenReturn(cityInfo2);

        mockMvc.perform(get("/compare-daylight?city1=Tokyo&city2=Sydney"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Both cities have the same daylight duration."));
    }

    /**
     * Test for checking which city is experiencing rain.
     */
    @Test
    public void testRainCheck() throws Exception {
        CityInfo cityInfo1 = new CityInfo("Paris", LocalTime.of(6, 30), LocalTime.of(18, 30), "rainy");
        CityInfo cityInfo2 = new CityInfo("Berlin", LocalTime.of(7, 0), LocalTime.of(19, 0), "clear");

        when(weatherService.forecastByCity("Paris")).thenReturn(cityInfo1);
        when(weatherService.forecastByCity("Berlin")).thenReturn(cityInfo2);

        mockMvc.perform(get("/rain-check?city1=Paris&city2=Berlin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Paris is currently experiencing rain."));
    }

    /**
     * Test when both cities are experiencing rain.
     */
    @Test
    public void testRainCheckBothCitiesRaining() throws Exception {
        CityInfo cityInfo1 = new CityInfo("Seattle", LocalTime.of(6, 30), LocalTime.of(18, 30), "rainy");
        CityInfo cityInfo2 = new CityInfo("Vancouver", LocalTime.of(7, 0), LocalTime.of(19, 0), "rainy");

        when(weatherService.forecastByCity("Seattle")).thenReturn(cityInfo1);
        when(weatherService.forecastByCity("Vancouver")).thenReturn(cityInfo2);

        mockMvc.perform(get("/rain-check?city1=Seattle&city2=Vancouver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Both cities are experiencing rain."));
    }

    /**
     * Test when neither city is experiencing rain.
     */
    @Test
    public void testRainCheckNoRain() throws Exception {
        CityInfo cityInfo1 = new CityInfo("Madrid", LocalTime.of(6, 30), LocalTime.of(18, 30), "clear");
        CityInfo cityInfo2 = new CityInfo("Rome", LocalTime.of(7, 0), LocalTime.of(19, 0), "cloudy");

        when(weatherService.forecastByCity("Madrid")).thenReturn(cityInfo1);
        when(weatherService.forecastByCity("Rome")).thenReturn(cityInfo2);

        mockMvc.perform(get("/rain-check?city1=Madrid&city2=Rome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Neither city is currently experiencing rain."));
    }

    /**
     * Test for invalid city input (city not found).
     */
    // @Test
    // public void testCityNotFound() throws Exception {
    //     doThrow(new CityNotFoundException("City not found")).when(weatherService).forecastByCity("Atlantis");

    //     mockMvc.perform(get("/compare-daylight?city1=Atlantis&city2=London"))
    //             .andExpect(status().isNotFound())
    //             .andExpect(jsonPath("$.message").value("City not found"));
    // }

    /**
     * Test when request is missing parameters.
     */
    @Test
    public void testMissingQueryParameters() throws Exception {
        mockMvc.perform(get("/compare-daylight?city1=New York"))
                .andExpect(status().isBadRequest());
    }
}
