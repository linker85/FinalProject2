package info.androidhive.navigationdrawer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.LoginResponse;
import info.androidhive.navigationdrawer.models.User;
import info.androidhive.navigationdrawer.retrofit_helpers.LoginRetrofitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginTAG_";
    @BindView(R.id.input_email_sign_in)
    public TextView        emailSignInTxt;
    @BindView(R.id.input_password_sign_in)
    public TextView        passwordSignInTxt;
    @BindView(R.id.sign_in_layout_error)
    public TextView        errorMessageSignIn;
    public String          defaultValue;
    @BindView(R.id.remember)
    public CheckBox        rememberMe;
    @BindView(R.id.input_layout_email_sign_in)
    public TextInputLayout inputLayoutMail;
    @BindView(R.id.input_layout_password_sign_in)
    public TextInputLayout inputLayoutPassword;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        emailSignInTxt   .addTextChangedListener(new MyTextWatcherLogin(emailSignInTxt));
        passwordSignInTxt.addTextChangedListener(new MyTextWatcherLogin(passwordSignInTxt));

        SharedPreferences sharedPref = null;
        try {
            sharedPref   = getApplicationContext().
                    getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
            defaultValue = sharedPref.getString("rem", "0");
            userId       = sharedPref.getString("userId", "");
            if (defaultValue != null && defaultValue.equals("1")) {
                // Do the login
                emailSignInTxt.setText(sharedPref.getString("emailR", ""));
                rememberMe.setChecked(true);
            } else {
                emailSignInTxt.setText("");
                rememberMe.setChecked(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateEmail() {
        String email = emailSignInTxt.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutMail.setError(getString(R.string.err_msg_email));
            requestFocus(emailSignInTxt);
            return false;
        } else {
            inputLayoutMail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (passwordSignInTxt.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordSignInTxt);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void doForgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private class MyTextWatcherLogin implements TextWatcher {

        private View view;

        private MyTextWatcherLogin(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    public void doSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private ProgressDialog progressDialog;

    public void doSignIn(View view) {
        boolean isValid = validateEmail() && validatePassword();
        Log.d(TAG, "doSignIn: ");
        progressDialog = new ProgressDialog(this);

        if (isValid) {
            final Intent intent = new Intent(this, MainActivity.class);

            User user = new User();
            user.setEmail(emailSignInTxt.getText().toString());
            user.setPassword(passwordSignInTxt.getText().toString());

            Observable<LoginResponse> resultGithubObservable = LoginRetrofitHelper.
                    Factory.createLogin(user.getEmail(), user.getPassword(), userId); // user

            resultGithubObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<LoginResponse>() {
                        @Override
                        public void onStart() {
                            Log.d(TAG, "onStart: ");
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                        }

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted: ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: " + e.getMessage());
                            try {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            } catch(Exception exception) {
                                exception.printStackTrace();
                            }
                        }

                        @Override
                        public void onNext(LoginResponse response) {
                            Log.d(TAG, "onNext: ");
                            try {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            } catch(Exception exception) {
                                exception.printStackTrace();
                            }
                            if (response != null && response.getSuccess()) {
                                SharedPreferences sharedPref = getApplicationContext().
                                        getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                                // Get shared preferences from mock-backend
                                final SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("email", response.getUser().getEmail());
                                editor.putString("name", response.getUser().getName());
                                editor.putString("plate", response.getUser().getPlate());
                                editor.putInt("card", response.getUser().getCard());
                                editor.putString("userId", response.getUser().getUserid());
                                if (rememberMe.isChecked()) {
                                    editor.putString("rem", "1");
                                    editor.putString("emailR", emailSignInTxt.getText().toString());
                                } else {
                                    editor.putString("rem", "0");
                                    editor.remove("emailR");
                                }
                                editor.commit();
                                try {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                } catch(Exception exception) {
                                    exception.printStackTrace();
                                }
                                startActivity(intent);
                            } else {
                                errorMessageSignIn.setText("Invalid user or password");
                                try{
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                }catch(Exception exception){
                                    exception.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

}