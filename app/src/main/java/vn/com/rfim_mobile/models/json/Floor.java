package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
