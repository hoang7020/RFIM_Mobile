package vn.com.rfim_mobile.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import vn.com.rfim_mobile.LoginActivity;
import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;
import vn.com.rfim_mobile.utils.ApiUtil;
import vn.com.rfim_mobile.utils.UrlUtil;

import java.net.URL;
import java.sql.Date;
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

    //Check cell is empty
    public void checkCellisEmpty(String rfid) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.check_cell_is_empty) + rfid);
        mApiUtil.setType(Constant.CHECK_CELL_IS_EMTPY);
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
    public void registerPackageAndBox(String packagedId, String invoiceId, String productId,
                                      int invoiceStatus, List<String> productRfids, long date) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.register_package_and_box));
        mApiUtil.setType(Constant.REGISTER_PACKAGE_AND_BOX);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packagedId);
        obj.addProperty("invoiceId", invoiceId);
        obj.addProperty("productId", productId);
        obj.addProperty("invoiceStatus", invoiceStatus);
        obj.addProperty("date", date);
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
        mApiUtil.setType(Constant.GET_PACKAGE_BY_PACKAGE_RFID);
        mApiUtil.execute(url);
    }

    //Stock in package
    //Update pakage cell id and date
    public void stockInPackage(String packageRfid, String cellId, long date) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.stock_in_package));
        mApiUtil.setType(Constant.STOCK_IN_PACKAGE);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("packageRfid", packageRfid);
        obj.addProperty("cellId", cellId);
        obj.addProperty("date", date);
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
    public void stockOutBox(List<String> boxRfids, String invoiceId) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.stock_out_boxes));
        mApiUtil.setType(Constant.STOCK_OUT_BOXES);
        mApiUtil.setMethod("POST");
        JsonObject obj = new JsonObject();
        obj.addProperty("invoiceId", invoiceId);
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

    //Save stocktake history
    public void saveStocktakeHistory(int userId, String productId,
                                     int quantity, long date, String description) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.save_stocktake_history));
        mApiUtil.setMethod("POST");
        mApiUtil.setType(Constant.SAVE_STOCKTAKE_HISTORY);
        JsonObject obj = new JsonObject();
        obj.addProperty("userId", userId);
        obj.addProperty("productId", productId);
        obj.addProperty("quantity", quantity);
        obj.addProperty("date", date);
        obj.addProperty("description", description);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Login
    public void login(String username, String password) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(LoginActivity.context.getString(R.string.host) +
                LoginActivity.context.getString(R.string.login));
        mApiUtil.setMethod("POST");
        mApiUtil.setType(Constant.LOGIN);
        JsonObject obj = new JsonObject();
        obj.addProperty("username", username);
        obj.addProperty("password", password);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }

    //Get Issues Invoice;
    public void getIssuseInvoice() {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_issue_invoice));
        mApiUtil.setType(Constant.GET_ISSUE_INVOICE);
        mApiUtil.execute(url);
    }

    //Get Receipt Invoice;
    public void getReceiptInvoice() {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.get_receipt_invoice));
        mApiUtil.setType(Constant.GET_RECEIPT_INVOICE);
        mApiUtil.execute(url);
    }

    //Sort issue invoice using disjkstra algorithm
    public void sortIssueInvoice(List<InvoiceInfoItem> invoices) {
        this.mApiUtil = new ApiUtil(listener);
        URL url = UrlUtil.getURL(MainActivity.context.getString(R.string.host) +
                MainActivity.context.getString(R.string.dijkstra));
        mApiUtil.setMethod("POST");
        mApiUtil.setType(Constant.DIJKSTRA);
        JsonObject obj = new JsonObject();
        JsonArray array = (JsonArray) gson.toJsonTree(invoices);
        obj.add("invoiceInfoItems", array);
        mApiUtil.setParam(obj);
        mApiUtil.execute(url);
    }
}
