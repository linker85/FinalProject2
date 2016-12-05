package info.androidhive.myparkmeter.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.myparkmeter.R;
import info.androidhive.myparkmeter.models.Success;
import info.androidhive.myparkmeter.retrofit_helpers.SaveApiRetroFitHelper;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegPayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegPayFragment#newInstance} factory method to
 * createSave an instance of this fragment.
 */
public class RegPayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG        = "RegPayFragmentTAG_";
    private static final int MY_SCAN_REQUEST_CODE = 100;

    @BindView(R.id.input_cardV)
    public TextView inputCardV;
    @BindView(R.id.input_mmV)
    public TextView inputMMV;
    @BindView(R.id.input_yyyyV)
    public TextView inputYYYYV;
    @BindView(R.id.input_cvvV)
    public TextView inputCVVV;
    @BindView(R.id.input_layout_card)
    public TextInputLayout inputLayoutCard;
    @BindView(R.id.input_layout_mm)
    public TextInputLayout inputLayoutMM;
    @BindView(R.id.input_layout_yyyy)
    public TextInputLayout inputLayoutYYYY;
    @BindView(R.id.input_layout_cvv)
    public TextInputLayout inputLayoutCVV;
    @BindView(R.id.btn_register_card)
    public Button btn_register_card;
    @BindView(R.id.btn_scan_card)
    public ImageView btn_scan_card;
    @BindView(R.id.id_pay_status)
    public TextView payStatus;

    private CreditCard scanResult;
    private boolean isAlreadyRegistered = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegPayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to createSave a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegPayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegPayFragment newInstance(String param1, String param2) {
        RegPayFragment fragment = new RegPayFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_regpay, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getIsAlreadyRegisteredObservable()
                .subscribeOn(Schedulers.io()) // does the work on the io thread
                .observeOn(AndroidSchedulers.mainThread()) // returns result to the main thread
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        payStatus.setText("An error ocurred while trying to register your car.");
                        payStatus.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        isAlreadyRegistered = aBoolean;
                        if (isAlreadyRegistered) {
                            payStatus.setText("Enter new card number to update payment method.");
                            payStatus.setVisibility(View.VISIBLE);
                        } else {
                            payStatus.setText("");
                            payStatus.setVisibility(View.INVISIBLE);
                        }
                    }
                });


        btn_register_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btn_scan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);

                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false

                // hides the manual entry button
                // if set, developers should provide their own manual entry mechanism in the app
                scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

                // matches the theme of your application
                scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            inputCardV.setText(scanResult.getRedactedCardNumber());

            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );

            if (scanResult.isExpiryValid()) {
                inputYYYYV.setText(String.valueOf(scanResult.expiryYear));
                inputMMV.setText(String.valueOf(scanResult.expiryMonth));
            }

            if (scanResult.cvv != null) {
                // Never log or display a CVV
                inputCVVV.setText(scanResult.cvv);
            }
        } else {
            payStatus.setText("Scan was canceled.");
        }

    }

    private Observable<Boolean> getIsAlreadyRegisteredObservable() {
        //return Observable.just(getString());
        return Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(isAlreadyRegistered());
            }
        });
    }

    private boolean isAlreadyRegistered() {
        SharedPreferences sharedPref = null;
        int card = 0;
        try {
            sharedPref = getActivity().
                    getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
            card = sharedPref.getInt("card", 0);
        } catch (Exception e) {

        }
        isAlreadyRegistered = (card > 0);
        return isAlreadyRegistered;
    }

    private ProgressDialog progressDialog;
    /**
     * Validating form
     */
    private void submitForm() {
        progressDialog = new ProgressDialog(getActivity());

        SharedPreferences sharedPref = null;
        String email = "";
        try {
            sharedPref = getActivity().
                    getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
            email = sharedPref.getString("email", "");
        } catch (Exception e) {
            Log.e(TAG, "submitForm: " + e.getMessage());
        }

        Observable<Success> resultSaveApiObservable = SaveApiRetroFitHelper.
                Factory.createSaveCard(email); // user
        resultSaveApiObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Success>() {
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
                        payStatus.setText("Your card couldn´t be registered.");
                        payStatus.setVisibility(View.VISIBLE);
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
                    public void onNext(Success success) {
                        Log.d(TAG, "onNext: " + success.isSuccess());
                        if (success.isSuccess()) {
                            payStatus.setText("");
                            payStatus.setVisibility(View.INVISIBLE);
                            inputCardV.setText("");
                            inputMMV.setText("");
                            inputYYYYV.setText("");
                            inputCVVV.setText("");
                            Toast.makeText(getView().getContext(), "Your card was registered", Toast.LENGTH_SHORT).show();
                        } else {
                            payStatus.setText("Your card couldn´t be registered.");
                            payStatus.setVisibility(View.VISIBLE);
                        }
                        try {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } catch(Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });

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
