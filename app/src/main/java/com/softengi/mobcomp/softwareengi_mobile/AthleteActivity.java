package com.softengi.mobcomp.softwareengi_mobile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.softengi.mobcomp.softwareengi_mobile.ProfileFragment.onUpdateProfile;
import com.softengi.mobcomp.softwareengi_mobile.PlansFragment.onPlansFragmentLoad;
import com.softengi.mobcomp.softwareengi_mobile.CreatePlanFragment.onCreateFragmentLoad;
import com.softengi.mobcomp.softwareengi_mobile.PlansDetailFragment.onPlansDetail;
import com.softengi.mobcomp.softwareengi_mobile.Controllers.PlanController;
import com.softengi.mobcomp.softwareengi_mobile.Utils.DetailPlanParser;
import com.softengi.mobcomp.softwareengi_mobile.Utils.HashMapAdapter;
import com.softengi.mobcomp.softwareengi_mobile.Utils.ListOfPlanParser;
import com.softengi.mobcomp.softwareengi_mobile.Utils.SharedPrefManager;
import com.softengi.mobcomp.softwareengi_mobile.Utils.SuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AthleteActivity extends AppCompatActivity implements onPlansFragmentLoad,
        onCreateFragmentLoad, onUpdateProfile, onPlansDetail {

    private BottomNavigationView mAthleteNav;
    private FrameLayout mAthleteFrame;
    private PlansFragment mPlansFragment;
    private ProfileFragment mProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete);

        mAthleteFrame   = findViewById(R.id.athlete_frame);
        mAthleteNav     = findViewById(R.id.athlete_nav);
        mPlansFragment   = new PlansFragment();
        mProfileFragment = new ProfileFragment();

        setFragment(mProfileFragment);

        mAthleteNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {

                    case R.id.nav_plans :
                        setFragment(mPlansFragment);
                        return true;

                    case R.id.nav_profile :
                        setFragment(mProfileFragment);
                        return true;

                    case R.id.nav_teams :
                        return true;

                    default:
                        return false;

                }

            }
        });

    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.athlete_frame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void loadPlansAdapter(final HashMapAdapter hmAdapter, final HashMap<Integer, String> hmData) {
        PlanController.getListOfPlans(
                getApplication(),
                SharedPrefManager.getInstance(getApplicationContext()).getUsername(),
                new ListOfPlanParser() {
                    @Override
                    public void onSuccessResponse(JSONArray data) {
                        try {

                            hmData.clear();

                            for(int i = 0; i < data.length(); i++) {
                                JSONObject jsonObj = data.getJSONObject(i);
                                data.put(jsonObj.getInt("id"), jsonObj.getString("title"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hmAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onCreatePlan() {
        CreatePlanFragment fragment = new CreatePlanFragment();
        setFragment(fragment);
    }

    @Override
    public void onPlanDetail(final Integer planId) {
        PlanController.getPlan(getApplicationContext(),
                planId,
                new DetailPlanParser() {
                    @Override
                    public void onSuccessResponse(JSONObject data) throws JSONException {
                        Bundle args = new Bundle();
                        args.putInt("planId", planId);
                        PlansDetailFragment fragment = new PlansDetailFragment();
                        fragment.setArguments(args);
                        setFragment(fragment);

                    }
                });

    }

    @Override
    public void onSubmitPlan() {

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        PlanController.postCreatePlans(getApplicationContext(),
                (EditText) findViewById(R.id.etPlanCreateTitle),
                (EditText) findViewById(R.id.etPlanCreateRequiredSteps),
                new SuccessListener() {
                    @Override
                    public void successful() {

                        PlansFragment fragment = new PlansFragment();
                        setFragment(fragment);

                    }
                }
        );

    }

    @Override
    public void updateEmail() {

    }

    @Override
    public void updateLanguage() {

    }

    @Override
    public void updateCoach() {

    }

    @Override
    public void logout() {
        SharedPrefManager.getInstance(getApplicationContext()).logout();
        AthleteActivity.this.finish();
        Intent i = new Intent(this,MainActivity.class);
        // clear the backstack
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void deletePlan(int planId) {
        PlanController.postDelete(getApplicationContext(), planId, new SuccessListener() {
            @Override
            public void successful() {
                PlansFragment fragment = new PlansFragment();
                setFragment(fragment);
            }
        });
    }

    @Override
    public void updatePlan(EditText title, EditText requiredSteps, int planId) {
        PlanController.postUpdate(getApplicationContext(), title, requiredSteps, planId, new SuccessListener() {
            @Override
            public void successful() {
                Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
