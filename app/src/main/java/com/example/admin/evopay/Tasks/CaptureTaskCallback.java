package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardCaptureResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface CaptureTaskCallback {
    void onCaptureResponse(BankCardCaptureResponse bankCardCaptureResponse);

    void onCaptureError(SnapApiError error);

    void onCaptureError(SnapSessionError error);

    void onCaptureError(SnapConnectionError error);

}
