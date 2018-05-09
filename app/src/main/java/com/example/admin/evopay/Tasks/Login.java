package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.evosnap.commercedriver.cws.serviceinformation.SignOnWithUserNameAndPasswordResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.evosnap.commercedriver.webservice.SnapSyncAccountError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class Login extends AsyncTask<Object, Object, SignOnWithUserNameAndPasswordResponse> {

    private final ServiceBinder service;
    private final LoginTaskCallback callback;
    private final String username;
    private final String password;
    private SnapApiError apiError;
    private SnapSessionError sessionError;
    private SnapSyncAccountError accountError;
    private SnapConnectionError connectionError;

    public Login(ServiceBinder service, LoginTaskCallback callback, String username, String password) {
        this.service = service;
        this.callback = callback;
        this.username = username;
        this.password = password;
        Log.d("username:  ",username);
        Log.d("password:  ",password);

    }

    @Override
    protected SignOnWithUserNameAndPasswordResponse doInBackground(Object... params) {
        try {
            return service.login(username, password);
        } catch (SnapApiError e) {
            this.apiError = e;
        } catch (SnapSessionError e) {
            this.sessionError = e;
        } catch (SnapSyncAccountError e) {
            this.accountError = e;
        } catch (SnapConnectionError e) {
            this.connectionError = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(SignOnWithUserNameAndPasswordResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }
        if (response != null) {
            callback.onLoginTaskResult(response, username, password);
        } else if (apiError != null) {
            callback.onLoginError(apiError);
        } else if (sessionError != null) {
            callback.onLoginError(sessionError);
        } else if (accountError != null) {
            callback.onLoginError(accountError);
        } else {
            callback.onLoginError(connectionError);
        }
    }

}
