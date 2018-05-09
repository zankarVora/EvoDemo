package com.example.admin.evopay.Tasks;

import android.os.AsyncTask;

import com.evosnap.commercedriver.cws.serviceinformation.UpdateSecurityQuestionsResponse;
import com.evosnap.commercedriver.cws.serviceinformation.security.SecurityAnswer;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.example.admin.evopay.service.ServiceBinder;

import java.util.List;

/**
 * Created by admin on 2/20/2018.
 */

public class SaveSecurityQuestions extends AsyncTask<Object, Object, UpdateSecurityQuestionsResponse> {
    private final ServiceBinder service;
    private final SaveSecurityQuestionsCallback callback;
    private final String username;
    private final String password;
    private final List<SecurityAnswer> answers_list;
    private SnapApiError apiError;
    private SnapConnectionError connectionError;

    public SaveSecurityQuestions(ServiceBinder service, SaveSecurityQuestionsCallback callback, String username, String password, List<SecurityAnswer> answers_list) {
        this.service = service;
        this.callback = callback;
        this.username = username;
        this.password = password;
        this.answers_list = answers_list;

    }

    @Override
    protected UpdateSecurityQuestionsResponse doInBackground(Object... params) {

        try {
            return service.saveSecurityQuestions(username, password,answers_list);
        } catch (SnapApiError snapApiError) {
            this.apiError = snapApiError;
        } catch (SnapConnectionError error) {
            this.connectionError = error;
        }
        return null;
    }

    @Override
    protected void onPostExecute(UpdateSecurityQuestionsResponse response) {
        super.onPostExecute(response);
        if (callback == null) {
            return;
        }

        if (response != null) {
            callback.onSaveSecurityQuestionsTask(response);
        } else if (apiError != null) {
            callback.onSaveSecurityQuestionsError(apiError);
        } else {
            callback.onSaveSecurityQuestionsError(connectionError);
        }
    }

}

