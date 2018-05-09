package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.Button;
import android.widget.Toast;

import com.evosnap.commercedriver.Terminal;
import com.evosnap.ingenico.terminal.RBATerminalFactory;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.Set;

import timber.log.Timber;

public class TerminalActivity extends AppCompatActivity implements ServiceConnection {


    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private ServiceBinder service;
    private ProgressDialog dialog;
    private static final int REQUEST_CODE_TRANSACTION_ACTIVITY = 1;
    String activity_name=" TerminalActivity";
    Button terminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        startService(new Intent(TerminalActivity.this, CommerceDriverService.class));
        bindService(new Intent(TerminalActivity.this, CommerceDriverService.class), TerminalActivity.this, BIND_AUTO_CREATE);


    }

    public static Intent newIntent(Context context) {
        return new Intent(context, TerminalActivity.class);
    }

    public TerminalActivity() {

    }


    private void onAddIcmpClicked() {

        try{
            Timber.tag("TenderPos").v("in onAddIcmpClicked " );


            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();

            BluetoothDevice[] bondedDeviceArray = new BluetoothDevice[bondedDevices.size()];
            bondedDeviceArray = bondedDevices.toArray(bondedDeviceArray);

            final String[] bondedNames = new String[bondedDevices.size()];

            for (int i = 0; i < bondedDeviceArray.length; i++) {
                bondedNames[i] = bondedDeviceArray[i].getName();
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(TerminalActivity.this);
            builder.setTitle(R.string.title_dialog_add_icmp_terminal);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setSingleChoiceItems(bondedNames, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String btName = bondedNames[which];

                    Terminal terminal = RBATerminalFactory.newICMPTerminal(btName);
                    boolean added = service.addTerminal(btName, terminal);

                    if (added) {


                        Timber.tag("TenderPos").v(" Terminal added "  + added+" " + activity_name + " \n");

                        Snackbar.make(findViewById(android.R.id.content), String.format("Add Terminal '%s' Success", btName), Snackbar.LENGTH_SHORT).show();
//                        Static_Constants.EvoTerminalAdded = "Yes";
//                    onStartTransactionClicked();

                        finish();

                    }
                    else {
                        Timber.tag("TenderPos").v(" Terminal not  added "  + added +" " + activity_name + " \n");

                        Snackbar.make(findViewById(android.R.id.content), String.format("Add Terminal '%s' Failed", btName), Snackbar.LENGTH_SHORT).show();
//                        Static_Constants.EvoTerminalAdded = "No";
                        onAddIcmpClicked();
                    }

                    dialog.dismiss();

                }
            });
            builder.create().show();

        }
        catch(Exception e)
        {
            Timber.tag("TenderPos").v("CATCH in onAddIcmpClicked" + e.getMessage() + " \n") ;
        }
    }



//    private void onStartTransactionClicked() {
//        if (service.hasTerminal()) {
//            startActivityForResult(TransactionActivity.newIntent(TerminalActivity.this), REQUEST_CODE_TRANSACTION_ACTIVITY);
//        } else {
//            Snackbar.make(findViewById(android.R.id.content), R.string.snackbar_message_please_add_terminal, Snackbar.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (REQUEST_CODE_TRANSACTION_ACTIVITY == requestCode) {
//            onHandleTransactionActivityResult(resultCode, data);
//        }
//    }

//    private void onHandleTransactionActivityResult(int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//
//            TransactionResult result = (TransactionResult) data.getSerializableExtra(TransactionActivity.EXTRA_TRANSACTION_RESULT);
//            Snackbar.make(findViewById(android.R.id.content), String.valueOf(result), Snackbar.LENGTH_SHORT).show();
//        } else {
//            if (service != null) {
//                service.resetTerminal();
//            } else {
//                Intent intent = CommerceDriverService.newResetIntent(TerminalActivity.this);
//                startService(intent);
//            }
//            Snackbar.make(findViewById(android.R.id.content), "Transaction Cancelled", Snackbar.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    // Bluetooth is not enable :)
                    Timber.tag("TenderPos").v("bluetooth is not enabled"+ " " + activity_name + " \n");

                    Toast.makeText(this, "enable bluetooth", Toast.LENGTH_SHORT).show();

                } else {
                    Timber.tag("TenderPos").v("bluetooth is enabled"+ " " + activity_name + " \n");

                    onAddIcmpClicked();

                }
            }
        }
        catch (Exception e)
        {
            Timber.tag("TenderPos").v("carch in onCreate "  +e.getMessage() + " " + activity_name + " \n");

        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(TerminalActivity.this);
    }



}

