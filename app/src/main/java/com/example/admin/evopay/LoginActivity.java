package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.evosnap.commercedriver.cws.ApiError;
import com.evosnap.commercedriver.cws.serviceinformation.SignOnWithUserNameAndPasswordResponse;
import com.evosnap.commercedriver.cws.serviceinformation.security.SecurityQuestion;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.evosnap.commercedriver.webservice.SnapSyncAccountError;
import com.example.admin.evopay.Tasks.Login;
import com.example.admin.evopay.Tasks.LoginTaskCallback;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.List;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements ServiceConnection, LoginTaskCallback {

    private static final int REQUEST_CODE_PASSWORD_CHANGE = 3;
    String Username;
    String Password, Amount;
    String activity_name = " LoginActivity";
    List<SecurityQuestion> questions;
    private ServiceBinder service;
    private ProgressDialog dialog;
    private Context mContext = this;
    private final ExampleLogger logger = new ExampleLogger("LoginActivity");


    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        startService(new Intent(LoginActivity.this, CommerceDriverService.class));
        bindService(new Intent(LoginActivity.this, CommerceDriverService.class), LoginActivity.this, BIND_AUTO_CREATE);

        onLoginClicked();

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(LoginActivity.this);
    }

    public void onLoginClicked() {
        try {

            View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_login_details, null);
            final EditText Username1 = (EditText) view.findViewById(R.id.EditText_Username1);
            final EditText Pwd = (EditText) view.findViewById(R.id.EditText_Pwd);

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Username = Username1.getText().toString().trim();
                    Password = Pwd.getText().toString().trim();
                   // showProgressDialog();

                    Login task = new Login(service, LoginActivity.this, Username, Password);
                    task.execute();

                    dialog.dismiss();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Login");
            builder.setView(view);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, onClickListener);
            builder.create().show();

            Timber.tag("TenderPos").v("Calling login method");


        } catch (Exception e) {

            Timber.tag("TenderPos").v("catch in login: " + e.getMessage());

            Log.d("catch in onloginclick", e.getMessage());
            Toast.makeText(mContext, "in onLoginClicked:" + e.getMessage(), Toast.LENGTH_LONG).show();
            hideProgressDialog();
        }
    }

    @Override
    public void onLoginTaskResult(SignOnWithUserNameAndPasswordResponse response, String username, String password) {
        hideProgressDialog();
        // Static_Constants.EvoLogin = "Yes";
        try {
            if (service.isLoggedIn()) {
                Timber.tag("TenderPos").v(" onLoginTaskResult " + "isLoggedIn:" + service.isLoggedIn() + " \n");
                Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                //Static_Constants.EvoLogin = "Yes";
                Intent intent = AccountSecurityActivity.newIntent(LoginActivity.this, username, password);
                startActivity(intent);
                finish();

            } else {
               // Static_Constants.EvoLogin = "No";

                Toast.makeText(LoginActivity.this, " not logged in", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
            {
                Timber.tag("TenderPos").v("in catch onLoginTaskResult: " + e.getMessage());

            }

//        Log.d("response:::", String.valueOf(response));
//
//        Timber.tag("TenderPos").v("onLoginTaskResult received response:"+response);



    }

    @Override
    public void onLoginError(SnapApiError error) {


        try {
            Timber.tag("TenderPos").v("onLoginTaskResult error:" + error);
            hideProgressDialog();
            ApiError response = error.getErrorResponse();
           // Static_Constants.EvoLogin = "No";
            Snackbar.make(findViewById(android.R.id.content), response.getReason(), Snackbar.LENGTH_SHORT).show();

            if (response.getErrorId() == 5001 || response.getErrorId() == 5004) {
                Log.d("services", String.valueOf(service));

                Intent intent = ChangePasswordActivity.newIntent(getApplicationContext(), Username, Password);
                startActivityForResult(intent, REQUEST_CODE_PASSWORD_CHANGE);
            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in catch onLoginError: " + e.getMessage());

        }

    }

    @Override
    public void onLoginError(SnapSessionError error) {
        hideProgressDialog();
        Timber.tag("TenderPos").v("onLoginTaskResult error:"+error);

       // Static_Constants.EvoLogin = "No";
        Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
        Snackbar.make(findViewById(android.R.id.content), error.getCause().toString(), Snackbar.LENGTH_LONG).show();
        Toast.makeText(mContext,  error.getMessage(), Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext,  error.getCause().toString(), Toast.LENGTH_SHORT).show();
        logger.log("onLoginError SnapSessionError getMessage: "  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onLoginError SnapSessionError getCause: "  +error.getCause().toString() + " " + activity_name + " \n");

        finish();


    }

    @Override
    public void onLoginError(SnapSyncAccountError error) {
        hideProgressDialog();
        Timber.tag("TenderPos").v("onLoginTaskResult error:"+error);

       // Static_Constants.EvoLogin = "No";
        Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
        Snackbar.make(findViewById(android.R.id.content), error.getCause().toString(), Snackbar.LENGTH_LONG).show();
        Toast.makeText(mContext,  error.getMessage(), Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext,  error.getCause().toString(), Toast.LENGTH_SHORT).show();

        logger.log("onLoginError SnapSyncAccountError getMessage: "  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onLoginError SnapSyncAccountError getCause: "  +error.getCause().toString() + " " + activity_name + " \n");
        finish();

    }

    @Override
    public void onLoginError(SnapConnectionError error) {
        hideProgressDialog();
        Timber.tag("TenderPos").v("onLoginTaskResult error:"+error);

        //Static_Constants.EvoLogin = "No";
        Snackbar.make(findViewById(android.R.id.content), error.getMessage(), Snackbar.LENGTH_LONG).show();
        Snackbar.make(findViewById(android.R.id.content), error.getCause().toString(), Snackbar.LENGTH_LONG).show();
        Toast.makeText(mContext,  error.getMessage(), Toast.LENGTH_SHORT).show();
        Toast.makeText(mContext,  error.getCause().toString(), Toast.LENGTH_SHORT).show();
        logger.log("onLoginError SnapConnectionError getMessage: "  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onLoginError SnapConnectionError getCause: "  +error.getCause().toString() + " " + activity_name + " \n");
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PASSWORD_CHANGE) {
            handlePasswordChangeActivityResult(resultCode, data);
        }
    }

    private void handlePasswordChangeActivityResult(int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_message_change_password_success, Snackbar.LENGTH_SHORT).show();
                onLoginClicked();
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_message_change_password_cancelled, Snackbar.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in catch handlePasswordChangeActivityResult: " + e.getMessage());

        }

    }

    public void showProgressDialog() {
        hideProgressDialog();
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setIndeterminate(true);
        dialog.setTitle(R.string.please_wait);
        dialog.setCancelable(false);
        dialog.show();
        this.dialog = dialog;

    }

    public void hideProgressDialog() {
        if (this.dialog != null) {
            this.dialog.dismiss();
        }
        this.dialog = null;
    }

}


