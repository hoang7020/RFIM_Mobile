package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
