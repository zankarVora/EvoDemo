package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.evosnap.commercedriver.cws.ApiError;
import com.evosnap.commercedriver.cws.enumerations.UndoReason;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardUndo;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.Tasks.UndoTransactionTask;
import com.example.admin.evopay.Tasks.UndoTransactionTaskCallback;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import timber.log.Timber;

public class UndoActivity extends AppCompatActivity implements ServiceConnection, UndoTransactionTaskCallback {

    public static final String EXTRA_UNDO_RESULT = "undo";
    private static final int REQUEST_CODE_TRANSACTION_ACTIVITY = 1;
    private static final String TRANSACTION_ID = "Transaction_id";
    String activity_name = " UndoActivity";
    private ServiceBinder service;
    private ProgressDialog dialog;
    private Context mContext = this;
    private final ExampleLogger logger = new ExampleLogger("UndoActivity");

    public static Intent newIntent(Context context) {
        return new Intent(context, UndoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo);

        startService(new Intent(UndoActivity.this, CommerceDriverService.class));
        bindService(new Intent(UndoActivity.this, CommerceDriverService.class), UndoActivity.this, BIND_AUTO_CREATE);

    }

    public void showProgressDialog() {
        hideProgressDialog();
        ProgressDialog dialog = new ProgressDialog(UndoActivity.this);
        dialog.setIndeterminate(true);
        dialog.setTitle(R.string.please_wait);
        dialog.setCancelable(false);
        dialog.show();
        this.dialog = dialog;

    }

//    public static Intent newIntent(Context context, String username) {
//        Intent intent = new Intent(context, UndoActivity.class);
//        intent.putExtra(TRANSACTION_ID, username);
//        return intent;
//    }

    public void hideProgressDialog() {
        if (this.dialog != null) {
            this.dialog.dismiss();
        }
        this.dialog = null;
    }

    private void onUndoTransClicked() {

        try {

            showProgressDialog();
//            Intent intent = getIntent();
            String transaction_id = Static_Constants.transaction_itemses.get(0).getTransactionId();


            Timber.tag("TenderPos").v("in onUndoTransClicked: " + " " + "transaction_id:" + transaction_id + " " + activity_name + " \n");
            BankCardUndo bankCardUndo = new BankCardUndo();
            bankCardUndo.setTransactionId(transaction_id);
            bankCardUndo.setUndoReason(UndoReason.CustomerCancellation);

            UndoTransactionTask undoTask = new UndoTransactionTask(service, UndoActivity.this, bankCardUndo);
            undoTask.execute();

        } catch (Exception e) {
            hideProgressDialog();
            Log.d("catch in onUndoTrans:", e.getMessage());
            Toast.makeText(this, "catch in onUndoTransClicked:" + e.getMessage(), Toast.LENGTH_LONG).show();

            Timber.tag("TenderPos").v("catch in onUndoTransClicked: " + " " + e.toString() + " " + activity_name + " \n");

        }

    }

    @Override
    public void onUndoTransactionResponse(BankCardTransactionResponse bankCardTransactionResponse) {
        hideProgressDialog();

        try {
            Timber.tag("TenderPos").v("in onUndoTransResponse: " + " " + bankCardTransactionResponse + " " + activity_name + " \n");


            Log.d("onUndoTransResponse:", String.valueOf(bankCardTransactionResponse));
            Toast.makeText(mContext, "undo successful", Toast.LENGTH_SHORT).show();


            Intent data = new Intent();
            data.putExtra(EXTRA_UNDO_RESULT, bankCardTransactionResponse);
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception e) {
            Log.d("catch in onUndoTransRes", e.getMessage());

            Toast.makeText(this, "catch in onUndoTransRes:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onUndoTransactionError(SnapApiError error) {
        hideProgressDialog();
        ApiError response = error.getErrorResponse();
        Log.d("onUndoTransResponse:", String.valueOf(response));
        logger.log("onUndoTransactionError SnapApiError"  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onUndoTransactionError SnapApiError"  +error.getCause() + " " + activity_name + " \n");


        String msg = "error reason:" + response.getReason() + "\nerror msg:" + response.getMessages();
        Toast.makeText(mContext, "undo error: " + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUndoTransactionError(SnapSessionError error) {
        hideProgressDialog();

        Log.d("onUndoTransResponse:", String.valueOf(error.getMessage()));
        Log.d("onUndoTransResponse:", String.valueOf(error));
        logger.log("onUndoTransactionError SnapSessionError"  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onUndoTransactionError SnapSessionError"  +error.getCause() + " " + activity_name + " \n");

        String msg = "Failure reason:" + error.getCause() + "\nFailure msg:" + error.getMessage();
        Toast.makeText(mContext, "undo fail: " + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUndoTransactionError(SnapConnectionError error) {
        hideProgressDialog();

        Log.d("onUndoTransResponse:", String.valueOf(error.getMessage()));
        Log.d("onUndoTransResponse:", String.valueOf(error));
        logger.log("onUndoTransactionError SnapConnectionError"  +error.getMessage() + " " + activity_name + " \n");
        logger.log("onUndoTransactionError SnapConnectionError"  +error.getCause() + " " + activity_name + " \n");
        String msg = "Failure reason:" + error.getCause() + "\nFailure msg:" + error.getMessage();
        Toast.makeText(mContext, "undo fail: " + msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;
        onUndoTransClicked();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(UndoActivity.this);
    }
}
