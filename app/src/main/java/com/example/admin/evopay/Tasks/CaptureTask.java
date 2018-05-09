package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardCaptureResponse;
import com.evosnap.commercedriver.cws.transactions.bankcard.pro.BankCardCapturePro;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class CaptureTask extends AsyncTask<Object, Object, BankCardCaptureResponse> {

    BankCardCapturePro bankCardCapturePro;
    private final CaptureTaskCallback callback;
    private final ServiceBinder service;
    private SnapConnectionError connectionError;
    private SnapSessionError sessionError;
    private SnapApiError apiError;

    public CaptureTask(ServiceBinder service, CaptureTaskCallback callback, BankCardCapturePro bankCardCapturePro) {
        this.bankCardCapturePro = bankCardCapturePro;

        this.service = service;
        this.callback = callback;

    }

    @Override
    protected BankCardCaptureResponse doInBackground(Object... params) {
        try {
            return  service.captureTransaction(bankCardCapturePro);
        } catch (SnapConnectionError error) {
            this.connectionError = error;
        } catch (SnapSessionError snapSessionError) {
            this.sessionError = snapSessionError;
        } catch (SnapApiError snapApiError) {
            this.apiError = snapApiError;
        }

        return null;
    }

    @Override
    protected void onPostExecute(BankCardCaptureResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }
        // Log.d("inCaptureTask:",response.getTransactionId());

        if (response != null) {
            callback.onCaptureResponse(response);
        } else if (apiError != null) {
            callback.onCaptureError(apiError);
        } else if (sessionError != null) {
            callback.onCaptureError(sessionError);
        } else {
            callback.onCaptureError(connectionError);
        }
    }


}
