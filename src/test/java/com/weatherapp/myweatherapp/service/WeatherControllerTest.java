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

import java.time.LocalTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    //@Test
    // public void testForecastByCity() throws Exception {
    //     CityInfo cityInfo = new CityInfo("New York", LocalTime.of(6, 0), LocalTime.of(18, 0), "clear");

    //     when(weatherService.forecastByCity("New York")).thenReturn(cityInfo);

    //     mockMvc.perform(get("/forecast/New%20York"))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.cityName").value("New York"))
    //             .andExpect(jsonPath("$.sunrise").value("06:00:00"))
    //             .andExpect(jsonPath("$.sunset").value("18:00:00"))
    //             .andExpect(jsonPath("$.weather").value("clear"));
    // }

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

// @Test
// public void testCompareDaylightHoursInvalidData() throws Exception {
//     when(weatherService.forecastByCity("InvalidCity")).thenReturn(null);

//     mockMvc.perform(get("/compare-daylight?city1=InvalidCity&city2=London"))
//             .andExpect(status().isBadRequest())
//             .andExpect(jsonPath("$").value("Error: Missing or invalid sunrise/sunset data for city: Unknown"));
// }

    // @Test
    // public void testCheckRainStatus() throws Exception {
    //     CityInfo london = new CityInfo("London", LocalTime.of(6, 0), LocalTime.of(18, 0), "rainy");
    //     CityInfo paris = new CityInfo("Paris", LocalTime.of(6, 0), LocalTime.of(18, 0), "clear");

    //     // Mock the service calls to return the city info
    //     when(weatherService.forecastByCity("London")).thenReturn(london);
    //     when(weatherService.forecastByCity("Paris")).thenReturn(paris);

    //     mockMvc.perform(get("/check-rain?city1=London&city2=Paris"))
    //     .andDo(result -> System.out.println("Actual Response: " + result.getResponse().getContentAsString()))
    //     .andExpect(status().isOk())
    //     .andExpect(jsonPath("$").value("It is currently raining in London"));
    // }
}
