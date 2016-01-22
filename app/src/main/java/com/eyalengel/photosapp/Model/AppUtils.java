package com.eyalengel.photosapp.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by EyalEngel on 20/01/16.
 */
public class AppUtils
{
    public static void popupErrorMsg(Context ctx, int userInputStatus)
    {
        String errorMsg = buildMessage(userInputStatus);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Login Error")
                .setMessage(errorMsg)
                .setCancelable(false)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing (dismiss)
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static String buildMessage(int userInputStatus)
    {
        String errorMsg = null;

        switch (userInputStatus)
        {
            case AppConstants.EMPTY_USER_ID_AND_PASSWORD: //empty user id and empty password
                errorMsg = AppConstants.EMPTY_USER_ID_AND_PASSWORD_MSG;
                break;
            case AppConstants.EMPTY_USER_ID:
                errorMsg = AppConstants.EMPTY_USER_ID_MSG;
                break;
            case AppConstants.EMPTY_PASSWORD:
                errorMsg = AppConstants.EMPTY_PASSWORD_MSG;
                break;
            case AppConstants.BAD_USER_ID_AND_PASSWORD:
                errorMsg = AppConstants.BAD_USER_ID_AND_PASSWORD_MSG;
                break;
            case AppConstants.BAD_USER_ID:
                errorMsg = AppConstants.BAD_USER_ID_MSG;
                break;
            case AppConstants.BAD_PASSWORD:
                errorMsg = AppConstants.BAD_PASSWORD_MSG;
                break;
            default:
                break;
        }

        return errorMsg;
    }
}
