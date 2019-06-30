package vn.com.rfim_mobile.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.utils.ApiUtil;
import vn.com.rfim_mobile.utils.UrlUtil;

import java.net.URL;
import java.util.List;

public class RFIMApi {

    public static final String TAG = RFIMApi.class.getSimpleName();

    private ApiUtil mApiUtil;
    private OnTaskCompleted listener;
    private Gson gson;

    public RFIMApi(OnTaskCompleted listener) {
        this.listener = listener;
        gson = new Gson();
    }

    //Get all shelf by using floor id
    public void getCellsByFloorId(String id) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.cell_by_floor_id) + id);
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

    //Get cell by cell rfid
    public void getCellByCellRfid(String rfid) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.cell_by_cell_rfid) + rfid);
        mApiUtil.setType(Constant.GET_CELL_BY_CELL_RFID);
        mApiUtil.execute(url);
    }

    //Get all floor by using shelf id
    public void getFloorsByShelfId(String id) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.floors) + id);
        mApiUtil.setType(Constant.GET_ALL_FLOORS_BY_SHELF_ID);
        mApiUtil.execute(url);
    }

    //Get floors by using cell id;
    public void getFloorByCellId(String id) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.floors_by_cell_id) + id);
        mApiUtil.setType(Constant.GET_FLOOR_BY_CELL_ID);
        mApiUtil.execute(url);
    }

    //Get all shelves
    public void getAllShelves() {
        mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.shelves));
        mApiUtil.setType(Constant.GET_ALL_SHELVES);
        mApiUtil.execute(url);
    }

    //Get shelf by floor id
    public void getShelfByFloorId(String id) {
        mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.shelf_by_floor_id) + id);
        mApiUtil.setType(Constant.GET_SHELF_BY_FLOOR_ID);
        mApiUtil.execute(url);
    }

    //Register box to package
    //Map pakage with product id
    public void registerPackageAndBox(String packagedId, String productId, List<String> productRfids) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.register_package_and_box));
        mApiUtil.setType(Constant.REGISTER_PACKAGE_AND_BOX);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packagedId);
        obj.addProperty("productId", productId);
        JsonArray array = (JsonArray) gson.toJsonTree(productRfids);
        obj.add("boxRfids", array);
        Log.e(TAG, "registerPackageAndBox: " + obj);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Check cell is registered
//    public void isRegisteredCell(String cellRfid) {
//        this.mApiUtil = new ApiUtil(listener);
//        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
//                MainActivity.context.getString(R.string.check_cell_is_registered) + cellRfid);
//        mApiUtil.setType(Constant.CHECK_CELL_IS_REGISTERED);
//        mApiUtil.execute(url);
//    }

    //Get package by package rfid
    public void getPackageByPackageRfid(String packageRfid) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_package_by_package_rfid) + packageRfid);
        mApiUtil.setType(Constant.CHECK_PACKAGE_IS_REGISTERED);
        mApiUtil.execute(url);
    }

    //Stock in package
    //Update pakage cell id
    public void stockInPackage(String packageRfid, String cellId) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.stock_in_package));
        mApiUtil.setType(Constant.STOCK_IN_PACKAGE);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packageRfid);
        obj.addProperty("cellId", cellId);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Transfer boxes
    //Update package rfid of box
    public void transferBoxes(String packageRfid, List<String> boxRfids) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.transfer_boxes));
        mApiUtil.setType(Constant.TRANSFER_BOXES);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packageRfid);
        JsonArray array = (JsonArray) gson.toJsonTree(boxRfids);
        obj.add("boxRfids", array);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Transfer package
    //Update cell rfid of package
    public void transferPackage(String packageRfid, String cellId) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.transfer_package));
        mApiUtil.setType(Constant.TRANSFER_PACKAGE);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packageRfid);
        obj.addProperty("cellId", cellId);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Stock out box
    public void stockOutBox(List<String> boxRfids) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.stock_out_boxes));
        mApiUtil.setType(Constant.STOCK_OUT_BOXES);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        JsonArray array = (JsonArray) gson.toJsonTree(boxRfids);
        obj.add("boxRfids", array);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Get all products
    public void getAllProduct() {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_all_products));
        mApiUtil.setType(Constant.GET_ALL_PRODUCTS);
        mApiUtil.execute(url);
    }

    //Get product by box rfid
    public void getProductByBoxRfid(String rfid) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_product_by_box_rfid) + rfid);
        mApiUtil.setType(Constant.GET_PRODUCT_BY_BOX_ID);
        mApiUtil.execute(url);
    }

    //Get box rfids by product id
    public void getBoxRfidsByProductId(String productId) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_box_rfid_by_product_id) + productId);
        mApiUtil.setType(Constant.GET_BOX_RFIDS_BY_PRODUCT_ID);
        mApiUtil.execute(url);
    }

    //Get all stocktake type
    public void getAllStocktakeType() {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.stocktaketypes));
        mApiUtil.setType(Constant.GET_ALL_STOCKTAKE_TYPE);
        mApiUtil.execute(url);
    }
}
