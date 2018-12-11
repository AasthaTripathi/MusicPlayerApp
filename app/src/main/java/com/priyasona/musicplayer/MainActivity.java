package com.priyasona.musicplayer;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    static MediaPlayer mp;
    static ListView lv;
    static int songposition;
    static int songid[];
    static String song[];
    static SeekBar sb;
    Thread th;
    Intent intent;
    SensorManager sensorManager;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.lv);
        sb = findViewById(R.id.seekBar);

        song = new String[]{"Teri Meri Dosti", "Barish", "Tu Dua Hai", "Afreen", "Nachde Ne", "Yahin Hoon Main", "Dariya"};
        songid = new int[]{R.raw.song1, R.raw.song2, R.raw.song3, R.raw.song4, R.raw.song5, R.raw.song6, R.raw.song7};

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, song);
        lv.setAdapter(adapter);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (mp != null) {
                        mp.seekTo(progress);
                    } else {
                        sb.setProgress(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mp != null) {
                        sb.setProgress(mp.getCurrentPosition());
                    } else {
                        sb.setProgress(0);
                    }
                }
            }
        };
        th.start();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        intent = new Intent(this, MyService.class);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        float v[] = event.values;
        if (v[0] != 0) {
            intent.putExtra("option", "play");
        } else {
            intent.putExtra("option", "pause");
            startService(intent);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int a) {

    }

    public void play(View v) {
        intent = new Intent(this, MyService.class);
        intent.putExtra("option", "play");
        startService(intent);
    }

    public void pause(View v) {
        if (mp != null)
            mp.pause();
    }


    public void stop(View v) {
        intent = new Intent(this, MyService.class);
        intent.putExtra("option", "stop");
        startService(intent);
    }


    public void next(View v) {
        intent = new Intent(this, MyService.class);
        intent.putExtra("option", "next");
        startService(intent);

    }

    public void prev(View v) {
        intent = new Intent(this, MyService.class);
        intent.putExtra("option", "prev");
        startService(intent);
    }

}
