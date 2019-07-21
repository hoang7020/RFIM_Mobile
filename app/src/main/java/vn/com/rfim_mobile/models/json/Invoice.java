package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invoice {

    @SerializedName("invoiceId")
    @Expose
    private String invoiceId;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("processQuantity")
    @Expose
    private Integer processQuantity;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("shelfId")
    @Expose
    private Object shelfId;
    @SerializedName("status")
    @Expose
    private Integer status;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getProcessQuantity() {
        return processQuantity;
    }

    public void setProcessQuantity(Integer processQuantity) {
        this.processQuantity = processQuantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getShelfId() {
        return shelfId;
    }

    public void setShelfId(Object shelfId) {
        this.shelfId = shelfId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
