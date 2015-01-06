package com.michaelnares.weatherstation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import dalvik.annotation.TestTarget;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherStationActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private SensorManager sensorManager;
    private TextView temperatureTextView, pressureTextView, lightTextView;

    private float currentTemperature = Float.NaN;
    private float currentPressure = Float.NaN;
    private float currentLight = Float.NaN;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        temperatureTextView = (TextView) findViewById(R.id.temperature);
        pressureTextView = (TextView)findViewById(R.id.pressure);
        lightTextView = (TextView)findViewById(R.id.light);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("weatherUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
            updateGUI();
            }
        }, 0, 1000);
    } // ends onCreate() method

    @Override
    public void onResume()
    {
        super.onResume();

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null)
        {
            sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        else lightTextView.setText("Light sensor unavailable");

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor != null)
        {
            sensorManager.registerListener(pressureSensorEventListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        else pressureTextView.setText("Barometer unavailable");

        Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        if (temperatureSensor != null)
        {
            sensorManager.registerListener(tempSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        else temperatureTextView.setText("Thermometer unavailable");
    } // onResume() ends here

    @Override
    public void onPause()
    {
        sensorManager.unregisterListener(tempSensorEventListener);
        sensorManager.unregisterListener(pressureSensorEventListener);
        sensorManager.unregisterListener(lightSensorEventListener);
        super.onPause();
    }

    private void updateGUI()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
            if (!Float.isNaN(currentPressure))
            {
                pressureTextView.setText(currentPressure + "hPa");
                pressureTextView.invalidate();
            }

            if (!Float.isNaN(currentLight))
            {
                String lightStr = "Sunny";
                if (currentLight <= SensorManager.LIGHT_CLOUDY) {
                    lightStr = "Night";
                } else if (currentLight <= SensorManager.LIGHT_OVERCAST) {
                    lightStr = "Cloudy";
                } else if (currentLight <= SensorManager.LIGHT_SUNLIGHT) {
                    lightStr = "Overcast";
                }
                lightTextView.setText(lightStr);
                lightTextView.invalidate();
            } // ends if block for current light
            if (!Float.isNaN(currentTemperature))
            {
                temperatureTextView.setText(currentTemperature + "C");
                temperatureTextView.invalidate();
            }
        } // ends run() method
        });
    }

    private final SensorEventListener tempSensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
        currentTemperature = sensorEvent.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };

    private final SensorEventListener pressureSensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            currentPressure = sensorEvent.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };

    private final SensorEventListener lightSensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            currentLight = sensorEvent.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }
    };
}
