package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.evosnap.commercedriver.cws.serviceinformation.ChangeUserPasswordResponse;
import com.example.admin.evopay.Tasks.ChangePasswordTask;
import com.example.admin.evopay.Tasks.ChangePasswordTaskCallback;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import timber.log.Timber;

public class ChangePasswordActivity extends AppCompatActivity implements ServiceConnection, ChangePasswordTaskCallback {


    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    //  String newPassword = "f4MGs59G";
    String newPassword = "g5NHt68H";
    String activity_name = "ChangePasswordActivity";
    private ServiceBinder service;
    private ProgressDialog dialog;

    public static Intent newIntent(Context context, String username, String password) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        intent.putExtra(USERNAME, username);
        intent.putExtra(PASSWORD, password);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        startService(new Intent(ChangePasswordActivity.this, CommerceDriverService.class));
        bindService(new Intent(ChangePasswordActivity.this, CommerceDriverService.class), ChangePasswordActivity.this, BIND_AUTO_CREATE);


    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;
        onChangePasswordClicked();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(ChangePasswordActivity.this);
    }

    public void showProgressDialog() {
        hideProgressDialog();
        ProgressDialog dialog = new ProgressDialog(ChangePasswordActivity.this);
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

    private void onChangePasswordClicked() {
        showProgressDialog();

        Intent intent = getIntent();
        String username = intent.getStringExtra(USERNAME);
        String password = intent.getStringExtra(PASSWORD);

        ChangePasswordTask task = new ChangePasswordTask(service, ChangePasswordActivity.this, username, password, newPassword);
        task.execute();
    }


    @Override
    public void onChangePasswordTaskResult(ChangeUserPasswordResponse response) {
        hideProgressDialog();

        Timber.tag("TenderPos").v("onChangePasswordTaskResult: " + " " + response);

        Log.d("ChangePasswordResult:", String.valueOf(response));

        Snackbar.make(findViewById(android.R.id.content), "password has been changed successfully", Snackbar.LENGTH_LONG).show();

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onChangePasswordError(Exception exception) {
        hideProgressDialog();
        Timber.tag("TenderPos").v("onChangePasswordTaskResult: " + " " + exception.getMessage());

        Snackbar.make(findViewById(android.R.id.content), exception.toString(), Snackbar.LENGTH_LONG).show();
    }
}

