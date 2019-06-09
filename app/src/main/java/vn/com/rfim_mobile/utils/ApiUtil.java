package vn.com.rfim_mobile.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.ObjectResult;

public class ApiUtil extends AsyncTask<URL, String, String> {

    public static final String TAG = ApiUtil.class.getSimpleName();

    private String result;
    private Gson gson;
    private OnTaskCompleted listener;
    private int type;
    private JsonObject param;
    private String method;

    public ApiUtil(OnTaskCompleted listener) {
        this.listener = listener;
        gson = new Gson();
        method = "GET";
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setParam(JsonObject param) {
        this.param = param;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    protected String doInBackground(URL... urls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < urls.length; i++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) urls[i].openConnection();
                BufferedReader br = null;
                if (method.equals("POST")) {
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = connection.getOutputStream();
                    os.write(gson.toJson(param).getBytes());
                    Log.e(TAG, "doInBackground: " + gson.toJson(param));
                }
                InputStream is = connection.getInputStream();
                InputStreamReader ir = new InputStreamReader(is);
                br = new BufferedReader(ir);
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    sb.append(tmp);
                }
                Log.e(TAG, "doInBackground: " + sb.toString());
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isCancelled()) {
                break;
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        ObjectResult obj = gson.fromJson(s, ObjectResult.class);
        listener.onTaskCompleted(obj, type);
    }
}
