package vn.com.rfim_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.models.json.User;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.net.HttpURLConnection;

public class LoginActivity extends AppCompatActivity implements OnTaskCompleted {

    public static final String TAG = LoginActivity.class.getSimpleName();

    public static Context context;

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnExit;
    private RFIMApi mRfimApi;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkLogin();

        initView();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString() + "";
                String password = edtPassword.getText().toString() + "";
                Log.e(TAG, "onClick: " + username + password);
                if (username.equals("") && password.equals("")) {
                    Toast.makeText(LoginActivity.this, getString(R.string.not_input_username_or_password), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.login(username, password);
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        btnExit = findViewById(R.id.btn_exit);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
        context = getApplicationContext();
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data);
        switch (type) {
            case Constant.LOGIN:
                if (code == HttpURLConnection.HTTP_OK) {
                    User user = gson.fromJson(data, User.class);
                    if (user.getRoleId() == 3) {
                        PreferenceUtil.getInstance(getApplicationContext()).putStringValue("username", user.getUsername());
                        PreferenceUtil.getInstance(getApplicationContext()).putIntValue("userid", user.getUserId());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    showResponseMessage(data);
                }
                break;
        }
    }

    public void checkLogin() {
        if (PreferenceUtil.getInstance(getApplicationContext()).getStringValue("username", "") != "") {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
