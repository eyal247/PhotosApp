package com.eyalengel.photosapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.eyalengel.photosapp.Model.AppConstants;
import com.eyalengel.photosapp.Model.AppUtils;
import com.eyalengel.photosapp.Model.SavedSharedPreferences;
import com.eyalengel.photosapp.R;

public class MainActivity extends AppCompatActivity
{
    private EditText userIdEditText; 
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBarTitle();
        setSharedPreferences();
        getUIComponents();
        setLoginButtonListener();
        checkIfUserAlreadyLoggedIn();
    }

    private void setActionBarTitle()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Log In");
    }

    private void setLoginButtonListener()
    {
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginButtonClick();
            }
        });
    }

    private void handleLoginButtonClick()
    {
        String userID, password;
        int userInputStatus;

        userID = userIdEditText.getText().toString();
        password = passwordEditText.getText().toString();
        userInputStatus = isInputValid(userID, password);

        if(userInputStatus == AppConstants.USER_INPUT_OK)
        {
            checkIfUserExistsInDB(userID, password); //this is just to demonstrate a "real world" check
                                                        // when you need to check input with server DB
        }
        else
        {
            AppUtils.popupErrorMsg(MainActivity.this, userInputStatus);
        }
    }

    private void checkIfUserExistsInDB(String userID, String password)
    {
        new CheckUserLoginDetailsTask().execute(userID, password);
    }

    private void getUIComponents()
    {
        userIdEditText = (EditText) findViewById(R.id.login_userID_ET);
        passwordEditText = (EditText) findViewById(R.id.login_password_ET);
        loginButton = (Button) findViewById(R.id.login_button);
    }

    //init the shared preferences class variables
    private void setSharedPreferences() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        SharedPreferences count = getSharedPreferences("photo_info", MODE_PRIVATE);
        SavedSharedPreferences.setSharedPreferenceCount(count);
        SavedSharedPreferences.photo_info.edit().putInt("count", 0).commit();
        SavedSharedPreferences.photo_info.edit().putInt("width", width).commit();
        SavedSharedPreferences.photo_info.edit().putInt("height", height).commit();

        SharedPreferences login = getSharedPreferences("alreadyLoggedIn", MODE_PRIVATE);
        SavedSharedPreferences.setSharedPreferenceLogin(login);
    }

    private void checkIfUserAlreadyLoggedIn() {
        Boolean flag_login= SavedSharedPreferences.alreadyLoggedIn.getBoolean("alreadyLoggedIn", false);

        if (flag_login) { //if user already logged in, go to Home activity
            startActivity(new Intent(MainActivity.this, UserHomeActivity.class));
            MainActivity.this.finish();
        }
    }

    //checking if one or more fields are empty before "checking details with server"
    private int isInputValid(String userID, String password) {

        //checking if one or more fields are empty before "checking details with server"
        if(userID.equals(AppConstants.EMPTY_STRING) && password.equals(AppConstants.EMPTY_STRING))
            return AppConstants.EMPTY_USER_ID_AND_PASSWORD;
        else if(userID.equals(AppConstants.EMPTY_STRING))
            return AppConstants.EMPTY_USER_ID;
        else if (password.equals(AppConstants.EMPTY_STRING))
            return AppConstants.EMPTY_PASSWORD;
        else
            return AppConstants.USER_INPUT_OK;
    }

    private class CheckUserLoginDetailsTask extends AsyncTask<String, String, Integer>
    {

        @Override
        protected Integer doInBackground(String... params)
        {
            return isUserExist(params[0], params[1]);
        }

        protected void onPostExecute(Integer result) {

            if(result == AppConstants.USER_INPUT_OK)
                switchToUserHomeActivity(false);
            else
                AppUtils.popupErrorMsg(MainActivity.this, result);
        }

        private int isUserExist(String username, String password)
        {
            Integer result = 0;

            if(username.equals(AppConstants.USER_ID) && password.equals(AppConstants.PASSWORD))
                result = AppConstants.USER_INPUT_OK;
            else if(username.equals(AppConstants.USER_ID) && !password.equals(AppConstants.PASSWORD))
                result = AppConstants.BAD_PASSWORD;
            else if(!username.equals(AppConstants.USER_ID) && password.equals(AppConstants.PASSWORD))
                result = AppConstants.BAD_USER_ID;
            else if(!username.equals(AppConstants.USER_ID) && !password.equals(AppConstants.PASSWORD))
                result =  AppConstants.BAD_USER_ID_AND_PASSWORD;

            return result;
        }
    }

    private void switchToUserHomeActivity(boolean alreadyLoggedIn)
    {
        if(!alreadyLoggedIn) //if this is first user login
            SavedSharedPreferences.alreadyLoggedIn.edit().putBoolean("alreadyLoggedIn", true).commit(); //save it for next times

        startActivity(new Intent(MainActivity.this, UserHomeActivity.class));
        MainActivity.this.finish();
    }
}


