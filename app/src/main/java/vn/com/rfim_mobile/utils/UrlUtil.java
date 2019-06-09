package vn.com.rfim_mobile.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    public static URL getURL(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
