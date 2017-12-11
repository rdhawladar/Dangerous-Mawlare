package com.example.axu1.richarddawkinsalarmclock;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Excel on 12/5/17.
 */

public class Common extends AppCompatActivity {
    Common cm = new Common();
    public void start(){

        Toast.makeText(getApplicationContext(), "from common start class", Toast.LENGTH_SHORT).show();
        return;
    }
}
