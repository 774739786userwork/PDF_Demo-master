package com.example.jammy.pdf_demo.server;


import android.util.Log;

import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.user.SocketMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**

 */

public class JsonUtil {
    /**
     * 根据消息对象构建Json对象
     *
     * @param message
     * @return
     */
    public static JSONObject initJsonObject(SocketMessage message) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    /**
     * 解析Json数据
     *
     * @param json
     * @return
     */
    public static SocketMessage parseJson(String json) {
        SocketMessage message = new SocketMessage();
        try {
            JSONObject jsonObject = new JSONObject(json);
//            message.setId(jsonObject.getString("id"));
            message.setFileUrl(jsonObject.getString("url"));
            Log.e("tag", "parseJson: "+message.getFileUrl());
//            message.setEmployeename(jsonObject.getString("employeeName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
}
