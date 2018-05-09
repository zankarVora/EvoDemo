package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardUndo;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class UndoTransactionTask extends AsyncTask<Object, Object, BankCardTransactionResponse> {

    private BankCardUndo bankCardUndo;
    private final UndoTransactionTaskCallback callback;
    private final ServiceBinder service;
    private SnapConnectionError connectionError;
    private SnapSessionError sessionError;
    private SnapApiError apiError;


    public UndoTransactionTask(ServiceBinder service, UndoTransactionTaskCallback callback, BankCardUndo bankCardUndo) {
        this.bankCardUndo = bankCardUndo;
        this.service = service;
        this.callback = callback;
    }

    @Override
    protected BankCardTransactionResponse doInBackground(Object... params) {


        try {
            return  service.undoTransaction(bankCardUndo);
        } catch (SnapConnectionError error) {
            this.connectionError = error;
        } catch (SnapSessionError error) {
            this.sessionError = error;
        } catch (SnapApiError error) {
            this.apiError = error;
        }
        return null;
    }

    @Override
    protected void onPostExecute(BankCardTransactionResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }
        if (response != null) {
            callback.onUndoTransactionResponse(response);
        } else if (apiError != null) {
            callback.onUndoTransactionError(apiError);
        } else if (sessionError != null) {
            callback.onUndoTransactionError(sessionError);
        } else {
            callback.onUndoTransactionError(connectionError);
        }
    }

}
