package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.evosnap.commercedriver.cws.ApiError;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardReturnPro;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.Tasks.ReturnByIdCallback;
import com.example.admin.evopay.Tasks.ReturnByIdTask;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.Date;

import timber.log.Timber;

public class TransactionReturnActivity extends AppCompatActivity implements ServiceConnection, ReturnByIdCallback {

    public static final String EXTRA_RETURN_RESULT = "return";
    ProgressDialog dialog;
    String activity_name = " TransactionReturnActivity";
    private Context mContext = this;
    private ServiceBinder service;

    public static Intent newIntent(Context context)
    {
        return new Intent(context, TransactionReturnActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_return);

        startService(new Intent(TransactionReturnActivity.this, CommerceDriverService.class));
        bindService(new Intent(TransactionReturnActivity.this, CommerceDriverService.class), TransactionReturnActivity.this, BIND_AUTO_CREATE);

    }

    public void onReturnTransClicked() {
        try {
            int size = Static_Constants.transaction_itemses.size();
            if (size > 0) {


                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_simple_transaction_amount_entry, null);
                final EditText editText = (EditText) view.findViewById(R.id.transaction_amount);
                editText.setHint("Enter amount for return transaction");
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {

                            try {
                                showProgressDialog();

                                String amountString = editText.getText().toString();
                                double amount = TextUtils.isEmpty(amountString) ? 0.00 : Double.valueOf(amountString);



                                String transaction_id = Static_Constants.transaction_itemses.get(0).getTransactionId();
                                Date return_date = Static_Constants.transaction_itemses.get(0).getSettlementDate();
                                // String return_amount = Static_Constants.transaction_itemses.get(0).getAmount();

                                BankCardReturnPro bankCardReturnPro = new BankCardReturnPro();
                                bankCardReturnPro.setTransactionId(transaction_id);
                                bankCardReturnPro.setAmount(amount);
                                bankCardReturnPro.setTransactionDateTime(return_date);

                                ReturnByIdTask returningTransTask = new ReturnByIdTask(service, TransactionReturnActivity.this, bankCardReturnPro);
                                returningTransTask.execute();
//                                } else {
//                                    Toast.makeText(mContext, "Do transaction again to perform return", Toast.LENGTH_LONG).show();
//                                }



                            } catch (Exception e) {
//                                logger.log("in CATCH onReturnTransClicked" + e.getMessage() +" "+activity_name+ " \n");
                                Timber.tag("TenderPos").v("catch in onReturnTransClicked: " + " " + e.toString() + " " + activity_name + " \n");

                            }
                        }
                    }


                };

                AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionReturnActivity.this);
                dialog.setTitle(R.string.title_dialog_authorize_and_capture);
                dialog.setView(view);
                dialog.setPositiveButton(R.string.ok, onClickListener);
                dialog.setNegativeButton(R.string.cancel, onClickListener);
                dialog.create().show();

//                String transaction_id = Static_Constants.transaction_itemses.get(0).getTransactionId();
//                Date return_date = Static_Constants.transaction_itemses.get(0).getSettlementDate();
//                String return_amount = Static_Constants.transaction_itemses.get(0).getAmount();
//
//                BankCardReturnPro bankCardReturnPro = new BankCardReturnPro();
//                bankCardReturnPro.setTransactionId(transaction_id);
//                bankCardReturnPro.setAmount(Double.valueOf(return_amount));
//                bankCardReturnPro.setTransactionDateTime(return_date);
//
//                ReturnByIdTask returningTransTask = new ReturnByIdTask(service, TransactionReturnActivity.this, bankCardReturnPro);
//                returningTransTask.execute();
            } else {
                Toast.makeText(mContext, "Do transaction again to perform return", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Timber.tag("TenderPos").v("catch in onReturnTransClicked: " + " " + e.toString() + " " + activity_name + " \n");

            Log.d("SetReturnTransaction:", e.getMessage());
        }
    }

    @Override
    public void onReturnByIdResponse(BankCardTransactionResponse response) {
        hideProgressDialog();

        try {
            Timber.tag("TenderPos").v("in onReturningTransResponse: " + " " + response + " " + activity_name + " \n");


            Log.d("ReturningTransResponse:", String.valueOf(response));
            Toast.makeText(mContext, "Return Transaction successful", Toast.LENGTH_SHORT).show();


            Intent data = new Intent();
            data.putExtra(EXTRA_RETURN_RESULT, response);
            setResult(RESULT_OK, data);
            finish();
        } catch (Exception e) {
            Log.d("ReturningTransResponse:", e.getMessage());
            // Toast.makeText(mContext, "in onReturningTransResponse:"+ e.getMessage(), Toast.LENGTH_LONG).show();
            Timber.tag("TenderPos").v("catch in onReturningTransResponse: " + " " + e.toString() + " " + activity_name + " \n");

        }

    }

    @Override
    public void onReturnByIdError(SnapApiError error) {
        hideProgressDialog();

        ApiError response = error.getErrorResponse();
        Log.d("ReturningTransResponse:", String.valueOf(response));
        Toast.makeText(mContext, "Return Transaction error:" + response.getMessages(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "Return Transaction error:" + response.getReason(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onReturnByIdError(SnapSessionError error) {
        hideProgressDialog();

        Log.d("ReturningTransResponse:", String.valueOf(error.getMessage()));
        Log.d("ReturningTransResponse:", String.valueOf(error));
        Toast.makeText(mContext, "Return Transaction failure:" + error.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "Return Transaction failure:" + error.getCause(), Toast.LENGTH_LONG).show();
        finish();

    }

    @Override
    public void onReturnByIdError(SnapConnectionError error) {
        hideProgressDialog();

        Log.d("ReturningTransResponse:", String.valueOf(error.getMessage()));
        Log.d("ReturningTransResponse:", String.valueOf(error));
        Toast.makeText(mContext, "Return Transaction failure:" + error.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "Return Transaction failure:" + error.getCause(), Toast.LENGTH_LONG).show();
        finish();

    }

    public void showProgressDialog() {
        hideProgressDialog();
        ProgressDialog dialog = new ProgressDialog(TransactionReturnActivity.this);
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


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;
        onReturnTransClicked();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(TransactionReturnActivity.this);
    }
}
