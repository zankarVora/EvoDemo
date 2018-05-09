package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.serviceinformation.SecurityQuestionsResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.example.admin.evopay.service.ServiceBinder;

/**
 * Created by admin on 2/20/2018.
 */

public class GetAnsweredSecurityQuestionsTask extends AsyncTask<Object, Object, SecurityQuestionsResponse> {
    private final ServiceBinder service;
    private final GetAnsweredSecurityQuestionsCallback callback;
    private final String username;
    private final String password;
    private SnapApiError apiError;
    private SnapConnectionError connectionError;

    public GetAnsweredSecurityQuestionsTask(ServiceBinder service, GetAnsweredSecurityQuestionsCallback callback, String username, String password) {
        this.service = service;
        this.callback = callback;
        this.username = username;
        this.password = password;
    }

    @Override
    protected SecurityQuestionsResponse doInBackground(Object... params) {
        try {
            return service.getAnsweredSecurityQuestions(username, password);
        } catch (SnapApiError error) {
            this.apiError = error;
        } catch (SnapConnectionError error) {
            this.connectionError = error;
        }
        return null;
    }

    @Override
    protected void onPostExecute(SecurityQuestionsResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }
        if (response != null) {
            callback.onGetAnsweredSecurityQuestionsResponse(response);
        } else if (apiError != null) {
            callback.onGetAnsweredSecurityQuestionsError(apiError);
        } else {
            callback.onGetAnsweredSecurityQuestionsError(connectionError);
        }
    }

}
