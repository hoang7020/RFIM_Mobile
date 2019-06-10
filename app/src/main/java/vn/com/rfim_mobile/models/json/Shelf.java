package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
