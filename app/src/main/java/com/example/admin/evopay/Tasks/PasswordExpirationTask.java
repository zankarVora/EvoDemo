package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.serviceinformation.PasswordExpirationResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class PasswordExpirationTask extends AsyncTask<Object, Object, PasswordExpirationResponse> {
    private final ServiceBinder service;
    private final PasswordExpirationCallback callback;
    private final String username;
    private final String password;
    private SnapApiError apiError;
    private SnapConnectionError connectionError;

    public PasswordExpirationTask(ServiceBinder service, PasswordExpirationCallback callback, String username, String password) {

        this.service = service;
        this.callback = callback;
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordExpirationResponse doInBackground(Object... params) {
        try {
            return service.getPasswordExpiration(username, password);
        } catch (SnapApiError snapApiError) {
            this.apiError = snapApiError;
        } catch (SnapConnectionError error) {
            this.connectionError = error;
        }
        return null;
    }

    @Override
    protected void onPostExecute(PasswordExpirationResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }

        if (response != null) {
            callback.onPasswordExpirationTaskResult(response);
        } else if (apiError != null) {
            callback.onPasswordExpirationError(apiError);
        } else {
            callback.onPasswordExpirationError(connectionError);
        }
    }

}
