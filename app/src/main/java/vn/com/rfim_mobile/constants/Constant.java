package vn.com.rfim_mobile.constants;

import java.util.UUID;

public class Constant {
//    public static final String address = "98:D3:31:FD:5C:49";
    public static final String address = "98:D3:31:FD:68:18";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //Scan Type
    public static final int SCAN_SHELF_RFID = 1;
    public static final int SCAN_CELL_RFID = 2;
    public static final int SCAN_PACKAGE_RFID = 3;
    public static final int SCAN_BOX_RFID = 4;
    public static final int SCAN_STOCKTAKE_RFID = 5;

    //Callback Type
    public static final int GET_ALL_SHELVES = 1;
    public static final int GET_ALL_FLOORS_BY_SHELF_ID = 2;
    public static final int GET_ALL_CELLS_BY_FLOOR_ID = 3;
    public static final int REGISTER_CELL = 4;
    public static final int GET_ALL_PRODUCTS = 5;
    public static final int REGISTER_PACKAGE_AND_BOX = 6;
    public static final int GET_FLOOR_BY_CELL_ID = 7;
    public static final int GET_SHELF_BY_FLOOR_ID = 8;
    public static final int GET_CELL_BY_CELL_RFID = 9;
    public static final int STOCK_IN_PACKAGE = 10;
    public static final int GET_PRODUCT_BY_BOX_ID = 11;
    public static final int STOCK_OUT_BOXES = 12;
    public static final int TRANSFER_PACKAGE = 13;
    public static final int GET_PACKAGE_BY_PACKAGE_RFID = 14;
    public static final int TRANSFER_BOXES = 15;
    public static final int GET_BOX_RFIDS_BY_PRODUCT_ID = 16;
    public static final int GET_ALL_STOCKTAKE_TYPE = 17;
    public static final int LOGIN = 18;
    public static final int SAVE_STOCKTAKE_HISTORY = 19;
    public static final int GET_ISSUE_INVOICE = 20;
    public static final int GET_RECEIPT_INVOICE = 21;
    public static final int DIJKSTRA = 22;
    public static final int CHECK_CELL_IS_EMTPY = 23;

    //Api Response Code
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
}
