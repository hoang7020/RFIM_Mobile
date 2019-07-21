package vn.com.rfim_mobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ExpandableListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.AlgorithmResultAdapter;
import vn.com.rfim_mobile.models.json.AlgorithmResult;

import java.util.List;

public class AlgorithmResultFragment extends Fragment {

    public static final String TAG = AlgorithmResultFragment.class.getSimpleName();

    private ExpandableListView eplvAlgorithmResult;
    private Toolbar mToolbar;
    private List<AlgorithmResult> mAlgorithmResults;
    private AlgorithmResultAdapter mAlgorithmResultAdapter;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_algorithm_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initView();

        mToolbar.inflateMenu(R.menu.exit_button_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(AlgorithmResultFragment.this).commit();
                IssueInvoiceFragmentV2.mOntuOnTurnBackListener.onTurnBack();
                return true;
            }
        });

        String result = getArguments().getString("LIST_ALGORITHM_RESULT");
        mAlgorithmResults = gson.fromJson(result, new TypeToken<List<AlgorithmResult>>(){}.getType());
        mAlgorithmResultAdapter = new AlgorithmResultAdapter(getContext(), mAlgorithmResults);
        eplvAlgorithmResult.setAdapter(mAlgorithmResultAdapter);
        Log.e(TAG, "onViewCreated: " + result);
    }

    private void initView() {
        eplvAlgorithmResult = getView().findViewById(R.id.eplv_algorithm_result);
        mToolbar = getView().findViewById(R.id.tb_algorithm);
        gson = new Gson();
    }
}
