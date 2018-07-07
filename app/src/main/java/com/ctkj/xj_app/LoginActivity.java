package com.ctkj.xj_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.ctkj.xj_app.app.MainActivity;
import com.ctkj.xj_app.bean.Reservoir;
import com.ctkj.xj_app.bean.UserInfo;
import com.ctkj.xj_app.http.VolleyJSONObjectListener;
import com.ctkj.xj_app.http.VolleyRequestUtil;
import com.ctkj.xj_app.util.CommonUtils;
import com.ctkj.xj_app.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by zhaohui on 2018/5/8 15:08
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.et_username)
    EditText username_et;
    @BindView(R.id.et_password)
    EditText password_et;
    @BindView(R.id.cb_login)
    CheckBox rememberPwd_cb;

    private SharedPreferences preferences;
    private String reservoirs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //进行数据库操作使数据库自动创建
        LitePal.getDatabase();
        //记住用户名和密码
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("remember_pwd", false);
        if (isRemember) {
            String username = preferences.getString("username", "");
            String password = preferences.getString("password", "");
            username_et.setText(username);
            password_et.setText(password);
            rememberPwd_cb.setChecked(true);
        }
    }

    /**
     * 点击登录按钮，进行用户名和密码的验证，正确后登录到主界面（地图界面）
     *
     * @param view
     */
    @OnClick(R.id.btn_login)
    public void onLoginClick(View view) {
        final String username = username_et.getText().toString().trim();
        final String password = password_et.getText().toString().trim();

        Map<String, String> loginMap = new HashMap<>();
        loginMap.put("username", username);
        loginMap.put("password", password);
//        final JSONObject loginObject = new JSONObject(loginMap);

        Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
        String serverUrl = properties.getProperty("serverUrl");
//        String loginUrl = serverUrl + "login";
        //修改后台APP登陆接口
        String loginUrl = serverUrl +"login/forApp";
        String loginTag = "login request";
        VolleyRequestUtil.jsonObjectPostMapRequest(loginUrl, loginTag, loginMap,
                new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                    @Override
                    public void onMySuccess(final JSONObject loginJsonObject) {
                        LogUtil.w(TAG, "onMySuccess: " + loginJsonObject.toString());
                        loginToHome(loginJsonObject, username, password);
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        LogUtil.d(TAG, "onMyError: " + error.toString());

                    }
                }, true);
    }

    /**
     * 根据服务器返回的不同状态码显示不同的结果
     */
    public void loginToHome(final JSONObject loginJsonObject, final String username, final String password) {
        try {
            String status = loginJsonObject.getString("status");
//            String token = jsonObject.getString("token");
            int loginStatus = 0;
            if (status.equals("success")) {
                loginStatus = 1;
            }
            switch (loginStatus) {
                case 1:
                    Properties properties = CommonUtils.getProperties(getApplicationContext(), "appConfig");
                    String serverUrl = properties.getProperty("serverUrl");
                    String reservoirsUrl = serverUrl + "reservoir/forApp/getIdAndName";
                    String reservoirsTag = "reservoirs request";
                    VolleyRequestUtil.jsonObjectRequestGet(reservoirsUrl, reservoirsTag,
                            new VolleyJSONObjectListener(VolleyJSONObjectListener.mListener, VolleyJSONObjectListener.mErrorListener) {
                                @Override
                                public void onMySuccess(JSONObject jsonObject) {
                                    Log.w(TAG, "onMySuccess: " + jsonObject.toString());
                                    reservoirs = getReservoirs(jsonObject);

                                    SharedPreferences.Editor editor = preferences.edit();
                                    Toast toastSuccess =
                                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT);
                                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                    toastSuccess.show();
                                    if (rememberPwd_cb.isChecked()) {
                                        editor.putBoolean("remember_pwd", true);
                                        editor.putString("username", username);
                                        editor.putString("password", password);
                                    } else {
                                        editor.clear();
                                    }
                                    editor.apply();
                                    try {
                                        String userInfo = loginJsonObject.getString("msg");
                                        Gson gson = new Gson();
                                        UserInfo user = gson.fromJson(userInfo, UserInfo.class);
                                        //保存到数据库
                                        user.saveOrUpdate("id = ?", user.getUserId() + "");
                                        ArrayList<Reservoir> reservoirList = gson.fromJson(reservoirs, new TypeToken<ArrayList<Reservoir>>() {
                                        }.getType());
                                        LogUtil.w(TAG, "onMySuccess: " + reservoirList.toString());
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putParcelableArrayListExtra("reservoirList", reservoirList);
                                        intent.putExtra("inspectUserId",user.getUserId());
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onMyError(VolleyError error) {
                                    LogUtil.w(TAG, "onMyError: " + error.toString());
                                }
                            }, true);
                    break;
                case 0:
                    Toast toastFailed =
                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT);
                    toastFailed.setGravity(Gravity.CENTER, 0, 0);
                    toastFailed.show();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回所有水库id、name和经纬度
     */

    private String getReservoirs(JSONObject jsonObject) {
        try {
            int code = jsonObject.getInt("code");
            switch (code) {
                case 1:
                    reservoirs = jsonObject.getString("data");
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reservoirs;
    }

}
