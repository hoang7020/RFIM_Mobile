package vn.com.rfim_mobile.interfaces;

public interface Observerable {
    public void registerObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void sendNotification(String message);
}
