package vn.com.rfim_mobile.interfaces;


import vn.com.rfim_mobile.models.json.ObjectResult;

public interface OnTaskCompleted {

    void onTaskCompleted(ObjectResult result, int type);

}
