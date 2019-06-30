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

public class ApiUtil extends AsyncTask<URL, Void, Result> {

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
    protected Result doInBackground(URL... urls) {
        StringBuilder sb = new StringBuilder();
        int code = 0;
        for (int i = 0; i < urls.length; i++) {
            try {
                HttpURLConnection connection = (HttpURLConnection) urls[i].openConnection();
                BufferedReader br = null;
                if (!method.equals("GET")) {
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = connection.getOutputStream();
                    os.write(gson.toJson(param).getBytes());
                    Log.e(TAG, "doInBackground: " + gson.toJson(param));
                }
                code = connection.getResponseCode();
                InputStream is;
                if (code >= 400) {
                    is = connection.getErrorStream();
                } else {
                    is = connection.getInputStream();
                }
                InputStreamReader ir = new InputStreamReader(is);
                br = new BufferedReader(ir);
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    sb.append(tmp);
                }

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isCancelled()) {
                break;
            }
        }
        return new Result(sb.toString(), code);
    }

    @Override
    protected void onPostExecute(Result result) {
//        ObjectResult obj = gson.fromJson(result.getData(), ObjectResult.class);
        listener.onTaskCompleted(result.getData(), type, result.getCode());
    }

}

class Result {
    private String data;
    private int code;

    public Result(String data, int code) {
        this.data = data;
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
