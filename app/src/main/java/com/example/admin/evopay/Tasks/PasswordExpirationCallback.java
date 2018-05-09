package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.serviceinformation.PasswordExpirationResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface PasswordExpirationCallback {

    void onPasswordExpirationTaskResult(PasswordExpirationResponse response);

    void onPasswordExpirationError(SnapApiError error);

    void onPasswordExpirationError(SnapConnectionError error);
}
