package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardReturnPro;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class ReturnByIdTask extends AsyncTask<Object, Object, BankCardTransactionResponse> {

    private BankCardReturnPro bankCardReturnPro;
    // private final ServiceBinder service;
    private final ReturnByIdCallback callback;
    private final ServiceBinder service;
    private SnapConnectionError connectionError;
    private SnapSessionError sessionError;
    private SnapApiError apiError;


    public ReturnByIdTask(ServiceBinder service, ReturnByIdCallback callback, BankCardReturnPro bankCardReturnPro) {
        this.bankCardReturnPro = bankCardReturnPro;
        this.service = service;
        this.callback = callback;
    }

    @Override
    protected BankCardTransactionResponse doInBackground(Object... params) {
        try {
            return  service.returnTransactionById(bankCardReturnPro);
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
            callback.onReturnByIdResponse(response);
        } else if (apiError != null) {
            callback.onReturnByIdError(apiError);
        } else if (sessionError != null) {
            callback.onReturnByIdError(sessionError);
        } else {
            callback.onReturnByIdError(connectionError);
        }
    }

}

