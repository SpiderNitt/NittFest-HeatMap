package spider.project.nittfestheat;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by praba1110 on 28/3/16.
 */
public class LocService extends Service implements LocationListener {

    LocationManager locationManager;
    LocationListener locationListener;
    Context context;
    boolean gps_enabled, network_enabled, thread_stop = false;
    String latitude, longitude;
    Thread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = null;
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        final Location finalLocation = location;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread_stop) {
                    try {
                        //every 30mins sends data
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    onLocationChanged(finalLocation);
                }
            }
        });
        thread.start();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        //new AsyncSendData().execute(latitude, longitude);

        //Toast.makeText(this, "Lat "+latitude+" Long "+longitude,
        //        Toast.LENGTH_SHORT).show();
        Log.d("Loc", "Lat " + latitude + " Long " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(LocService.this, "Service stopped", Toast.LENGTH_SHORT).show();
        thread_stop = true;


    }

    //TODO: Finish the AsyncTask
    public class AsyncSendData extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }
}
