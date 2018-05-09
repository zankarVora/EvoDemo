package com.example.admin.evopay;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evosnap.commercedriver.InitializeTerminalRequest;
import com.evosnap.commercedriver.SnapValidationError;
import com.evosnap.commercedriver.TransactionRequestBuilder;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardTransactionDataPro;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardTransactionResponsePro;
import com.evosnap.commercedriver.terminal.ApDupeOverrideRequest;
import com.evosnap.commercedriver.terminal.ApplicationSelectionRequest;
import com.evosnap.commercedriver.terminal.CardReadData;
import com.evosnap.commercedriver.terminal.ConfirmCardRemovedRequest;
import com.evosnap.commercedriver.terminal.ConfirmSignatureRequest;
import com.evosnap.commercedriver.terminal.FinalConfirmationRequest;
import com.evosnap.commercedriver.terminal.InitializeTerminalResult;
import com.evosnap.commercedriver.terminal.InitializeTerminalResultListener;
import com.evosnap.commercedriver.terminal.SnapTerminalError;
import com.evosnap.commercedriver.terminal.TransactionCompletedData;
import com.evosnap.commercedriver.terminal.TransactionEventListener;
import com.evosnap.commercedriver.terminal.TransactionNotification;
import com.evosnap.commercedriver.terminal.TransactionResult;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.Items.Transaction_Items;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class TransactionActivity extends AppCompatActivity implements ServiceConnection, TransactionEventListener,InitializeTerminalResultListener {

    public static final String EXTRA_TRANSACTION_RESULT = "result";
    public static final String EXTRA_TRANSACTION_RESPONSE = "response";
    public static final String EXTRA_TRANSACTION_REQUEST = "request";
    Button abort;
    private ServiceBinder service;
    String activity_name=" TransactionActivity";

    int i= 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, TransactionActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        abort = (Button) findViewById(R.id.abort);
        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                service.cancelRequest();

                finish();
            }
        });

        Timber.tag("TenderPos").v("TransactionActivity is called"  + " \n") ;


        startService(new Intent(TransactionActivity.this, CommerceDriverService.class));
        bindService(new Intent(TransactionActivity.this, CommerceDriverService.class), TransactionActivity.this, BIND_AUTO_CREATE);
    }

    private void onAuthorizeAndCaptureClicked() {

        try {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_simple_transaction_amount_entry, null);
            final EditText editText = (EditText) view.findViewById(R.id.transaction_amount);

            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {

                        try {

                            String amountString = editText.getText().toString();
                            double amount = TextUtils.isEmpty(amountString) ? 0.00 : Double.valueOf(amountString);
                            TransactionRequestBuilder request = new TransactionRequestBuilder();
                            request.setAmount(amount);

                            startAuthorizeAndCaptureTransaction(request);

                        } catch (Exception e) {
                            Timber.tag("TenderPos").v("CATCH onAuthorizeAndCaptureClicked" + e.getMessage() +" "+activity_name+ " \n");
                        }
                    }
                }
            };

            AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
            dialog.setTitle(R.string.title_dialog_authorize_and_capture);
            dialog.setView(view);
            dialog.setPositiveButton(R.string.ok, onClickListener);
            dialog.setNegativeButton(R.string.cancel, onClickListener);
            dialog.create().show();
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in onAuthorizeAndCaptureClicked: " + e.getMessage()+" "+activity_name+ " \n");

        }

    }


    private void onAuthorizeClicked() {

        // Here a dialog is displayed with a simplified form for entry of amount
        // This example starts an Authorize transaction on the "active" terminal.
        // You will have to Capture the transaction later.
        // Your example will differ greatly with the minimum implementation being a way to input a custom amount, tip, and cash back
        // No Level 2 or Line Items are added in this example.

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_simple_transaction_amount_entry, null);
        final EditText editText = (EditText) view.findViewById(R.id.transaction_amount);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {

                    String amountString = editText.getText().toString();
                    double amount = TextUtils.isEmpty(amountString) ? 0.00 : Double.valueOf(amountString);
                    TransactionRequestBuilder request = new TransactionRequestBuilder();
                    request.setAmount(amount);

                    startAuthorizeTransaction(request);
                }
            }
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
        dialog.setTitle(R.string.title_dialog_authorize);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.ok, onClickListener);
        dialog.setNegativeButton(R.string.cancel, onClickListener);
        dialog.create().show();
    }

    private void onReturnUnlinkedClicked() {

        // Here a dialog is displayed with a simplified form for entry of amount
        // This example starts a Return Unlinked transaction on the "active" terminal.
        // Your example will differ greatly with the minimum implementation being a way to input a custom amount, tip, and cash back
        // No Level 2 or Line Items are added in this example.
        // No Tip or Cash Back are added in this example.

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_simple_transaction_amount_entry, null);
        final EditText editText = (EditText) view.findViewById(R.id.transaction_amount);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {

                    try {

                        String amountString = editText.getText().toString();
                        double amount = TextUtils.isEmpty(amountString) ? 0.00 : Double.valueOf(amountString);
                        TransactionRequestBuilder request = new TransactionRequestBuilder();
                        request.setAmount(amount);

                        startReturnTransactionUnlinked(request);
                    }

                    catch(Exception e)
                    {
                        Timber.tag("TenderPos").v("CATCH onReturnUnlinkedClicked" + e.getMessage() + " \n") ;
                    }
                }
            }
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
        dialog.setTitle(R.string.title_dialog_return_unlinked);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.ok, onClickListener);
        dialog.setNegativeButton(R.string.cancel, onClickListener);
        dialog.create().show();
    }

    private void startReturnTransactionUnlinked(TransactionRequestBuilder request) {

        // The Amount has been set in the request, but there are several additional fields that must be set
        request.setOrderNumber("1234");
        request.setEmployeeId("1");
        request.setLaneId("2");
        request.setReference("5678");

        // Callbacks are used to provide status updates during a transaction. See below.

        // The status update receiver provides information on what is going on during a transaction
        // The completed receiver provides information on the result of a transaction
        // The card data receiver provides basic data on the swiped/inserted/tapped card data
        // the signature request receiver provides a notification to collect a signature
        // This is required on all terminals that do not have a signature pad built into the display.
        // the final confirmation receiver provides a way to confirm the final amount.
        // This is only required for a limited subset of terminals.
        // this sample app uses iCMP/iPP terminal library which do not require this callback
        // the duplicate override receiver provides a way to confirm that a transaction isn't a duplicate
        // In some cases, the processor may think the transaction is a duplicate.
        // This callback can be used to assert that a transaction is not a duplicate.
        // The application selection receiver provides a way to select an application on a card
        // Some cards have multiple applications. Some terminals require this callback.
        // this sample app uses iCMP/iPP terminal library which do not require this callback
        // The card removed receiver provides a way to confirm a card has been removed.
        // Some terminals require this callback.
        // this sample app uses iCMP/iPP terminal library which do not require this callback
        request.setTransactionEventListener(this);

//        authorizeAndCaptureButton.setClickable(false);
//        authorizeButton.setClickable(false);
//        returnUnlinkedButton.setClickable(false);

        // use commerce driver to start a transaction on the active terminal with the transaction request
        Log.d("services: ", String.valueOf(service));
        try {
            service.returnTransactionUnlinked(request);
        } catch (SnapValidationError snapValidationError) {
            snapValidationError.printStackTrace();
        } catch (SnapTerminalError snapTerminalError) {
            snapTerminalError.printStackTrace();

        } catch (SnapSessionError snapSessionError) {
            snapSessionError.printStackTrace();
        }

    }

    private void startAuthorizeAndCaptureTransaction(TransactionRequestBuilder request) {

        try {
            request.setOrderNumber("12345");
            request.setEmployeeId("1");
            request.setLaneId("2");
            request.setReference("56789");
            Date currentTime = Calendar.getInstance().getTime();
            request.setTransactionDateTime(currentTime);
            request.setTransactionEventListener(this);


            try {
                Timber.tag("TenderPos").v("authorizeAndCaptureTransaction request calling " + " " + activity_name + " \n");

                service.authorizeAndCaptureTransaction(request);
            } catch (SnapValidationError snapValidationError) {
                snapValidationError.printStackTrace();
                Timber.tag("TenderPos").v("isTerminalInitialized snapValidationError:" + snapValidationError.getMessage());

            } catch (SnapTerminalError snapTerminalError) {
                snapTerminalError.printStackTrace();
                Timber.tag("TenderPos").v("isTerminalInitialized snapTerminalError:" + snapTerminalError.getMessage());

            } catch (SnapSessionError snapSessionError) {
                snapSessionError.printStackTrace();
                Timber.tag("TenderPos").v("isTerminalInitialized snapSessionError:" + snapSessionError.getMessage());

            }
        }

        catch (Exception e)
        {
            Timber.tag("TenderPos").v("in startAuthorizeAndCaptureTransaction: " + e.getMessage()+" "+activity_name+ " \n");

        }

    }


    void InitializeTerminal()
    {
        try {
            InitializeTerminalRequest initializeTerminalRequest = new InitializeTerminalRequest(this);

            service.initializeTerminal(initializeTerminalRequest);

        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("catch in InitializeTerminal: " + e.getMessage()+" "+activity_name+ " \n");

        }

    }

    private void startAuthorizeTransaction(TransactionRequestBuilder request) {

        request.setOrderNumber("1234");
        request.setEmployeeId("1");
        request.setLaneId("2");
        request.setReference("5678");

        request.setTransactionEventListener(this);

        try {
            service.authorizeTransaction(request);
        } catch (SnapValidationError snapValidationError) {
            snapValidationError.printStackTrace();
        } catch (SnapTerminalError snapTerminalError) {
            snapTerminalError.printStackTrace();
        } catch (SnapSessionError snapSessionError) {
            snapSessionError.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(TransactionActivity.this);
        Timber.tag("TenderPos").v(" onDestroy method called"  + " " + activity_name + " \n");

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service1) {
        this.service = (ServiceBinder) service1;

        try {
            if (service.isTerminalInitialized()) {
                onAuthorizeAndCaptureClicked();

            } else {
                InitializeTerminal();

            }

        }
        catch (Exception e)
            {
                Timber.tag("TenderPos").v("in catch onServiceConnected: " + e.getMessage() +" "+ activity_name+"\n");

            }

        }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;

        Timber.tag("TenderPos").v("onServiceDisconnected callled:" +name.flattenToString());
        Timber.tag("TenderPos").v("onServiceDisconnected callled:" +name.flattenToShortString());
        Timber.tag("TenderPos").v("onServiceDisconnected callled:" +name.toString());



    }

    @Override
    public void onRequestSignatureConfirmation(final ConfirmSignatureRequest request) {

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    request.onSignatureConfirmed(true);
                } else {
                    request.onSignatureConfirmed(false);
                }
            }
        };

        DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.onSignatureConfirmed(false);
            }
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
        dialog.setTitle(R.string.title_dialog_signature_required);
        dialog.setMessage(R.string.message_dialog_signature_accept);
        dialog.setPositiveButton(R.string.accept, onClickListener);
        dialog.setNegativeButton(R.string.reject, onClickListener);
        dialog.setOnCancelListener(onCancelListener);
        dialog.create().show();
    }

    @Override
    public void onRequestFinalConfirmation(FinalConfirmationRequest finalConfirmationRequest) {


        Timber.tag("TenderPos").v("finalConfirmationRequest callled:" +finalConfirmationRequest.getTransactionData());


    }

    @Override
    public void onRequestApplicationSelection(ApplicationSelectionRequest applicationSelectionRequest) {

        Timber.tag("TenderPos").v("ApplicationSelectionRequest callled:" +applicationSelectionRequest.getApplicationList());

    }

    @Override
    public void onRequestConfirmCardRemoved(ConfirmCardRemovedRequest confirmCardRemovedRequest) {

        Timber.tag("TenderPos").v("ConfirmCardRemovedRequest callled:" +confirmCardRemovedRequest.toString());

    }

    @Override
    public void onTransactionNotification(TransactionNotification transactionNotification) {

//        String status= transactionNotification.toString();

        Timber.tag("TenderPos").v("in onTransactionNotification: " + transactionNotification.toString() +" "+ activity_name+"\n");

        Toast.makeText(TransactionActivity.this, transactionNotification.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardRead(CardReadData cardData) {
        try {
            if (cardData != null) {
                Log.v("TransactionActivity", "===Received Card Data===");
                Log.v("TransactionActivity", "Cardholder Name: " + cardData.getCardholderName());
                Log.v("TransactionActivity", "Masked PAN: " + cardData.getMaskedPan());
                Log.v("TransactionActivity", "Card Type: " + cardData.getCardType());
                Log.v("TransactionActivity", "Expiration: " + cardData.getExpiration());

                Timber.tag("TenderPos").v("in onCardRead CardholderName: " + cardData.getCardholderName() + " " + activity_name + "\n");

            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("catch in onCardRead: " + e.getMessage() +" "+ activity_name+"\n");

        }
    }

    @Override
    public void onTransactionCompleted(TransactionCompletedData transactionCompletedData) {
        try {

        Timber.tag("TenderPos").v(" onTransactionCompleted called "  + " " + activity_name + " \n");

        BankCardTransactionDataPro bankCardTransactionDataPro = transactionCompletedData.getTransactionData();
        BankCardTransactionResponsePro bankCardTransactionResponse = transactionCompletedData.getTransactionResponse();
        TransactionResult transactionResult = transactionCompletedData.getTransactionResult();

        Timber.tag("TenderPos").v("in BankCardTransactionCompletedReceiver transactionResult: " + transactionResult.toString() + " " + " \n");
        Timber.tag("TenderPos").v("TransactionId :  " + " "+bankCardTransactionResponse.getTransactionId() + " \n");
            Log.d("TransactionActivity", "TransactionId: " + bankCardTransactionResponse.getTransactionId());



            Intent data = new Intent();

            String result = String.valueOf(transactionResult);
            if (bankCardTransactionResponse != null && result.equals("APPROVED")) {

                Timber.tag("TenderPos").v("Transaction is approved" + " " + activity_name + " \n");
                Timber.tag("TenderPos").v("TransactionId: "+bankCardTransactionResponse.getTransactionId() + " \n");


                Transaction_Items transaction_items = new Transaction_Items();

                transaction_items.setAmount(String.valueOf(bankCardTransactionResponse.getAmount()));

                transaction_items.setTransactionId(bankCardTransactionResponse.getTransactionId());
                transaction_items.setSettlementDate(bankCardTransactionResponse.getSettlementDate());

                Static_Constants.transaction_itemses.add(transaction_items);
                data.putExtra(EXTRA_TRANSACTION_RESULT, transactionResult);
                data.putExtra(EXTRA_TRANSACTION_REQUEST, transactionResult);
                data.putExtra(EXTRA_TRANSACTION_RESPONSE, transactionResult);
                setResult(RESULT_OK, data);
                finish();
            }
            else if (result.equalsIgnoreCase("TRANSACTION_ERROR"))
            {

                i++;
                Toast.makeText(this, "TRANSACTION_ERROR"+i, Toast.LENGTH_SHORT).show();

              //  if (i==1 || i==2)
              //  {
                    try {
                        if (service.isTerminalInitialized()) {
                            onAuthorizeAndCaptureClicked();

                        } else {
                            InitializeTerminal();

                        }

                    }
                    catch (Exception e)
                    {
                        Timber.tag("TenderPos").v("in catch onServiceConnected: " + e.getMessage() +" "+ activity_name+"\n");

                    }


//                }
//                else {
//                    data.putExtra(EXTRA_TRANSACTION_RESULT, transactionResult);
//                    data.putExtra(EXTRA_TRANSACTION_REQUEST, transactionResult);
//                    data.putExtra(EXTRA_TRANSACTION_RESPONSE, transactionResult);
//                    setResult(RESULT_OK, data);
//                    finish();
//                }

            }
            else
            {
                data.putExtra(EXTRA_TRANSACTION_RESULT, transactionResult);
                data.putExtra(EXTRA_TRANSACTION_REQUEST, transactionResult);
                data.putExtra(EXTRA_TRANSACTION_RESPONSE, transactionResult);
                setResult(RESULT_OK, data);
                finish();
            }

        }
        catch(Exception e)
        {
            Timber.tag("TenderPos").v("in CATCH onTransactionCompleted" + e.getMessage()+ " " + activity_name + " \n") ;
        }
    }

    @Override
    public void onRequestApDupeOverride(final ApDupeOverrideRequest request) {


        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    request.onDuplicateOverrideConfirmation(true);
                } else {
                    request.onDuplicateOverrideConfirmation(false);
                }
            }
        };
        DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.onDuplicateOverrideConfirmation(false);
            }
        };

        AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
        dialog.setTitle(R.string.title_dialog_duplicate_override);
        dialog.setMessage(R.string.message_dialog_duplicate_override_accept);
        dialog.setPositiveButton(R.string.accept, onClickListener);
        dialog.setNegativeButton(R.string.reject, onClickListener);
        dialog.setOnCancelListener(onCancelListener);
        dialog.create().show();
    }

    @Override
    public void onInitializeTerminalResult(InitializeTerminalResult initializeTerminalResult) {
        try {
            Timber.tag("TenderPos").v(" onInitializeTerminalResult called " + "terminal initialized: " + initializeTerminalResult.isSuccessful() + " " + activity_name + " \n");

            if (initializeTerminalResult.isSuccessful()) {

                onAuthorizeAndCaptureClicked();

            } else {
                Toast.makeText(TransactionActivity.this, "Failed to initialize terminal", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        catch(Exception e)
        {
            Timber.tag("TenderPos").v("in CATCH onInitializeTerminalResult: " + e.getMessage()+ " " + activity_name + " \n") ;
        }

    }



}

