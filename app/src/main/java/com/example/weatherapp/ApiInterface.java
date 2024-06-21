package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
  @GET("weather")
  Call<WeatherApp> getWeatherData(
    @Query("q") String city,
    @Query("appId") String apiKey,
    @Query("units") String units
  );
}
