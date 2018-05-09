package com.example.admin.evopay.Tasks;

import com.evosnap.commercedriver.cws.serviceinformation.ChangeUserPasswordResponse;

/**
 * Created by admin on 2/20/2018.
 */

public interface ChangePasswordTaskCallback {

    void onChangePasswordTaskResult(ChangeUserPasswordResponse apiResponse);

    void onChangePasswordError(Exception exception);
}
