package com.example.axu1.richarddawkinsalarmclock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {


    public String mDeviceId = null;
    String uniqueID;
    public Handler handler;
    String clientId, telephoneNo;
    LayoutInflater layoutInflater;

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {



            layoutInflater = (LayoutInflater)
                    context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.toast_img, null);
            ImageView toastimg = (ImageView)
                    layout.findViewById(R.id.toastImg);
            toastimg.setImageResource(R.drawable.image_w);
            TextView toastmes = (TextView)
                    layout.findViewById(R.id.toastTxt);
                                    /*toastmes.setText("Call to this number>>> \n 会員ＩＤ: "+client_id[i]+ "\n -Mobile : "+telephone[i]);*/
            toastmes.setText("Please call to the following number>>> \n Your ID: "+clientId+"\n Contact Number : \n "+telephoneNo);
            final Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setView(layout);
            toast.show();





            //RETRIVEING MOBILE DATA AND SEND TO SERVER IF NOT EXIST
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

                                    @Override
                                    public void run() {
                                        Log.d("delay: ", "test time");
                                        layoutInflater = (LayoutInflater)
                                                context.getSystemService(LAYOUT_INFLATER_SERVICE);
                                        View layout = layoutInflater.inflate(R.layout.toast_img, null);
                                        ImageView toastimg = (ImageView)
                                                layout.findViewById(R.id.toastImg);
                                        toastimg.setImageResource(R.drawable.image_w);
                                        TextView toastmes = (TextView)
                                                layout.findViewById(R.id.toastTxt);
                                    /*toastmes.setText("Call to this number>>> \n 会員ＩＤ: "+client_id[i]+ "\n -Mobile : "+telephone[i]);*/
                                        toastmes.setText("Please call to the following number>>> \n Your ID: "+clientId+"\n Contact Number : \n "+telephoneNo);
                                        final Toast toast = new Toast(context);
                                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                                        toast.setView(layout);
                                        toast.show();
                                        new CountDownTimer(90, 1000) {

                                            public void onTick(long millisUntilFinished) {
                                                toast.show();
                                            }

                                            public void onFinish() {
                                                toast.show();
                                            }

                                        }.start();
                                        handler.postDelayed(this, 100);
                                    }
                                };

                                handler.postDelayed(runnable, 100);
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
            Toast.makeText(context, "Data Loaded Successfully!", Toast.LENGTH_SHORT).show();

            //return state;


        }
    }

}