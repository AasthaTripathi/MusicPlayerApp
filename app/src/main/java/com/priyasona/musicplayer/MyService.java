package com.priyasona.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import static com.priyasona.musicplayer.MainActivity.lv;
import static com.priyasona.musicplayer.MainActivity.mp;
import static com.priyasona.musicplayer.MainActivity.sb;
import static com.priyasona.musicplayer.MainActivity.song;
import static com.priyasona.musicplayer.MainActivity.songid;
import static com.priyasona.musicplayer.MainActivity.songposition;

public class MyService extends Service implements MediaPlayer.OnCompletionListener {
    NotificationManager nm;
    Notification notification;
    Notification.Builder nb;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myStop();
                songposition = position;
                myPlay(position);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String option = intent.getStringExtra("option");
        switch (option) {
            case "play":
                myPlay(songposition);
                break;
            case "stop":
                myStop();
                break;
            case "next":
                myNext();
                break;
            case "prev":
                myPrevious();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }


    private void myPlay(int songposition) {
        if (mp == null) {
            mp = MediaPlayer.create(this, songid[songposition]);
            mp.setOnCompletionListener(this);
            sb.setMax(mp.getDuration());
            mp.start();
            myNotification();
        } else if (!mp.isPlaying()) {
            mp.start();
        }
    }

    private void myStop() {
        if (mp != null) {
            mp.stop();
            mp = null;
        }
    }

    private void myNext() {
        if (songposition == songid.length - 1) {
            songposition = 0;
        } else {
            songposition++;
        }
        myStop();
        myPlay(songposition);
    }

    private void myPrevious() {
        if (songposition == 0) {
            songposition = songid.length - 1;
        } else {
            songposition--;
        }
        myStop();
        myPlay(songposition);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        myNext();
    }

    public void myNotification() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nb = new Notification.Builder(this);
        nb.setContentText("" + song[songposition]);
        nb.setSubText("My Music");
        nb.setDefaults(Notification.DEFAULT_ALL);
        nb.setSmallIcon(R.mipmap.ic_launcher);
        nb.setTicker("Ticker Text");
        Intent i = new Intent(this, Main2Activity.class);
        Intent i1 = new Intent(this, MyService.class);
        i1.putExtra("option", "prev");
        Intent i2 = new Intent(this, MyService.class);
        i2.putExtra("option", "pause");

        Intent i3 = new Intent(this, MyService.class);
        i3.putExtra("option", "next");
        PendingIntent pi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), i, 0);
        PendingIntent pi1 = PendingIntent.getService(this, 1, i1, 0);
        PendingIntent pi2 = PendingIntent.getService(this, 2, i2, 0);
        PendingIntent pi3 = PendingIntent.getService(this, 3, i3, 0);
        nb.setContentIntent(pi);
        nb.setAutoCancel(true);
        nb.addAction(android.R.drawable.ic_media_previous, "Previous", pi1);
        nb.addAction(android.R.drawable.ic_media_pause, "Pause", pi2);
        nb.addAction(android.R.drawable.ic_media_next, "Next", pi3);
        notification = nb.build();
        nm.notify(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
