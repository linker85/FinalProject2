package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.WizardFragment;
import org.codepond.wizardroid.persistence.ContextVariable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.other.UpdateStepperEvent;

public class HomeFragment extends WizardFragment {

    private static final String TAG = "HomeFragmentTAG_";
    private Button nextButton;
    private Button previousButton;
    /**
     * Tell WizarDroid that these are context variables.
     * These values will be automatically bound to any field annotated with {@link ContextVariable}.
     * NOTE: Context Variable names are unique and therefore must
     * have the same name wherever you wish to use them.
     */
    @ContextVariable
    private boolean isCheckout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    // This method will be called when a HelloWorldEvent is posted
    @Subscribe
    public void onEvent(UpdateStepperEvent event){
        // your implementation
        Log.d("TAG", "onEvent: ");
        nextButton.setEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        nextButton = (Button) view.findViewById(R.id.wizard_next_button);

        if (isCheckout) {
            view.findViewById(R.id.wizard_button_bar).setVisibility(View.INVISIBLE);
        }

        nextButton.setEnabled(false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wizard.goNext();
                updateWizardControls();
            }
        });
        previousButton = (Button) view.findViewById(R.id.wizard_previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wizard.goBack();
                updateWizardControls();
            }
        });
        return view;
    }

    //You must override this method and createSave a wizard flow by
    //using WizardFlow.Builder as shown in this example
    @Override
    public WizardFlow onSetup() {
        isCheckout = getArguments().getBoolean("isCheckout");
        bindDataFields(isCheckout);
        Log.d("TAG", "onSetup: " + isCheckout);
        if (!isCheckout) {
            return new WizardFlow.Builder()
                    .addStep(TutorialStep1.class)           //Add your steps in the order you want them
                    .addStep(TutorialStep2.class)           //to appear and eventually call createSave()
                    .create();
        } else {
            return new WizardFlow.Builder()
                    .addStep(TutorialStep1.class)           //Add your steps in the order you want them
                    .create();
        }
        //to createSave the wizard flow.
    }

    private void bindDataFields(boolean isCheckout) {
        //The values of these fields will be automatically stored in the wizard context
        //and will be populated in the next steps only if the same field names are used.
        this.isCheckout = isCheckout;
    }

    @Override
    public void onStepChanged() {
        if (wizard.isFirstStep()) {
            // Remove preferences from shared
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    "my_park_meter_pref", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove("checkin_temp");
            editor.commit();
        }
        nextButton.setEnabled(false);
    }

    /**
     * Updates the UI according to current step position
     */
    private void updateWizardControls() {
        previousButton.setEnabled(!wizard.isFirstStep());
        if (wizard.isLastStep()) {
            nextButton.setText("Next");
        } else {
            wizard.dispose();
        }
    }

    /**
     * Triggered when the wizard is completed.
     * Overriding this method is optional.
     */
    @Override
    public void onWizardComplete() {
        // Remove preferences from shared
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "my_park_meter_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("checkin_temp");
        editor.commit();
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

}