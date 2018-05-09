package com.example.admin.evopay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    private final ExampleLogger logger = new ExampleLogger("SplashScreen");
    String activity_name="SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

       // initializeSSLContext(getApplicationContext());
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            logger.log("catch in ProviderInstaller "  +e.getMessage() + " " + activity_name + " \n");

        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            logger.log("catch in ProviderInstaller "  +e.getMessage() + " " + activity_name + " \n");

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);


            }
        }, SPLASH_TIME_OUT);
    }

//    public static void initializeSSLContext(Context mContext){
//        try {
//            SSLContext.getInstance("TLSv1.1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }

}
