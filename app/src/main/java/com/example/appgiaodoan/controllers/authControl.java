package com.example.appgiaodoan.controllers;
import android.util.Log;
import org.json.JSONObject;
import okhttp3.*;

import java.io.IOException;
public class authControl {
    private boolean testMode = true;
    private static final String SUPABASE_URL = "https://hkjqvbgrjqxenugjuhni.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhranF2YmdyanF4ZW51Z2p1aG5pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5MTkyOTksImV4cCI6MjA3NTQ5NTI5OX0.T5kJg5aMeXH17BUAf_BpmacgU-GC77y44V-s4xswi44";

    private final OkHttpClient client = new OkHttpClient();

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
    public void testOTP(String SDT, traOTP otp) {
        if (testMode) {
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    otp.onSuccess();
                } catch (InterruptedException e) {
                    otp.onError("Lỗi gửi OTP!");
                }
            }).start();
            return;
        }
    }
    public interface traOTP {
        void onSuccess();
        void onError(String message);
    }

    public void guiOTP(String SDT, traOTP otp) {
        final String phone = SDT.startsWith("+") ? SDT : "+84" + SDT.substring(1);
        if (testMode) {
            otp.onSuccess();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("phone", phone);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/auth/v1/otp")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        otp.onSuccess();
                    } else {
                        otp.onError("Lỗi gửi OTP: " + response.code());
                    }
                }
            } catch (IOException e) {
                Log.e("AuthController", "Network error: ", e);
                otp.onError("Lỗi kết nối máy chủ!");
            } catch (Exception e) {
                Log.e("AuthController", "Data error: ", e);
                otp.onError("Lỗi xử lý dữ liệu!");
            }
        }).start();
    }
    public void xacThucOTP(String SDT, String token, traOTP otp) {
        final String phone = SDT.startsWith("+") ? SDT : "+84" + SDT.substring(1);
        if (testMode) {
            // chế độ test: OTP mặc định là 123456
            if ("123456".equals(token)) {
                otp.onSuccess();
            } else {
                otp.onError("OTP không đúng!");
            }
            return;
        }
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("phone", phone);
                json.put("token", token);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, json.toString());

                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/auth/v1/verify") // endpoint xác thực OTP
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        otp.onSuccess();
                    } else {
                        otp.onError("OTP không đúng hoặc hết hạn!");
                    }
                }
            } catch (Exception e) {
                otp.onError("Lỗi kết nối hoặc dữ liệu!");
            }
        }).start();
    }
}

