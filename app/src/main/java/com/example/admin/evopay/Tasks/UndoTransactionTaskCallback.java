package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.transactions.bankcard.BankCardTransactionResponse;
import com.evosnap.commercedriver.webservice.SnapApiError;
import com.evosnap.commercedriver.webservice.SnapConnectionError;
import com.evosnap.commercedriver.webservice.SnapSessionError;

/**
 * Created by admin on 2/20/2018.
 */

public interface UndoTransactionTaskCallback {

    void onUndoTransactionResponse(BankCardTransactionResponse bankCardTransactionResponse);

    void onUndoTransactionError(SnapApiError error);

    void onUndoTransactionError(SnapSessionError error);

    void onUndoTransactionError(SnapConnectionError error);
}
