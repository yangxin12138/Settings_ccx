package com.twd.settingsccx.repository;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.twd.settingsccx.R;

public class BaseFragment extends Fragment {
    protected final String TAG = getClass().toString();
    protected FragmentActivity mActivity;

    protected void initTitle(View view, int i) {
        if (view == null) {
            return;
        }
        TextView titleView = (TextView) view.findViewById(R.id.titleTV);
        if (titleView == null) {
            return;
        }
        titleView.setText(i);
        //titleView.setOnClickListener(new BaseFragment..ExternalSyntheticLambda0(this));
    }

    protected void initTitle(View view, String str) {
        if (view == null) {
            return;
        }
        TextView titleView = (TextView) view.findViewById(R.id.titleTV);
        if (titleView == null) {
            return;
        }
        titleView.setText(str);
        //titleView.setOnClickListener(new BaseFragment..ExternalSyntheticLambda1(this));
    }

    @Override
    public void onAttach(Context mContext) {
        Log.d(TAG, " onAttach  ......");
        super.onAttach(mContext);
        mActivity = ((FragmentActivity) mContext);
    }
}
