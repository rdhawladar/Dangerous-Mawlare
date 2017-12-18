package com.example.axu1.rdhawladar;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by RD Hawladar on 12/5/17.
 */

public class Common extends AppCompatActivity {
    Common cm = new Common();

    public void start() {

        Toast.makeText(getApplicationContext(), "from common start class", Toast.LENGTH_SHORT).show();
        return;
    }
}
