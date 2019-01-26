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
     * 解析Json数据
     *
     * @param json
     * @return
     */
    public static SocketMessage parseJson(String json) {
        SocketMessage message = new SocketMessage();
        try {
            JSONObject jsonObject = new JSONObject(json);
            message.setId(jsonObject.getString("id"));
            message.setFeature(jsonObject.getString("feature"));
            message.setFid(jsonObject.getString("fid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }
}
