package vn.com.rfim_mobile.models;

import java.io.Serializable;
import java.sql.Date;

public class InvoiceInfoItem implements Serializable {

    private String productId;
    private String productName;
    private int quantity;
    private String shelf;
    private Date date;

    public InvoiceInfoItem() {
    }

    public InvoiceInfoItem(String productId, String productName, int quantity, String shelf) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.shelf = shelf;
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

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
