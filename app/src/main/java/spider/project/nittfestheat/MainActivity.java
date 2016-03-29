package spider.project.nittfestheat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context context;
    SharedPreferences sharedPreferences;
    EditText rollno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        rollno= (EditText) findViewById(R.id.editText);
        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if(!isLocationEnabled(this)){
            Toast.makeText(MainActivity.this, "Enable GPS and Mobile data", Toast.LENGTH_SHORT).show();
        }


        String restoredText = sharedPreferences.getString("RollNo", null);
        if (restoredText != null) {
            Intent intent= new Intent(this,WebViewActivity.class);
            //intent.putExtra("rollNo",rollnotext);
            startActivity(intent);
        }

    }
    public void stopService(View V){
        stopService(new Intent(getBaseContext(),LocService.class));
    }
    public void submit(View V){
        if(!isLocationEnabled(this)){
            Toast.makeText(MainActivity.this, "Enable GPS and Mobile data", Toast.LENGTH_SHORT).show();
            return;
        }
        String rollnotext=rollno.getText().toString();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("RollNo",rollnotext);
        editor.apply();

        Intent intent= new Intent(this,WebViewActivity.class);
        //intent.putExtra("rollNo",rollnotext);
        startActivity(intent);
    }

    public static boolean isLocationEnabled(Context context) {
            int locationMode = 0;
            String locationProviders;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            }else{
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }


        }
    }

