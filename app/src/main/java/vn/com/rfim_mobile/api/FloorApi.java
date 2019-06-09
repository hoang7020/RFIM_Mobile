package vn.com.rfim_mobile.api;

import java.net.URL;

import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.utils.ApiUtil;
import vn.com.rfim_mobile.utils.UrlUtil;

public class FloorApi {

    public static String TAG = FloorApi.class.getSimpleName();

    private ApiUtil mApiUtil;
    private OnTaskCompleted listener;

    public FloorApi(OnTaskCompleted listener) {
        this.listener = listener;
    }

    //Get all floor by using shelf id
    public void getFloorsByShelfId(String id) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.all_floors) + id);
        mApiUtil.setType(Constant.GET_ALL_FLOORS_BY_SHELF_ID);
        mApiUtil.execute(url);
    }
}
