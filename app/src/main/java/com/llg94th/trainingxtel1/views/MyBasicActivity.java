package com.llg94th.trainingxtel1.views;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


public class MyBasicActivity extends AppCompatActivity {
    public final String MY_TAG= "MY_TAG";

    public void log(String msg){
        Log.d(MY_TAG,msg);
    }
    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
