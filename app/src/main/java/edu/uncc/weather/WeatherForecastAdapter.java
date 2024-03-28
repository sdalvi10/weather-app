package edu.uncc.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;

import java.util.List;

import edu.uncc.weather.models.Forecast;

/*
 * Homework 06
 * WeatherForecastAdapter.java
 * Authors: 1) Sudhanshu Dalvi, 2) Pradip Nemane
 * */

public class WeatherForecastAdapter extends ArrayAdapter<Forecast> {
    public WeatherForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_row_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewDateTime = convertView.findViewById(R.id.textViewDateTime);
            viewHolder.textViewTemp = convertView.findViewById(R.id.textViewTemp);
            viewHolder.textViewTempMax = convertView.findViewById(R.id.textViewTempMax);
            viewHolder.textViewTempMin = convertView.findViewById(R.id.textViewTempMin);
            viewHolder.textViewDesc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.textViewHumidity = convertView.findViewById(R.id.textViewHumidity);
            viewHolder.imageViewWeatherIcon = convertView.findViewById(R.id.imageViewWeatherIcon);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Forecast forecast = getItem(position);
        viewHolder.textViewDateTime.setText(forecast.getDate());
        viewHolder.textViewTemp.setText(String.valueOf(forecast.getTemp()) + " F");
        viewHolder.textViewTempMax.setText("Max: " + String.valueOf(forecast.getTempMax()) + " F");
        viewHolder.textViewTempMin.setText("Min: " + String.valueOf(forecast.getTempMin()) + " F");
        viewHolder.textViewDesc.setText(WordUtils.capitalize(forecast.getDescription()));
        viewHolder.textViewHumidity.setText(String.valueOf("Humidity: " + forecast.getHumidity()) + "%");
        String imageUrl = "https://openweathermap.org/img/wn/" + forecast.getIcon() + "@2x.png";
        Picasso.get().load(imageUrl).into(viewHolder.imageViewWeatherIcon);
        return convertView;
    }

    private class ViewHolder {
        TextView textViewDateTime;
        TextView textViewTemp;
        TextView textViewTempMax;
        TextView textViewTempMin;
        TextView textViewHumidity;
        TextView textViewDesc;
        ImageView imageViewWeatherIcon;
    }
}
