package vn.com.rfim_mobile.interfaces;

public interface Observerable {
    public void registerObserver(Observer observer, int type);

    public void unregisterObserver(Observer observer);

    public void sendNotification(String message);
}
