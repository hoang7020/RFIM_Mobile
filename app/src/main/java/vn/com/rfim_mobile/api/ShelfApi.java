package vn.com.rfim_mobile.api;

import android.app.Activity;
import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.utils.ApiUtil;
import vn.com.rfim_mobile.utils.UrlUtil;

public class ShelfApi {

    public static final String TAG = ShelfApi.class.getSimpleName();

    private ApiUtil mApiUtil;
    private OnTaskCompleted listener;

    public ShelfApi(OnTaskCompleted listener) {
        this.listener = listener;
    }

    public void getAllShelves() {
        mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.all_shelves));
        mApiUtil.setType(Constant.GET_ALL_SHELVES);
        mApiUtil.execute(url);
    }


}
