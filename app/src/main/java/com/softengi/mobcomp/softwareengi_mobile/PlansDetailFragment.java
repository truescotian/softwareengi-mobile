package com.softengi.mobcomp.softwareengi_mobile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class PlansDetailFragment extends Fragment {

    public interface onPlansDetail {
        void deletePlan(String planId);
        void updatePlan(EditText title, EditText requiredSteps, String planId);
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PLAN_TITLE = "plan_title";
    private static final String ARG_PLAN_REQUIRED_STEPS = "plan_required_steps";
    private static final String ARG_PLAN_ID = "plan_id";

    private String mPlanTitle;
    private String mPlanRequiredSteps;
    private String mPlanId;
    private EditText etPlanDetailTitle;
    private EditText etPlanDetailRequiredSteps;
    private Button btnPlanDetailDelete;
    private Button btnPlanDetailUpdate;

    onPlansDetail plansDetail;

    public PlansDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlanTitle         = getArguments().getString(ARG_PLAN_TITLE);
            mPlanRequiredSteps = getArguments().getString(ARG_PLAN_REQUIRED_STEPS);
            mPlanId            = getArguments().getString(ARG_PLAN_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plans_detail, container, false);
        etPlanDetailTitle = v.findViewById(R.id.etPlanDetailTitle);
        etPlanDetailRequiredSteps = v.findViewById(R.id.etPlanDetailRequiredSteps);
        btnPlanDetailDelete = v.findViewById(R.id.btnDetailDelete);
        btnPlanDetailUpdate = v.findViewById(R.id.btnDetailUpdate);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            plansDetail = (PlansDetailFragment.onPlansDetail) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        etPlanDetailTitle.setText(mPlanTitle);
        etPlanDetailRequiredSteps.setText(mPlanRequiredSteps);

        btnPlanDetailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plansDetail.updatePlan(etPlanDetailTitle, etPlanDetailRequiredSteps, mPlanId);
            }
        });

        btnPlanDetailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plansDetail.deletePlan(mPlanId);
            }
        });
    }

}