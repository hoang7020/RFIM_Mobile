package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ObjectResult {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("shelves")
        @Expose
        private List<Shelf> shelves = null;

        @SerializedName("floors")
        @Expose
        private List<Floor> floors = null;

        @SerializedName("cells")
        @Expose
        private List<Cell> cells = null;

        public List<Shelf> getShelves() {
            return shelves;
        }

        public void setShelves(List<Shelf> shelves) {
            this.shelves = shelves;
        }

        public List<Floor> getFloors() {
            return floors;
        }

        public void setFloors(List<Floor> floors) {
            this.floors = floors;
        }


        public List<Cell> getCells() {
            return cells;
        }

        public void setCells(List<Cell> cells) {
            this.cells = cells;
        }
    }

    public class Shelf {

        @SerializedName("shelfId")
        @Expose
        private String shelfId;
        @SerializedName("shelfCode")
        @Expose
        private String shelfCode;
        @SerializedName("desicription")
        @Expose
        private String desicription;
        @SerializedName("floorNumber")
        @Expose
        private Integer floorNumber;
        @SerializedName("cellNumber")
        @Expose
        private Integer cellNumber;

        public String getShelfId() {
            return shelfId;
        }

        public void setShelfId(String shelfId) {
            this.shelfId = shelfId;
        }

        public String getShelfCode() {
            return shelfCode;
        }

        public void setShelfCode(String shelfCode) {
            this.shelfCode = shelfCode;
        }

        public String getDesicription() {
            return desicription;
        }

        public void setDesicription(String desicription) {
            this.desicription = desicription;
        }

        public Integer getFloorNumber() {
            return floorNumber;
        }

        public void setFloorNumber(Integer floorNumber) {
            this.floorNumber = floorNumber;
        }

        public Integer getCellNumber() {
            return cellNumber;
        }

        public void setCellNumber(Integer cellNumber) {
            this.cellNumber = cellNumber;
        }
    }

    public class Floor {

        @SerializedName("floorId")
        @Expose
        private String floorId;
        @SerializedName("floorNo")
        @Expose
        private Integer floorNo;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("shelfId")
        @Expose
        private String shelfId;
        @SerializedName("shelf")
        @Expose
        private Object shelf;

        public String getFloorId() {
            return floorId;
        }

        public void setFloorId(String floorId) {
            this.floorId = floorId;
        }

        public Integer getFloorNo() {
            return floorNo;
        }

        public void setFloorNo(Integer floorNo) {
            this.floorNo = floorNo;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getShelfId() {
            return shelfId;
        }

        public void setShelfId(String shelfId) {
            this.shelfId = shelfId;
        }

        public Object getShelf() {
            return shelf;
        }

        public void setShelf(Object shelf) {
            this.shelf = shelf;
        }
    }

    public class Cell {

        @SerializedName("cellId")
        @Expose
        private String cellId;
        @SerializedName("cellNo")
        @Expose
        private Integer cellNo;
        @SerializedName("cellRfid")
        @Expose
        private Object cellRfid;
        @SerializedName("description")
        @Expose
        private Object description;
        @SerializedName("floorId")
        @Expose
        private String floorId;
        @SerializedName("floor")
        @Expose
        private Object floor;

        public String getCellId() {
            return cellId;
        }

        public void setCellId(String cellId) {
            this.cellId = cellId;
        }

        public Integer getCellNo() {
            return cellNo;
        }

        public void setCellNo(Integer cellNo) {
            this.cellNo = cellNo;
        }

        public Object getCellRfid() {
            return cellRfid;
        }

        public void setCellRfid(Object cellRfid) {
            this.cellRfid = cellRfid;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public String getFloorId() {
            return floorId;
        }

        public void setFloorId(String floorId) {
            this.floorId = floorId;
        }

        public Object getFloor() {
            return floor;
        }

        public void setFloor(Object floor) {
            this.floor = floor;
        }

    }
}
