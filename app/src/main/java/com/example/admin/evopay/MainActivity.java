package com.example.admin.evopay;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.terminal.TransactionResult;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    Button pay_process,returnn,undo;
    private ServiceBinder service;
    private static final int REQUEST_CODE_Init_TRANSACTION_ACTIVITY = 1;
    private static final int REQUEST_CODE_Return_TRANSACTION_ACTIVITY = 2;
    private static final int REQUEST_CODE_Undo_TRANSACTION_ACTIVITY = 3;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, CommerceDriverService.class));
        bindService(new Intent(MainActivity.this, CommerceDriverService.class), MainActivity.this, BIND_AUTO_CREATE);


        Init();
        checkAndRequestPermissions();
    }

    private boolean checkAndRequestPermissions() {

        int permissionREAD_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);


        int storagePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int BLUETOOTH = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH);

        int BLUETOOTH_ADMIN = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionREAD_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (BLUETOOTH != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }

        if (BLUETOOTH_ADMIN != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }

        return true;
    }


    void Init()
    {
        pay_process=(Button)findViewById(R.id.pay);
        pay_process.setOnClickListener(this);

        returnn=(Button)findViewById(R.id.returnn);
        returnn.setOnClickListener(this);
        undo=(Button)findViewById(R.id.undo);
        undo.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if (view == pay_process)
        {

            try {
                if (!service.isLoggedIn()) {

                    startActivity(LoginActivity.newIntent(MainActivity.this));
                } else {
                    if (!service.hasTerminal())  {
                        startActivity(TerminalActivity.newIntent(MainActivity.this));
                    } else {
                        //onStartTransactionClicked();
                        startActivityForResult(TransactionActivity.newIntent(MainActivity.this), REQUEST_CODE_Init_TRANSACTION_ACTIVITY);

                    }
                }
            }
            catch (Exception e)
            {
                Timber.tag("TenderPos").v("in catch While clicking on pay button: " + e.getMessage());

            }
        }
        else if (view == returnn)
        {
            if (!service.isLoggedIn()) {
                startActivity(LoginActivity.newIntent(MainActivity.this));
            }
            else
            {
                if (!service.hasTerminal())
                {
                    startActivity(TerminalActivity.newIntent(MainActivity.this));
                }
                else {
                    startActivityForResult(TransactionReturnActivity.newIntent(MainActivity.this), REQUEST_CODE_Return_TRANSACTION_ACTIVITY);

                }
            }
        }
        else if (view == undo)
        {
            if (!service.isLoggedIn()) {
                startActivity(LoginActivity.newIntent(MainActivity.this));
            }
            else
            {
                if (!service.hasTerminal())
                {
                    startActivity(TerminalActivity.newIntent(MainActivity.this));
                }
                else {
                    startActivityForResult(UndoActivity.newIntent(MainActivity.this), REQUEST_CODE_Undo_TRANSACTION_ACTIVITY);

                }
            }
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    private void onStartTransactionClicked() {

        try {
            if (service.hasTerminal()) {
                startActivityForResult(TransactionActivity.newIntent(MainActivity.this), REQUEST_CODE_Init_TRANSACTION_ACTIVITY);
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_message_please_add_terminal, Snackbar.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in catch onStartTransactionClicked: " + e.getMessage());

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_Init_TRANSACTION_ACTIVITY == requestCode) {
            onHandleTransactionActivityResult(resultCode, data);
        }

        else if (REQUEST_CODE_Return_TRANSACTION_ACTIVITY == requestCode) {
            onHandleReturnActivityResult(resultCode, data);
        }

        else if (REQUEST_CODE_Undo_TRANSACTION_ACTIVITY == requestCode) {
            onHandleUndoActivityResult(resultCode, data);
        }


    }

    private void onHandleTransactionActivityResult(int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {

                TransactionResult result = (TransactionResult) data.getSerializableExtra(TransactionActivity.EXTRA_TRANSACTION_RESULT);
                Snackbar.make(findViewById(android.R.id.content), String.valueOf(result), Snackbar.LENGTH_SHORT).show();

            } else {

                Snackbar.make(findViewById(android.R.id.content), "Transaction Cancelled", Snackbar.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in catch onHandleTransactionActivityResult: " + e.getMessage());

        }

    }

    private void onHandleUndoActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            BankCardTransactionResponse result = (BankCardTransactionResponse) data.getSerializableExtra(UndoActivity.EXTRA_UNDO_RESULT);
//            Static_Constants.transaction_itemses.clear();
            Snackbar.make(findViewById(android.R.id.content), result.toString()+ " for undo", Snackbar.LENGTH_SHORT).show();

        } else {

            Snackbar.make(findViewById(android.R.id.content), "Undo Cancelled", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void onHandleReturnActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            BankCardTransactionResponse result = (BankCardTransactionResponse) data.getSerializableExtra(TransactionReturnActivity.EXTRA_RETURN_RESULT);
            Static_Constants.transaction_itemses.clear();
            Snackbar.make(findViewById(android.R.id.content), result.toString()+ " for return", Snackbar.LENGTH_SHORT).show();
        } else {

            Snackbar.make(findViewById(android.R.id.content), "Return Cancelled", Snackbar.LENGTH_SHORT).show();
        }
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//      //  finish();
//    }

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
        unbindService(MainActivity.this);
    }

}
