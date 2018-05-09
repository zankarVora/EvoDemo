package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.serviceinformation.SignOnWithUserNameAndPasswordResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;
import com.evosnap.commercedriver.webservice.SnapSyncAccountError;

/**
 * Created by admin on 2/20/2018.
 */

public interface LoginTaskCallback {

    void onLoginTaskResult(SignOnWithUserNameAndPasswordResponse startSessionResponse, String username, String password);

    void onLoginError(SnapApiError error);

    void onLoginError(SnapSessionError error);

    void onLoginError(SnapSyncAccountError error);

    void onLoginError(SnapConnectionError error);
}
