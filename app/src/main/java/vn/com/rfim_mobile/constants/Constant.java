package vn.com.rfim_mobile.constants;

import java.util.UUID;

public class Constant {
    public static final String address = "98:D3:31:FD:5C:49";
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    //Scan Type
    public static final int SCAN_SHELF_RFID = 1;
    public static final int SCAN_PACKAGE_RFID = 2;
    public static final int SCAN_BOX_RFID = 3;

    //Callback Type
    public static final int GET_ALL_SHELVES = 1;
    public static final int GET_ALL_FLOORS_BY_SHELF_ID = 2;
    public static final int GET_ALL_CELLS_BY_FLOOR_ID = 3;
    public static final int REGISTER_CELL = 4;

    //Api Response Code
    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
}
