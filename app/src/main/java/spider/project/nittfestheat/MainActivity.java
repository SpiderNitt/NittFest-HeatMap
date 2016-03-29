package spider.project.nittfestheat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    EditText rollno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollno= (EditText) findViewById(R.id.editText);
        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


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
        String rollnotext=rollno.getText().toString();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("RollNo",rollnotext);
        editor.apply();

        Intent intent= new Intent(this,WebViewActivity.class);
        //intent.putExtra("rollNo",rollnotext);
        startActivity(intent);
    }
}
