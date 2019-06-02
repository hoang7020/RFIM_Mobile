package vn.com.rfim_mobile.utils.Bluetooth;

import java.util.Vector;

import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.Observerable;

public class TempScanResult implements Observerable {

    private Vector<Observer> observers;

    private String rfidID;

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
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void sendNotification(String message) {
        for (Observer o: observers) {
            o.getNotification(message);
        }
    }
}
