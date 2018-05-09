package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.serviceinformation.UpdateSecurityQuestionsResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface SaveSecurityQuestionsCallback {

    void onSaveSecurityQuestionsTask(UpdateSecurityQuestionsResponse response);

    void onSaveSecurityQuestionsError(SnapApiError apiError);

    void onSaveSecurityQuestionsError(SnapConnectionError connectionError);
}
