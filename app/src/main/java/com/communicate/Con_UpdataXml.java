package com.communicate;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.model.Account;
import com.tool.ParseXml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Con_UpdataXml extends Con_Base{
    private Map<String, String> params;
    private String result;
    private Handler handler;
    private static final String URL_USERACCOUNT = "/view/userIdMap_androidSingleMapOnPagingView";
    private static final String URL_USERCLEARACCOUNT = "/view/userIdMap_androidExpirePush";

    private  HttpURLConnection conn ;
    private String resultData = "";
    private URL url = null;

    public Con_UpdataXml(Handler handler) {
        this.handler = handler;
    }

    private void setFailed(String result) {
        Log.i("con_login", "连接服务器出错：" + result);
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("result", result);
        data.putString("method", "con_failed");
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public void getAccountList(final int userId, final int start, final int limit) {
        class downloadApkThread extends Thread {
            @Override
            public void run() {
                try {
                    params = new HashMap<>();
                    params.put("id", String.valueOf(userId));
                    params.put("start", String.valueOf(start));
                    params.put("limit", String.valueOf(limit));
                    initCon_Post(URL_USERACCOUNT);
                    SetParams(params);
                    result = getDate();
                } catch (IOException e) {
                    result = "网络连接失败";
                    setFailed(result);
                    e.printStackTrace();
                    return;
                }
                JSONArray rows;
                int total;
                List<Account> dataList_account = new ArrayList<>();
                try {
                    rows = new JSONObject(result).getJSONArray("rows");
                    total = new JSONObject(result).getInt("total");
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject jsonObject = (JSONObject) rows.opt(i);
                        Account account = new Account();
                        try {
                            account.setData(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        dataList_account.add(account);
                    }
                } catch (JSONException e) {
                    result = "数据转换失败";
                    setFailed(result);
                    e.printStackTrace();
                    return;
                }
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("total", total);
                data.putParcelableArrayList("rows", (ArrayList<? extends Parcelable>) dataList_account);
                data.putString("method", "user_account");
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }
        new downloadApkThread().start();
    }

}
