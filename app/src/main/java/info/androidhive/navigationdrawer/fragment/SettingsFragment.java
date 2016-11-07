package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.activity.LoginActivity;
import info.androidhive.navigationdrawer.models.Success;
import info.androidhive.navigationdrawer.models.UserMock;
import info.androidhive.navigationdrawer.retrofit_helpers.SaveApiRetroFitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * createSave an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SettingsFragmentTAG_";

    private EditText inputName, inputEmail, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;
    private Button btn_register;
    private TextView settingStatus;
    private boolean isSignUp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to createSave a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputLayoutName     = (TextInputLayout) getView().findViewById(R.id.input_layout_name);
        inputLayoutEmail    = (TextInputLayout) getView().findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) getView().findViewById(R.id.input_layout_password);
        inputName           = (EditText) getView().findViewById(R.id.input_name);
        inputEmail          = (EditText) getView().findViewById(R.id.input_email);
        inputPassword       = (EditText) getView().findViewById(R.id.input_password);
        btn_register        = (Button) getView().findViewById(R.id.btn_register);
        settingStatus       = (TextView) getView().findViewById(R.id.id_settings_status);

        settingStatus.setVisibility(getView().INVISIBLE);

        if (getArguments() != null) {
            isSignUp = getArguments().getBoolean("isSignUp");
        }

        if (!isSignUp) {
            SharedPreferences sharedPref = null;
            try {
                sharedPref   = getActivity().getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                String email = sharedPref.getString("email", "");
                String name  = sharedPref.getString("name", "");
                if (email != null) {
                    inputEmail.setText(email);
                }
                if (name != null) {
                    inputName.setText(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        inputName    .addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail   .addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        Observable<Success> resultSaveApiObservable = SaveApiRetroFitHelper.
                Factory.createSaveUser("581deb6b0f0000702a02daee"); // user
        resultSaveApiObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Success>() {

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        settingStatus.setText("Your USER couldn´t be registered.");
                        settingStatus.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Success success) {
                        ////////////// Simulation of saving the user settings in the backend
                        boolean successBack = false;
                        String  message     = "Your user couldn´t be registered.";
                        List<UserMock> userMocksList = UserMock.findWithQuery(UserMock.class, "SELECT * FROM USER_MOCK WHERE EMAIL = ?", inputEmail.getText().toString());
                        if (isSignUp) {
                            if (userMocksList != null && !userMocksList.isEmpty()) { // Update user
                                message     = "The email that you are trying to registered is already being used.";
                                successBack = false;
                            } else {
                                // Add new user
                                UserMock userMock = new UserMock();
                                userMock.setEmail(inputEmail.getText().toString());
                                userMock.setPassword(inputPassword.getText().toString());
                                userMock.setName(inputName.getText().toString());
                                userMock.save();
                                successBack = true;
                            }
                        } else {
                            if (userMocksList != null && !userMocksList.isEmpty()) { // Update user
                                UserMock userMock = UserMock.findById(UserMock.class, userMocksList.get(0).getId());
                                userMock.setEmail(inputEmail.getText().toString());
                                userMock.setPassword(inputPassword.getText().toString());
                                userMock.setName(inputName.getText().toString());
                                userMock.save();
                                successBack = true;
                            } else { // Add new user
                                UserMock userMock = new UserMock();
                                userMock.setEmail(inputEmail.getText().toString());
                                userMock.setPassword(inputPassword.getText().toString());
                                userMock.setName(inputName.getText().toString());
                                userMock.save();
                                successBack = true;
                            }
                            settingStatus.setVisibility(View.INVISIBLE);
                        }
                        ////////////////////////////////////////////////////////////////////
                        if (success.getResult() == 1 && successBack) {
                            settingStatus.setText("");
                            settingStatus.setVisibility(View.INVISIBLE);
                            if (isSignUp) {
                                Toast.makeText(getView().getContext(), "Your user was registered", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                                final SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("email", inputEmail.getText().toString());
                                editor.putString("name", inputName.getText().toString());
                                editor.commit();
                                Toast.makeText(getView().getContext(), "Your user settings have been updated.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            settingStatus.setText(message);
                            settingStatus.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
