package com.example.axu1.rdhawladar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pending_intent;

    private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    public String mDeviceId = null;
    String uniqueID;
    public Handler handler;
    String clientId, telephoneNo;
    LayoutInflater layoutInflater;

    private AlarmReceiver alarm;


    MainActivity inst;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this.context = this;


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();
        String phone = "+8801671822671";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone));
        startActivity(callIntent);



        TelephonyManager tMgr = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);


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
        mDeviceId = tMgr.getDeviceId();
        uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String androidID = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        String number = tMgr.getLine1Number();



        Log.d("test11", androidID);
        Log.d("test11", deviceID);
        Log.d("test Mobile no: ", number);


        //fetchingData();



        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        if(!connected){
            handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout = layoutInflater.inflate(R.layout.toast_img, null);
                    ImageView toastimg = layout.findViewById(R.id.toastImg);
                    toastimg.setImageResource(R.drawable.image_w);
                    TextView toastmes = layout.findViewById(R.id.toastTxt);
                                    /*toastmes.setText("Call to this number>>> \n 会員ＩＤ: "+client_id[i]+ "\n -Mobile : "+telephone[i]);*/
                    toastmes.setText("Please connect to the internet : " );
                    final Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setView(layout);
                    toast.show();
                    new CountDownTimer(90, 1000)
                    {

                        public void onTick(long millisUntilFinished) {toast.show();}
                        public void onFinish() {toast.show();}

                    }.start();

                    handler.postDelayed(this, 100);
                }
            };

            handler.postDelayed(runnable, 100);

        }

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
        myURL += mDeviceId;

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
                        Log.d("boolead: ", Integer.valueOf(status[i]).toString());

                        clientId = client_id[i];
                        telephoneNo = telephone[i];

                        if( Integer.valueOf(status[i]).equals(0)) {
                            handler = new Handler();
                            Runnable runnable = new Runnable() {

                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void run() {

                                    layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                    View layout = layoutInflater.inflate(R.layout.toast_img, null);
                                    ImageView toastimg = layout.findViewById(R.id.toastImg);
                                    toastimg.setImageResource(R.drawable.image_w);
                                    TextView toastmes = layout.findViewById(R.id.toastTxt);
                                    //toastmes.setText("Call to this number>>> \n 会員ＩＤ: "+client_id[i]+ "\n -Mobile : "+telephone[i]);
                                    toastmes.setText("Please call to the following number>>> \n Your ID: "+clientId+"\n Contact Number : \n "+telephoneNo);
                                    final Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    toast.setView(layout);
                                    toast.show();
                                    new CountDownTimer(2, 100000) {

                                        public void onTick(long millisUntilFinished) {
                                            toast.show();
                                        }

                                        public void onFinish() {
                                            toast.show();
                                        }

                                    }.start();
                                    handler.postDelayed(this, 10);
                                }
                            };

                            handler.postDelayed(runnable, 10);
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



        com.example.axu1.rdhawladar.AppController.getInstance().addToRequestQueue(jsonArrayRequest);
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
