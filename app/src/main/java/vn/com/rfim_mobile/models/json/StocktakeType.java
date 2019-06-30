package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StocktakeType {

    @SerializedName("stocktakeTypeId")
    @Expose

    private Integer stocktakeTypeId;
    @SerializedName("stocktakeType")
    @Expose
    private String stocktakeType;

    public Integer getStocktakeTypeId() {
        return stocktakeTypeId;
    }

    public void setStocktakeTypeId(Integer stocktakeTypeId) {
        this.stocktakeTypeId = stocktakeTypeId;
    }

    public String getStocktakeType() {
        return stocktakeType;
    }

    public void setStocktakeType(String stocktakeType) {
        this.stocktakeType = stocktakeType;
    }
}
