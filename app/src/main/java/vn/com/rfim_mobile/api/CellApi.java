package vn.com.rfim_mobile.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URL;

import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.utils.ApiUtil;
import vn.com.rfim_mobile.utils.UrlUtil;

public class CellApi {
    public static String TAG = FloorApi.class.getSimpleName();

    private ApiUtil mApiUtil;
    private OnTaskCompleted listener;

    public CellApi(OnTaskCompleted listener) {
        this.listener = listener;
    }

    //Get all shelf by using floor id
    public void getCellsByFloorId(String id) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.all_cells) + id);
        mApiUtil.setType(Constant.GET_ALL_CELLS_BY_FLOOR_ID);
        mApiUtil.execute(url);
    }

    //Map cell with cell's rfid tag
    public void registerCell(String cellId, String cellRfid) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.register_cell));
        mApiUtil.setType(Constant.REGISTER_CELL);
        mApiUtil.setMethod("POST");
        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("cellId", cellId);
        obj.addProperty("cellRfid", cellRfid);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }
}
