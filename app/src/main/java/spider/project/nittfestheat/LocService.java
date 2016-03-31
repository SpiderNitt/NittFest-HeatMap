package spider.project.nittfestheat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by praba1110 on 28/3/16.
 */
public class LocService extends Service implements LocationListener {


    String BASE_URL="https://spider.nitt.edu/heat/add";
    SharedPreferences pref;
    LocationManager locationManager;
    LocationListener locationListener;
    Context context;
    boolean gps_enabled, network_enabled, thread_stop = false;
    String latitude, longitude,rollNo;
    Thread thread;
    int lastsavedseconds;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        rollNo=pref.getString("RollNo",null);

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
/*
        Intent stopServiceIntent=new Intent(this, stopService.class);
        stopServiceIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 1, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
*/
        Intent intent2 = new Intent(this, WebViewActivity.class);

        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);


        Notification noti = null;

            noti = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("NITTFest Heat")
                    .setContentText("May the odds be ever in your dept's favor")
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
 //                   .addAction(android.R.drawable.ic_delete,"Stop service",pendingIntent2)
                    .setContentIntent(pendingIntent)
                    .build();


        startForeground(1234, noti);



        return START_NOT_STICKY;

    }



    String provider;
    Location location;
    @Override
    public void onCreate() {
        super.onCreate();

        pref=getApplicationContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!thread_stop) {

                    provider=LocationManager.GPS_PROVIDER;
                    locationManager.requestLocationUpdates(provider, 5000, 0,LocService.this,Looper.getMainLooper());

                    location=locationManager.getLastKnownLocation(provider);
                    //SystemClock.sleep(60000);
                    //every 120secs sends data
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                        if(location!=null)
                        {

                            onLocationChanged(location);
                            Log.d("lat","notnull");
                        }
                        else{
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0,LocService.this,Looper.getMainLooper());
                            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            onLocationChanged(location);
                            Log.d("lat","null");
                        }



                    Log.d("Loc", "Lat " + latitude + " Long " + longitude);
                    new AsyncSendData().execute(latitude,longitude,rollNo);

                    }

                }

        });
        thread.start();



    }


    @Override
    public void onLocationChanged(Location location) {

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());



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

        Toast.makeText(LocService.this, "Service stopped", Toast.LENGTH_SHORT).show();
        thread_stop = true;
        locationManager.removeUpdates(LocService.this);
        super.onDestroy();
    }

    //TODO: Finish the AsyncTask
    public class AsyncSendData extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          //  locationManager.requestLocationUpdates(provider, 5000, 0,LocService.this, Looper.getMainLooper());
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String result = "";
            try {
                String link = BASE_URL;
                String latitude = params[0];
                String longitude = params[1];
                String rollno = params[2];

                URL URL=new URL(link);
                HttpURLConnection urlConnection= (HttpURLConnection) URL.openConnection();
                String postParameters="lat="+latitude+"&long="+longitude
                        +"&rollno="+rollno;
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
                InputStream inputStream=urlConnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while((line=bufferedReader.readLine())!=null){
                    Log.d("line",line);
                    result+=line;
                }
                Log.d("DATA",result);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            locationManager.removeUpdates(LocService.this);
        }
    }
}
