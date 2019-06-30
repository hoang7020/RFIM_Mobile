package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package {
    @SerializedName("packageRfid")
    @Expose
    private String packageRfid;
    @SerializedName("packageName")
    @Expose
    private Object packageName;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("cellId")
    @Expose
    private String cellId;

    public String getPackageRfid() {
        return packageRfid;
    }

    public void setPackageRfid(String packageRfid) {
        this.packageRfid = packageRfid;
    }

    public Object getPackageName() {
        return packageName;
    }

    public void setPackageName(Object packageName) {
        this.packageName = packageName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }
}
