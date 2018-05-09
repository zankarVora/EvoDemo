package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.serviceinformation.ChangeUserPasswordResponse;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class ChangePasswordTask extends AsyncTask<Object, Object, ChangeUserPasswordResponse> {

    private final ServiceBinder service;
    private final ChangePasswordTaskCallback callback;
    private final String username;
    private final String oldPassword;
    private final String newPassword;
    private Exception exception;

    public ChangePasswordTask(ServiceBinder service,
                              ChangePasswordTaskCallback callback,
                              String username,
                              String oldPassword,
                              String newPassword) {
        this.service = service;
        this.callback = callback;
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    @Override
    protected ChangeUserPasswordResponse doInBackground(Object... params) {
        try {
            return service.changePassword(username, oldPassword, newPassword);
        } catch (Exception e) {
            this.exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ChangeUserPasswordResponse apiResponse) {
        super.onPostExecute(apiResponse);
        if (callback != null) {
            if (apiResponse != null) {
                callback.onChangePasswordTaskResult(apiResponse);
            } else {
                callback.onChangePasswordError(exception);
            }
        }
    }

}
