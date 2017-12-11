package com.example.axu1.richarddawkinsalarmclock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    public String mDeviceId = null;
    String uniqueID;
    UUID deviceId;
    String sub_id;
    String sim_serial;
    String line_num;
    String main_num;

    private AlarmReceiver alarm;


    MainActivity inst;
    Context context;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RETRIVEING MOBILE DATA AND SEND TO SERVER IF NOT EXIST
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        if (null != tMgr) mDeviceId = tMgr.getDeviceId();

        if (null == mDeviceId || 0 == mDeviceId.length())
            mDeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
       // mDeviceId = tMgr.getDeviceId();
       // mDeviceId = UUID.randomUUID();
        if(null == mDeviceId)
            mDeviceId = UUID.randomUUID().toString();

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        deviceId = new UUID(androidId.hashCode(), ((long) mDeviceId.hashCode() << 32));

        uniqueID = UUID.randomUUID().toString();

        sub_id = tMgr.getSubscriberId();
        sim_serial = tMgr.getSimSerialNumber();
        line_num = tMgr.getLine1Number();
        main_num = tMgr.getVoiceMailNumber();








        this.context = this;

        //alarm = new AlarmReceiver();
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        final Intent myIntent = new Intent(this.context, AlarmReceiver.class);

        // Get the alarm manager service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // set the alarm to the time that you picked
        final Calendar calendar = Calendar.getInstance();

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);



        Button start_alarm= (Button) findViewById(R.id.start_alarm);
        start_alarm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)

            @Override
            public void onClick(View v) {

                calendar.add(Calendar.SECOND, 3);
                //setAlarmText("You clicked a button");

                final int hour = alarmTimePicker.getCurrentHour();
                final int minute = alarmTimePicker.getCurrentMinute();;

                Log.e("MyActivity", "In the receiver with " + hour + " and " + minute);
                setAlarmText("You clicked a " + hour + " and " + minute);


                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

                myIntent.putExtra("extra", "yes");
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);


                // now you should change the set Alarm text so it says something nice


                setAlarmText("Alarm set to " + hour + ":" + minute);
                //Toast.makeText(getApplicationContext(), "You set the alarm", Toast.LENGTH_SHORT).show();
            }

        });

        Button stop_alarm= (Button) findViewById(R.id.stop_alarm);
        stop_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int min = 1;
                int max = 9;

                Random r = new Random();
                int random_number = r.nextInt(max - min + 1) + min;
                Log.e("random number is ", String.valueOf(random_number));

                myIntent.putExtra("extra", "no");
                sendBroadcast(myIntent);

                alarmManager.cancel(pending_intent);
                setAlarmText("Alarm canceled");
                //setAlarmText("You clicked a " + " canceled");
            }
        });

    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }



    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "start application", Toast.LENGTH_SHORT).show();
        fetchingData();

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(false){
            LayoutInflater layoutInflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.toast_img, null);
            ImageView toastimg = (ImageView)
                    layout.findViewById(R.id.toastImg);
            toastimg.setImageResource(R.drawable.image_w);
            TextView toastmes = (TextView)
                    layout.findViewById(R.id.toastTxt);
            toastmes.setText("COnntect to the internet fast... ");
            final Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();
            new CountDownTimer(500, 50000)
            {

                public void onTick(long millisUntilFinished) {toast.show();}
                public void onFinish() {toast.show();}

            }.start();

        }
        Toast.makeText(getApplicationContext(), String.valueOf(connected).toString(), Toast.LENGTH_SHORT).show();

        inst = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(), "Destroy!", Toast.LENGTH_SHORT).show();
    }


    //FUNCTION TO FETCH DATA FROM THE SERVER
    public void fetchingData(){



        String myURL = "http://fh4c2dv5yu.com/android_get.php?udid=";
        myURL += deviceId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(myURL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                final String[] device_id = new String[response.length()];
                final String[] status = new String[response.length()];
                final String[] client_id = new String[response.length()];
                final String[] telephone = new String[response.length()];
                final String[] telset1 = new String[response.length()];

                for (int i =0; i < response.length(); i++){

                    try {

                        JSONObject jsonObject = (JSONObject) response.get(i);
                        device_id[i] = jsonObject.getString("ud_id");
                        status[i] = jsonObject.getString("flag_status");
                        client_id[i] = jsonObject.getString("client_id");
                        client_id[i] = client_id[i].substring(2,client_id[i].length());
                        telephone[i] = jsonObject.getString("telephone");
                        telset1[i] = jsonObject.getString("telset1");

                        if(device_id[i].equals(String.valueOf(mDeviceId).toString())) {
                            Log.d("status: ", String.valueOf(status[i]));
                            if(status[i].equals(String.valueOf(0).toString())){
                                for (int j=0; j < 10; j++) {
                                    LayoutInflater layoutInflater = (LayoutInflater)
                                            getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View layout = layoutInflater.inflate(R.layout.toast_img, null);
                                    ImageView toastimg = (ImageView)
                                            layout.findViewById(R.id.toastImg);
                                    toastimg.setImageResource(R.drawable.image_w);
                                    TextView toastmes = (TextView)
                                            layout.findViewById(R.id.toastTxt);
                                    toastmes.setText("Call to this number>>> \n 会員ＩＤ: "+client_id[i]+ "\n -Mobile : "+telephone[i]);
           /*                         toastmes.setText("Message"+j+"\n 会員ＩＤ: "+client_id[i]+ "\n  -Mobile : "+telephone[i] + "\n -devid ID: "+ mDeviceId +
                                            "\n -Unique ID: "+uniqueID+ "\n -subscriber ID: "+sub_id+"\n -sim srial ID: "+sim_serial+"\n -main number:"+main_num+"\n -line number:"
                                            +line_num);*/
                                    final Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    toast.setView(layout);
                                    toast.show();
                                    new CountDownTimer(500, 50000)
                                    {

                                        public void onTick(long millisUntilFinished) {toast.show();}
                                        public void onFinish() {toast.show();}

                                    }.start();
                                }

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Volley Log", error);

            }
        });



        com.example.axu1.richarddawkinsalarmclock.AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        Toast.makeText(getApplicationContext(), "Data Loaded Successfully!", Toast.LENGTH_SHORT).show();

        //return state;
    }
    //END FUNCTION TO FETCH DATA FROM THE SERVER



    //disbaled touch effect
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        return true;//consume
    }

    // disabled backpressed
    @Override
    public void onBackPressed() {
        // dont call **super**, if u want disable back button in current screen.
    }


}
