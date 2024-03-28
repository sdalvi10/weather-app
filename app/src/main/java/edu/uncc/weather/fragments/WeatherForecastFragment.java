package edu.uncc.weather.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.weather.models.DataService;
import edu.uncc.weather.models.Forecast;
import edu.uncc.weather.R;
import edu.uncc.weather.WeatherForecastAdapter;
import edu.uncc.weather.databinding.FragmentWeatherForecastBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Homework 06
 * WeatherForecastFragment.java
 * Authors: 1) Sudhanshu Dalvi, 2) Pradip Nemane
 * */

public class WeatherForecastFragment extends Fragment {
    private DataService.City mCity;
    WeatherForecastAdapter adapter;
    FragmentWeatherForecastBinding binding;
    OkHttpClient client = new OkHttpClient();
    ArrayList<Forecast> mForecasts = new ArrayList<>();
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    public static WeatherForecastFragment newInstance(DataService.City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Weather Forecast");

        binding.textViewCityName.setText(mCity.getCity());
        adapter = new WeatherForecastAdapter(getContext(), R.layout.forecast_row_item, mForecasts);
        binding.listView.setAdapter(adapter);

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("lat", String.valueOf(mCity.getLat()))
                .addQueryParameter("lon", String.valueOf(mCity.getLon()))
                .addQueryParameter("appid", CurrentWeatherFragment.API_KEY)
                .addQueryParameter("units", "imperial")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    mForecasts.clear();
                    try {
                        mForecasts.addAll(parseJson(response));
                        getActivity().runOnUiThread(()->{
                            adapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    ArrayList<Forecast> parseJson(Response response) throws JSONException, IOException {
        ArrayList<Forecast> forecasts = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response.body().string());
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            Forecast forecast = new Forecast();
            JSONObject listJsonObject = jsonArray.getJSONObject(i);
            JSONObject mainJsonObject = listJsonObject.getJSONObject("main");
            forecast.setTemp(mainJsonObject.getDouble("temp"));
            forecast.setTempMax(mainJsonObject.getDouble("temp_max"));
            forecast.setTempMin(mainJsonObject.getDouble("temp_min"));
            forecast.setHumidity(mainJsonObject.getInt("humidity"));

            JSONArray weatherJsonArray = listJsonObject.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherJsonArray.getJSONObject(0);
            forecast.setDescription(weatherJsonObject.getString("description"));
            forecast.setIcon(weatherJsonObject.getString("icon"));

            forecast.setDate(listJsonObject.getString("dt_txt"));
            forecasts.add(forecast);
        }
        return forecasts;
    }
}