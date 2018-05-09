package com.example.admin.evopay;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.evosnap.commercedriver.cws.ApiError;
import com.evosnap.commercedriver.cws.serviceinformation.SecurityQuestionsResponse;
import com.evosnap.commercedriver.cws.serviceinformation.UpdateSecurityQuestionsResponse;
import com.evosnap.commercedriver.cws.serviceinformation.security.SecurityAnswer;
import com.evosnap.commercedriver.cws.serviceinformation.security.SecurityQuestion;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.example.admin.evopay.Tasks.AccountSecurityQuestionsTaskCallback;
import com.example.admin.evopay.Tasks.AvailableSecurityQuestionsTask;
import com.example.admin.evopay.Tasks.GetAnsweredSecurityQuestionsCallback;
import com.example.admin.evopay.Tasks.GetAnsweredSecurityQuestionsTask;
import com.example.admin.evopay.Tasks.SaveSecurityQuestions;
import com.example.admin.evopay.Tasks.SaveSecurityQuestionsCallback;
import com.example.admin.evopay.service.CommerceDriverService;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class AccountSecurityActivity extends AppCompatActivity implements ServiceConnection,
        GetAnsweredSecurityQuestionsCallback, AccountSecurityQuestionsTaskCallback, SaveSecurityQuestionsCallback {

//    private final ExampleLogger logger = new ExampleLogger("AccountSecurityActivity");
    private static final int REQUEST_CODE_TRANSACTION_ACTIVITY = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    String activity_name = " AccountSecurityActivity";
    List<SecurityQuestion> questions;
    String Username;
    String Password, Amount;
    private ServiceBinder service;
    private ProgressDialog dialog;
    private Context mContext = this;

    public AccountSecurityActivity() {

    }

    public static Intent newIntent(Context context, String username, String password) {

        Intent intent = new Intent(context, AccountSecurityActivity.class);
        intent.putExtra(USERNAME, username);
        intent.putExtra(PASSWORD, password);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);


        startService(new Intent(AccountSecurityActivity.this, CommerceDriverService.class));
        bindService(new Intent(AccountSecurityActivity.this, CommerceDriverService.class), AccountSecurityActivity.this, BIND_AUTO_CREATE);

        Intent intent = getIntent();
        Username = intent.getStringExtra(USERNAME);
        Password = intent.getStringExtra(PASSWORD);

    }

    private void onAvailableSecurityQuestionsClicked() {

        try {
            showProgressDialog();

            AvailableSecurityQuestionsTask task = new AvailableSecurityQuestionsTask(service, AccountSecurityActivity.this, Username, Password);
            task.execute();
        } catch (Exception e) {
            Timber.tag("TenderPos").v("CATCH onAvailableSecurityQuestionsClicked " + e.getMessage());

        }
    }

    @Override
    public void onReturnAvailableSecurityQuestions(SecurityQuestionsResponse response) {

        try {
            hideProgressDialog();

            Timber.tag("TenderPos").v("in onReturnAvailableSecurityQuestions:"  + " " + activity_name + " \n");

            Timber.tag("TenderPos").v("in onReturnAvailableSecurityQuestions: " +  response.getQuestions() + " " + activity_name + " \n");

            Log.d("response::", String.valueOf(response));
           // Timber.tag("TenderPos").v("onAvailableSecurityQuestionsTaskResult: " + " " + response + " " + activity_name + " \n");


            questions = response.getQuestions();
            Log.d("response::", String.valueOf(response.getQuestions()));

            Log.d("questions::", String.valueOf(questions));
            AlertDialog.Builder builder = new AlertDialog.Builder(AccountSecurityActivity.this);
            builder.setTitle(R.string.title_dialog_available_security_questions);
            builder.setMessage(String.format("There are %s available security questions", questions.size()));
            builder.setPositiveButton(R.string.ok, null);
            builder.create().show();

            onSaveSecurityQuestionsClicked(questions);

        } catch (Exception e) {
            Timber.tag("TenderPos").v("catch in onAvailableSecurityQuestionsTaskResult: " + " " + e.toString() + " " + activity_name + " \n");

        }

    }

    @Override
    public void onAvailableSecurityQuestionsError(SnapApiError error) {
        ApiError response = error.getErrorResponse();
        Timber.tag("TenderPos").v(" onAvailableSecurityQuestionsError SnapApiError: "  + response.getReason() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onAvailableSecurityQuestionsError SnapApiError: "  + response.getMessages() + " " + activity_name + " \n");

        Toast.makeText(mContext, "fail onAvailableSecurityQuestionsTaskResult" + response.getMessages(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "fail onAvailableSecurityQuestionsTaskResult" + response.getReason(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAvailableSecurityQuestionsError(SnapConnectionError error) {

        Timber.tag("TenderPos").v(" onAvailableSecurityQuestionsError SnapConnectionError: "  + error.getMessage() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onAvailableSecurityQuestionsError SnapConnectionError: "  + error.getCause() + " " + activity_name + " \n");
        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + error.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + error.getCause(), Toast.LENGTH_LONG).show();
    }

    private void onSaveSecurityQuestionsClicked(List<SecurityQuestion> questions) {
        try {
            showProgressDialog();


            //  List<SecurityQuestion> availableQuestions =questions;

            for (int i = 0; i < questions.size(); i++) {

                Log.d("available ques Code:", questions.get(i).getQuestionCode());
                Log.d("available ques Text:", questions.get(i).getQuestionText());

            }

            List<SecurityAnswer> answers = new ArrayList<>();

            ArrayList<String> code = new ArrayList<String>(Arrays.asList("MotherMaiden", "SchoolMascot", "FirstPet"));

            ArrayList<String> answer = new ArrayList<String>(Arrays.asList("julia", "Lowell", "angel"));

            for (int i = 0; i < code.size(); i++) {

                String answer_code = code.get(i);
                String answer_text = answer.get(i);

                SecurityAnswer securityAnswer = new SecurityAnswer();
                securityAnswer.setAnswer(answer_text);
                securityAnswer.setQuestionCode(answer_code);
                answers.add(securityAnswer);

            }

            Log.d("answers_list size", String.valueOf(answers.size()));

            if (answers.size() > 0) {

                SaveSecurityQuestions task = new SaveSecurityQuestions(service, AccountSecurityActivity.this, Username, Password, answers);
                task.execute();
            }

        } catch (Exception e) {
            Timber.tag("TenderPos").v("catch in onSaveSecurityQuestionsClicked: "  + e.toString() + " " + activity_name + " \n");

        }

    }

    @Override
    public void onSaveSecurityQuestionsTask(UpdateSecurityQuestionsResponse response) {
        hideProgressDialog();

        Log.d("response::", String.valueOf(response));

        try {
            Timber.tag("TenderPos").v("in onSaveSecurityQuestionsTask: " + " " + response + " " + activity_name + " \n\n");

            Toast.makeText(mContext, "security questions saved successfully", Toast.LENGTH_SHORT).show();

            startActivity(TerminalActivity.newIntent(AccountSecurityActivity.this));
            finish();


        } catch (Exception e) {

            Timber.tag("TenderPos").v("catch in onSaveSecurityQuestionsTask: "  + e.toString() + " " + activity_name + " \n");


        }
    }

    @Override
    public void onSaveSecurityQuestionsError(SnapApiError error) {
        hideProgressDialog();
        ApiError response = error.getErrorResponse();
        Timber.tag("TenderPos").v(" onSaveSecurityQuestionsError SnapApiError: "  + response.getReason() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onSaveSecurityQuestionsError SnapApiError: "  + response.getMessages() + " " + activity_name + " \n");

        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + response.getMessages(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + response.getReason(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveSecurityQuestionsError(SnapConnectionError error) {
        hideProgressDialog();
        Timber.tag("TenderPos").v(" onSaveSecurityQuestionsError SnapConnectionError: "  + error.getMessage() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onSaveSecurityQuestionsError SnapConnectionError: "  + error.getCause() + " " + activity_name + " \n");

        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + error.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "error onSaveSecurityQuestionsTask" + error.getCause(), Toast.LENGTH_LONG).show();
    }


    private void onAnsweredSecurityQuestionsClicked() {

        try {
            showProgressDialog();
            Timber.tag("TenderPos").v(" in onAnsweredSecurityQuestionsClicked"  + " " + activity_name + " \n");

            GetAnsweredSecurityQuestionsTask task = new GetAnsweredSecurityQuestionsTask(service, AccountSecurityActivity.this, Username, Password);
            task.execute();
        } catch (Exception e) {
            Timber.tag("TenderPos").v("catch in calling onAnsweredSecurityQuestionsClicked: " + activity_name + " \n");

        }
    }

    @Override
    public void onGetAnsweredSecurityQuestionsResponse(SecurityQuestionsResponse response) {
        hideProgressDialog();


        try {
            Log.d("response::", String.valueOf(response));

            Timber.tag("TenderPos").v("in onAnsweredSecurityQuestionsTaskResult called"  + " " + activity_name + " \n");

            questions = response.getQuestions();
//            Toast.makeText(mContext, "%s security questions have been answered", Toast.LENGTH_SHORT).show();

//            AlertDialog.Builder builder = new AlertDialog.Builder(AccountSecurityActivity.this);
//            builder.setTitle(R.string.title_dialog_answered_security_questions);
//            builder.setMessage(String.format("%s security questions have been answered", questions.size()));
//            builder.setPositiveButton(R.string.ok, null);
//            builder.create().show();

            if (questions != null && questions.size() >= 3) {

                //  onAddIcmpClicked();
                startActivity(TerminalActivity.newIntent(AccountSecurityActivity.this));
                finish();

            } else {
                onAvailableSecurityQuestionsClicked();
            }


        } catch (Exception e) {
            Timber.tag("TenderPos").v(" " + "catch in onAnsweredSecurityQuestionsTaskResult: " + e.toString() + " " + " \n");
        }
    }

    @Override
    public void onGetAnsweredSecurityQuestionsError(SnapApiError error) {
        hideProgressDialog();
        ApiError response = error.getErrorResponse();
//        Timber.tag("TenderPos").v("isTerminalInitialized error:" + error.getMessage());
        Timber.tag("TenderPos").v(" onGetAnsweredSecurityQuestionsError SnapApiError: "  + response.getMessages() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onGetAnsweredSecurityQuestionsError SnapApiError: "  + response.getReason() + " " + activity_name + " \n");

        Toast.makeText(mContext, "error onAnsweredSecurityQuestionsTaskResult" + response.getMessages(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "error onAnsweredSecurityQuestionsTaskResult" + response.getReason(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetAnsweredSecurityQuestionsError(SnapConnectionError error) {
        hideProgressDialog();
//        Timber.tag("TenderPos").v("onGetAnsweredSecurityQuestionsError error:" + error.getMessage());
        Timber.tag("TenderPos").v(" onGetAnsweredSecurityQuestionsError SnapConnectionError: "  + error.getMessage() + " " + activity_name + " \n");
        Timber.tag("TenderPos").v(" onGetAnsweredSecurityQuestionsError SnapConnectionError: "  + error.getCause() + " " + activity_name + " \n");

        Toast.makeText(mContext, "error onAnsweredSecurityQuestionsTaskResult" + error.getMessage(), Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "error onAnsweredSecurityQuestionsTaskResult" + error.getCause(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = (ServiceBinder) service;
        onAnsweredSecurityQuestionsClicked();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.service = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(AccountSecurityActivity.this);
    }

    public void showProgressDialog() {
        hideProgressDialog();
        ProgressDialog dialog = new ProgressDialog(AccountSecurityActivity.this);
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

