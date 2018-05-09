package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface ReturnByIdCallback {

    void onReturnByIdResponse(BankCardTransactionResponse response);

    void onReturnByIdError(SnapApiError error);

    void onReturnByIdError(SnapSessionError error);

    void onReturnByIdError(SnapConnectionError error);
}
