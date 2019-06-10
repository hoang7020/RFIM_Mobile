package vn.com.rfim_mobile.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

import vn.com.rfim_mobile.R;

public class ScanningFragment extends DialogFragment {

    LottieAnimationView lavScanning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.fragment_scanning, container, false);
        lavScanning = view.findViewById(R.id.lav_scanning);
        lavScanning.setAnimation(R.raw.scan);
        lavScanning.playAnimation();
        lavScanning.loop(true);
        return view;
    }
}
