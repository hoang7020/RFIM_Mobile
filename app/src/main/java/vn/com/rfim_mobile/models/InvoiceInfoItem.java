package vn.com.rfim_mobile.models;

import java.io.Serializable;
import java.sql.Date;

public class InvoiceInfoItem implements Serializable {

    private String productId;
    private String productName;
    private int quantity;
    private int processQuantity;
    private String shelfId;
    private int status;
    private Date date;

    public InvoiceInfoItem(String productId, String productName, int quantity, String shelfId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.shelfId = shelfId;
    }

    public InvoiceInfoItem(String productId, String productName, int quantity, int processQuantity, int status) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.processQuantity = processQuantity;
        this.status = status;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProcessQuantity() {
        return processQuantity;
    }

    public void setProcessQuantity(int processQuantity) {
        this.processQuantity = processQuantity;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
