package vn.com.rfim_mobile.utils.Bluetooth;

import android.util.Log;

import java.util.Vector;

import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.Observerable;

public class TempScanResult implements Observerable {

    private static final String TAG = TempScanResult.class.getSimpleName();

    private Vector<Observer> observers;
    private String rfidID;
    private int type;

    public TempScanResult() {
        observers = new Vector<>();
    }

    public String getRfidID() {
        return rfidID;
    }

    public void setRfidID(String rfidID) {
        this.rfidID = rfidID;
        sendNotification("Scan Result Change");
    }

    @Override
    public void registerObserver(Observer observer, int type) {
        this.type = type;
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void sendNotification(String message) {
        for (Observer o: observers) {
            o.getNotification(message, type);
        }
    }
}
