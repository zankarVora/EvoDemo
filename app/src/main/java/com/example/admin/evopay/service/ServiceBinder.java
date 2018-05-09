package com.example.admin.evopay.service;

import android.os.Binder;

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

public class ServiceBinder extends Binder {
    private final CommerceDriverService service;

    public ServiceBinder(CommerceDriverService service) {
        this.service = service;
    }

    public SignOnWithUserNameAndPasswordResponse login(String username, String password) throws SnapApiError, SnapSessionError, SnapSyncAccountError, SnapConnectionError {
        return service.login(username, password);
    }

    public ChangeUserPasswordResponse changePassword(String username, String oldPassword, String newPassword) throws SnapApiError, SnapConnectionError {
        return service.changePassword(username, oldPassword, newPassword);
    }

    public PasswordExpirationResponse getPasswordExpiration(String username, String password) throws SnapApiError, SnapConnectionError {
        return service.getPasswordExpiration(username, password);
    }

    public SecurityQuestionsResponse getAnsweredSecurityQuestions(String username, String password) throws SnapApiError, SnapConnectionError {
        return service.getAnsweredSecurityQuestions(username, password);
    }

    public SecurityQuestionsResponse getAvailableSecurityQuestions(String username, String password) throws SnapApiError, SnapConnectionError {
        return service.getAvailableSecurityQuestions(username, password);
    }

    public boolean addTerminal(String id, Terminal terminal) {
        return service.addTerminal(id, terminal);
    }

    public List<String> getTerminalIds() {
        return service.getTerminalIds();
    }

    public boolean selectTerminal(String id) {
        return service.selectTerminal(id);
    }

    public void returnTransactionUnlinked(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        service.returnTransactionUnlinked(request);
    }
    public void cancelRequest()
    {
        service.cancelRequest();
    }


    public void authorizeAndCaptureTransaction(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        service.authorizeAndCaptureTransaction(request);
    }

    public void authorizeTransaction(TransactionRequestBuilder request) throws SnapValidationError, SnapTerminalError, SnapSessionError {
        service.authorizeTransaction(request);
    }

    public UpdateSecurityQuestionsResponse saveSecurityQuestions(String username, String password, List<SecurityAnswer> answers) throws SnapApiError, SnapConnectionError {
        return  service.saveSecurityQuestions(username, password,answers);
    }

    public BankCardCaptureResponse captureTransaction(BankCardCapturePro differenceData) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return  service.captureTransaction(differenceData);
    }


//    public void startTransaction(POSBankCardReturnUnlinkedRequest request) {
//        service.startTransaction(request);
//    }

    public BankCardTransactionResponse returnTransactionById(BankCardReturnPro bankCardReturnPro) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return service.returnTransactionById(bankCardReturnPro);
    }

    public BankCardTransactionResponse undoTransaction(BankCardUndo bankCardUndo) throws SnapConnectionError, SnapSessionError, SnapApiError {
        return  service.undoTransaction(bankCardUndo);
    }
//    public void resetTerminal() {
//        service.resetTerminal();
//    }

    public boolean isTerminalInitialized() {
        return service.isTerminalInitialized();
    }

    public boolean isLoggedIn() {
        return service.isLoggedIn();
    }
    public boolean hasTerminal() {
        return service.hasTerminal();
    }

    public void initializeTerminal(InitializeTerminalRequest request) throws SnapTerminalError {
        service.initializeTerminal(request);
    }

}
