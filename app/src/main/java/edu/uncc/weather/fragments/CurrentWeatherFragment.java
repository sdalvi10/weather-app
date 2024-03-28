package edu.uncc.weather.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.weather.BuildConfig;
import edu.uncc.weather.models.DataService;
import edu.uncc.weather.models.Weather;
import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * Homework 06
 * CurrentWeatherFragment.java
 * Authors: 1) Sudhanshu Dalvi, 2) Pradip Nemane
 * */

public class CurrentWeatherFragment extends Fragment {
    Weather mWeather;
    private DataService.City mCity;
    FragmentCurrentWeatherBinding binding;
    OkHttpClient client = new OkHttpClient();
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    public static final String API_KEY = BuildConfig.API_KEY;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
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
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");

        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder()
                .addQueryParameter("lat", String.valueOf(mCity.getLat()))
                .addQueryParameter("lon", String.valueOf(mCity.getLon()))
                .addQueryParameter("appid", API_KEY)
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
                    try {

                        mWeather = parseJson(response);
                        getActivity().runOnUiThread(()->{
                            binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());
                            binding.textViewTemp.setText(String.valueOf(mWeather.getTemp()) + " F");
                            binding.textViewTempMax.setText(String.valueOf(mWeather.getTempMax()) + " F");
                            binding.textViewTempMin.setText(String.valueOf(mWeather.getTempMin()) + " F");
                            binding.textViewDesc.setText(WordUtils.capitalize(mWeather.getDescription()));
                            binding.textViewHumidity.setText(String.valueOf(mWeather.getHumidity()) + "%");
                            binding.textViewWindSpeed.setText(String.valueOf(mWeather.getWindSpeed()) + " miles/hr");
                            binding.textViewWindDegree.setText(String.valueOf(mWeather.getWindDegree()) + " degrees");
                            binding.textViewCloudiness.setText(String.valueOf(mWeather.getCloudiness()) + "%");
                            String imageUrl = "https://openweathermap.org/img/wn/" + mWeather.getIcon() + "@2x.png";
                            Picasso.get().load(imageUrl).into(binding.imageViewWeatherIcon);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.buttonCheckForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoWeatherForecastFragment(mCity);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CurrentWeatherFragmentListener) {
            mListener = (CurrentWeatherFragmentListener) context;
        } else {
            throw new RuntimeException(context + "must implement CurrentWeatherFragmentListener");
        }
    }

    CurrentWeatherFragmentListener mListener;
    public interface CurrentWeatherFragmentListener {
        void gotoWeatherForecastFragment(DataService.City city);
    }

    Weather parseJson(Response response) throws IOException, JSONException {
        Weather weather = new Weather();
        JSONObject jsonObject = new JSONObject(response.body().string());
        JSONArray jsonArray = jsonObject.getJSONArray("weather");
        JSONObject weatherJsonObject = jsonArray.getJSONObject(0);
        weather.setDescription(weatherJsonObject.getString("description"));
        weather.setIcon(weatherJsonObject.getString("icon"));

        JSONObject mainJsonObject = jsonObject.getJSONObject("main");
        weather.setTemp(mainJsonObject.getDouble("temp"));
        weather.setTempMax(mainJsonObject.getDouble("temp_max"));
        weather.setTempMin(mainJsonObject.getDouble("temp_min"));
        weather.setHumidity(mainJsonObject.getInt("humidity"));

        JSONObject windJsonObject = jsonObject.getJSONObject("wind");
        weather.setWindSpeed(windJsonObject.getDouble("speed"));
        weather.setWindDegree(windJsonObject.getDouble("deg"));

        JSONObject cloudsJsonObject = jsonObject.getJSONObject("clouds");
        weather.setCloudiness(cloudsJsonObject.getDouble("all"));

        return weather;
    }
}