package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.weatherapp.databinding.ActivityMainBinding;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
  private ApiInterface weatherApi;
  private static ActivityMainBinding binding;

  private ActivityMainBinding getBinding() {
    if (binding == null) {
      binding = ActivityMainBinding.inflate(getLayoutInflater());
    }
    return binding;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getBinding().getRoot());

    weatherApi = Objects.requireNonNull(RetrofitClient.getInstance()).create(ApiInterface.class);
    fetchWeatherData("bhopal");
    SearchCity();
  }

  private void SearchCity() {
    SearchView search = binding.searchCity;
    search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        fetchWeatherData(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        // Handle search query text change (optional)
        return true;
      }
    });
  }

  private void fetchWeatherData(String city) {
    Call<WeatherApp> call = weatherApi.getWeatherData(city, "c4796d806180ceb42d4b73253d899284", "metric");
    if (call != null) {
      call.enqueue(new Callback<WeatherApp>() {
        @Override
        public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
          Log.d("TAG3", "Inside onResponse");
          if (response.isSuccessful()) {
            Log.d("TAG4", "Inside isSuccessfull");
            WeatherApp weatherResponse = response.body();

            if (weatherResponse != null) {
              Double temp = weatherResponse.getMain().getTemp();
              double maxTemp = weatherResponse.getMain().getTempMax();
              double minTemp = weatherResponse.getMain().getTempMin();
              String condition = weatherResponse.getWeather().get(0).getMain();
              long humidity = weatherResponse.getMain().getHumidity();
              double windSpeed = weatherResponse.getWind().getSpeed();
              long sunrise = weatherResponse.getSys().getSunrise();
              long sunset = weatherResponse.getSys().getSunset();
              long sea = weatherResponse.getMain().getSeaLevel();

              binding.temperature.setText(temp.toString());
              binding.maxTemp.setText("Max Temp : " + maxTemp + "℃");
              binding.minTemp.setText("Min Temp : " + minTemp + "℃");
              binding.condition.setText(condition);
              binding.conditionText.setText(condition);
              binding.humidity.setText(humidity + "%");
              binding.windSpeed.setText(windSpeed + " m/s");
              binding.sunrise.setText(extractTime(sunrise));
              binding.sunset.setText(extractTime(sunset));
              binding.sea.setText(sea + " hPa");
              binding.cityName.setText(city);

              Log.d("TAG", condition);
              changeWeatherAppInterface(condition);
            }
          } else {
            // Handle API error
            System.out.println("API Error: " + response.message());
          }
        }

        @Override
        public void onFailure(Call<WeatherApp> call, Throwable t) {
          // Handle network error
          System.out.println("Network Error: " + t.getMessage());
        }
      });
    } else {
      // Handle the case where the call is null
      System.out.println("Call object is null");
    }
  }

  public static void changeWeatherAppInterface(String condition) {
    switch (condition) {
      case "Clear Sky":
      case "Sunny":
      case "Clear": {
        binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
        binding.lottieAnimationView.setAnimation(R.raw.sun);
        break;
      }

      case "Partly Clouds":
      case "Clouds":
      case "Overcast":
      case "Mist":
      case "Foggy": {
        binding.getRoot().setBackgroundResource(R.drawable.cloud_background);
        binding.lottieAnimationView.setAnimation(R.raw.cloud);
        break;
      }
      case "Light Rain":
      case "Drizzle":
      case "Moderate Rain":
      case "Showers":
      case "Heavy Rain": {
        binding.getRoot().setBackgroundResource(R.drawable.rain_background);
        binding.lottieAnimationView.setAnimation(R.raw.rain);
        break;
      }
      case "Light Snow":
      case "Moderate Snow":
      case "Heavy Snow":
      case "Blizzard": {
        binding.getRoot().setBackgroundResource(R.drawable.snow_background);
        binding.lottieAnimationView.setAnimation(R.raw.snow);
        break;
      }
    }
    binding.lottieAnimationView.playAnimation();
  }

  public static String getHumanReadableDateTime(long ts) {
    if (String.valueOf(ts).length() == 10) ts = ts * 1000;

    Timestamp tms = new Timestamp(ts);

    //Getting the calendar class instance
    Calendar calendar = Calendar.getInstance();

    // Passing the long value to calendar class function
    calendar.setTimeInMillis(tms.getTime());

    //printing the time using getTime() function
    return calendar.getTime().toString();
  }

  public static String extractDate(long timestamp) {
    if (String.valueOf(timestamp).length() == 10) timestamp = timestamp * 1000;

    Timestamp tms = new Timestamp(timestamp);

    Date date = new Date(tms.getTime());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return dateFormat.format(date);
  }

  public static String extractTime(long timestamp) {
    if (String.valueOf(timestamp).length() == 10) timestamp = timestamp * 1000;

    Timestamp tms = new Timestamp(timestamp);

    Date date = new Date(tms.getTime());
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    return timeFormat.format(date);
  }
}