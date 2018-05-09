package com.example.admin.evopay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.evosnap.commercedriver.CommerceDriver;
import com.evosnap.commercedriver.InitializeTerminalRequest;
import com.evosnap.commercedriver.SnapValidationError;
import com.evosnap.commercedriver.Terminal;
import com.evosnap.commercedriver.TransactionRequestBuilder;
import com.evosnap.commercedriver.cws.serviceinformation.ChangeUserPasswordResponse;
import com.evosnap.commercedriver.cws.serviceinformation.PasswordExpirationResponse;
import com.evosnap.commercedriver.cws.serviceinformation.SecurityQuestionsResponse;
import com.evosnap.commercedriver.cws.serviceinformation.SignOnWithUserNameAndPasswordResponse;
import com.evosnap.commercedriver.cws.serviceinformation.UpdateSecurityQuestionsResponse;
import com.evosnap.commercedriver.cws.serviceinformation.security.SecurityAnswer;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardCaptureResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardUndo;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardCapturePro;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardReturnPro;
import com.evosnap.commercedriver.terminal.SnapTerminalError;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.evosnap.commercedriver.webservice.SnapSyncAccountError;

import java.util.List;

/**
 * Created by admin on 2/20/2018.
 */

public class CommerceDriverService extends Service {

    private static final String ACTION_RESET_TERMINAL = "reset";
    private static final String APPLICATION_PROFILE_ID = "1449707";
    private static final String SERVICE_KEY = "1E5B57F7D871300C";
    private ServiceBinder binder;
    private CommerceDriver commerceDriver;

    public CommerceDriverService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        commerceDriver = new CommerceDriver(APPLICATION_PROFILE_ID, SERVICE_KEY);
        binder = new ServiceBinder(CommerceDriverService.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (commerceDriver != null && intent != null && ACTION_RESET_TERMINAL.equals(intent.getAction())) {
            commerceDriver.clearTerminals();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public SignOnWithUserNameAndPasswordResponse login(String username, String password) throws SnapApiError, SnapSyncAccountError, SnapConnectionError, SnapSessionError {
        return commerceDriver.loginWithUsernameAndPassword(username, password);
    }

    public ChangeUserPasswordResponse changePassword(String username, String oldPassword, String newPassword) throws SnapApiError, SnapConnectionError {
        return commerceDriver.changePassword(username, oldPassword, newPassword);
    }

    public PasswordExpirationResponse getPasswordExpiration(String username, String password) throws SnapApiError, SnapConnectionError {
        return commerceDriver.getUserExpiration(username, password);
    }

    public SecurityQuestionsResponse getAnsweredSecurityQuestions(String username, String password) throws SnapApiError, SnapConnectionError {
        return commerceDriver.getSecurityQuestions(username, password);
    }

    public SecurityQuestionsResponse getAvailableSecurityQuestions(String username, String password) throws SnapApiError, SnapConnectionError {
        return commerceDriver.getAvailableSecurityQuestions(username, password);
    }

    public boolean addTerminal(String id, Terminal terminal) {
        return commerceDriver.addTerminal(terminal);
    }

    public List<String> getTerminalIds() {
        return commerceDriver.getTerminalIds();
    }

    public boolean selectTerminal(String id) {
        return commerceDriver.selectTerminal(id);
    }

    public void returnTransactionUnlinked(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        commerceDriver.returnTransactionUnlinked(request);
    }

    public void authorizeAndCaptureTransaction(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        commerceDriver.authorizeAndCaptureTransaction(request);
    }

    public void authorizeTransaction(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        commerceDriver.authorizeTransaction(request);
    }

    public BankCardTransactionResponse returnTransactionById(BankCardReturnPro bankCardReturnPro) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return  commerceDriver.returnTransactionById(bankCardReturnPro);
    }

    public BankCardTransactionResponse undoTransaction(BankCardUndo bankCardUndo) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return  commerceDriver.undoTransaction(bankCardUndo);
    }

    public UpdateSecurityQuestionsResponse saveSecurityQuestions(String username, String password, List<SecurityAnswer> answers) throws SnapApiError, SnapConnectionError {
        return  commerceDriver.updateSecurityQuestions(username, password,answers);
    }

    public BankCardCaptureResponse captureTransaction(BankCardCapturePro differenceData) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return  commerceDriver.captureTransaction(differenceData);
    }


//   // public void resetTerminal() {
//        commerceDriver.clearTerminals();
//    }

    public boolean hasTerminal() {
        return commerceDriver.getSelectedTerminalId() != null;
    }

    public boolean isTerminalInitialized() {
        return commerceDriver.isTerminalInitialized();
    }

    public void initializeTerminal(InitializeTerminalRequest request) throws SnapTerminalError {
        commerceDriver.initializeTerminal(request);
    }
    public void cancelRequest()
    {
        commerceDriver.cancelRequest();
    }

    public boolean isLoggedIn() {
        return commerceDriver.isLoggedIn();
    }

//    public static Intent newResetIntent(Context context) {
//        Intent intent = new Intent(context, CommerceDriverService.class);
//        intent.setAction(ACTION_RESET_TERMINAL);
//        return intent;
//    }

    public Terminal getTerminalById(String id)
    {
        return commerceDriver.getTerminalById(id);
    }
    public String getSelectedTerminalId()
    {
        return commerceDriver.getSelectedTerminalId();
    }

}
