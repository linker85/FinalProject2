package info.androidhive.navigationdrawer.activity;

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

import java.util.List;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.User;
import info.androidhive.navigationdrawer.models.UserMock;
import info.androidhive.navigationdrawer.retrofit_helpers.LoginRetrofitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginTAG_";
    private TextView        emailSignInTxt;
    private TextView        passwordSignInTxt;
    private TextView        errorMessageSignIn;
    private String          defaultValue;
    private CheckBox        rememberMe;
    private TextInputLayout inputLayoutMail, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayoutMail      = (TextInputLayout) findViewById(R.id.input_layout_email_sign_in);
        inputLayoutPassword  = (TextInputLayout) findViewById(R.id.input_layout_password_sign_in);

        errorMessageSignIn   = (TextView) findViewById(R.id.sign_in_layout_error);
        emailSignInTxt       = (TextView) findViewById(R.id.input_email_sign_in);
        passwordSignInTxt    = (TextView) findViewById(R.id.input_password_sign_in);
        rememberMe           = (CheckBox) findViewById(R.id.remember);

        emailSignInTxt   .addTextChangedListener(new MyTextWatcherLogin(emailSignInTxt));
        passwordSignInTxt.addTextChangedListener(new MyTextWatcherLogin(passwordSignInTxt));

        SharedPreferences sharedPref = null;
        try {
            sharedPref   = getApplicationContext().
                    getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
            defaultValue = sharedPref.getString("rem", "0");
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

    public void doSignIn(View view) {
        boolean isValid = validateEmail() && validatePassword();
        Log.d(TAG, "doSignIn: ");
        if (isValid) {
            final Intent intent = new Intent(this, MainActivity.class);

            Observable<List<User>> resultGithubObservable = LoginRetrofitHelper.
                    Factory.createLogin("581e710d3e0000da02c08e10"); // user

            resultGithubObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<User>>() {

                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted: ");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onNext(List<User> users) {
                            /// Simulation of getting users from backend + from the database to simulate the registry of users
                            boolean found   = false;
                            String name = "";
                            String email    = "";
                            List<UserMock> userMocksList = UserMock.findWithQuery(UserMock.class, "SELECT * FROM USER_MOCK");
                            if (userMocksList != null && !userMocksList.isEmpty()) {
                                for (UserMock u : userMocksList) {
                                    User user = new User();
                                    user.setEmail(u.getEmail());
                                    user.setPassword(u.getPassword());
                                    user.setName(u.getName());
                                    users.add(user);
                                }
                            }
                            for (User user : users) {
                                if (emailSignInTxt.getText().toString().equalsIgnoreCase(user.getEmail()) &&
                                        passwordSignInTxt.getText().toString().equalsIgnoreCase(user.getPassword())) {
                                    found = true;
                                    name  = user.getName();
                                    email = user.getEmail();
                                    break;
                                } else {
                                    found = false;
                                }
                            }
                            //////////////////////////////////////////////////////////////////////////////////////////////////
                            if (found) {
                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                                // Get shared preferences from mock-backend
                                final SharedPreferences.Editor editor = sharedPref.edit();
                                Log.d(TAG, "email: " + email);
                                Log.d(TAG, "name: " + name);
                                editor.putString("email", email);
                                editor.putString("name", name);
                                if (rememberMe.isChecked()) {
                                    editor.putString("rem", "1");
                                    editor.putString("emailR", emailSignInTxt.getText().toString());
                                } else {
                                    editor.putString("rem", "0");
                                    editor.remove("emailR");
                                }
                                found = true;
                                editor.commit();
                                startActivity(intent);
                            } else {
                                errorMessageSignIn.setText("Invalid user or password");
                            }
                        }
                    });
        }
    }


}
