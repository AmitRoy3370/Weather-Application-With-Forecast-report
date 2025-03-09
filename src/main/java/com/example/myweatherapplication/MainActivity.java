package com.example.myweatherapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText cityName;
    Button weatherSearch, forecastSearch;
    ListView forecastData;
    TextView weatherData;

    String apiKey = R.string.apiKey + "";

    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cityName = findViewById(R.id.city);
        weatherData = findViewById(R.id.weatherInfo);
        forecastData = findViewById(R.id.weatherForecastInfo);

        weatherSearch = findViewById(R.id.weatherSearch);
        forecastSearch = findViewById(R.id.forecastSearch);

        forecastAdapter = new ForecastAdapter(this, new ArrayList<>());

        forecastData.setAdapter(forecastAdapter);

        weatherSearch.setOnClickListener(this);
        forecastSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.weatherSearch) {

            searchWeather();

        } else if(view.getId() == R.id.forecastSearch) {

            searchForecast();

        }

    }

    private void searchWeather() {

        if(cityName.getText().toString().trim().isEmpty()) {

            Toast.makeText(MainActivity.this, "have to give proper city name", Toast.LENGTH_SHORT).show();

            return;

        }

        new Thread(()-> {

            try {

                String city = cityName.getText().toString().trim();

                String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();

                String line;

                while((line = reader.readLine()) != null) {

                    response.append(line);

                }

                reader.close();

                parseWeatherData(response.toString().trim());

            } catch(Exception e) {

                runOnUiThread(()-> Toast.makeText(MainActivity.this, "Error fetching Data...", Toast.LENGTH_SHORT).show());

            }

        }).start();

    }

    private void searchForecast() {

        if(cityName.getText().toString().trim().isEmpty()) {

            Toast.makeText(MainActivity.this, "have to give proper city name", Toast.LENGTH_SHORT).show();

            return;

        }

        new Thread(()-> {

            try {

                String city = cityName.getText().toString().trim();

                String urlString = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey;

                URL url = new URL(urlString);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();

                String line;

                while((line = reader.readLine()) != null) {

                    response.append(line);

                }

                reader.close();

                parseForecastData(response.toString().trim());

            } catch (Exception e) {

                runOnUiThread(()-> Toast.makeText(MainActivity.this, "fetch to weather data", Toast.LENGTH_SHORT).show());

            }


        }).start();

    }

    private void parseWeatherData(String weatherInformation) {

        try {

            JSONObject jsonObject = new JSONObject(weatherInformation);

            String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = jsonObject.getJSONObject("main").getDouble("temp");

            String weatherInfoText = "Temperature: " + temperature + "°C\nDescription: " + weatherDescription;

            runOnUiThread(()->{
                weatherData.setText(weatherInfoText);
            });

        } catch(Exception e) {

            runOnUiThread(()-> {
                Toast.makeText(MainActivity.this, "failed to process weather data", Toast.LENGTH_SHORT).show();
            });

        }

    }

    private void parseForecastData(String forecastData) {

        try {

            JSONObject jsonObject = new JSONObject(forecastData);

            JSONArray jsonArray = jsonObject.getJSONArray("list");

            List<String> forecastList = new ArrayList<>();

            for(int i = 0; i < jsonArray.length(); i += 8) {

                JSONObject item = jsonArray.getJSONObject(i);
                String date = item.getString("dt_txt");
                double temperature = item.getJSONObject("main").getDouble("temp");
                String weatherDescription = item.getJSONArray("weather").getJSONObject(0).getString("description");

                forecastList.add(date + ": " + temperature + "°C, " + weatherDescription);

            }

            runOnUiThread(() -> {
                forecastAdapter.clear();
                forecastAdapter.addAll(forecastList);
                forecastAdapter.notifyDataSetChanged();
            });

        } catch (Exception e) {

            runOnUiThread(()->{
                Toast.makeText(MainActivity.this, "failed to parse forecast data", Toast.LENGTH_SHORT).show();
            });

        }

    }

}