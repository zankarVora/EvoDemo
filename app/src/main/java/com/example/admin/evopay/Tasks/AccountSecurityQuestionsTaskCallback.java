package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.serviceinformation.SecurityQuestionsResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface AccountSecurityQuestionsTaskCallback {

    void onReturnAvailableSecurityQuestions(SecurityQuestionsResponse apiResponse);

    void onAvailableSecurityQuestionsError(SnapApiError error);

    void onAvailableSecurityQuestionsError(SnapConnectionError error);
}
