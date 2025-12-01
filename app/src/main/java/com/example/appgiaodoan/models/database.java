package com.example.appgiaodoan.models;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.*;

public class database {

    private static final String SUPABASE_URL = "https://hkjqvbgrjqxenugjuhni.supabase.co";
    public static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhranF2YmdyanF4ZW51Z2p1aG5pIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1OTkxOTI5OSwiZXhwIjoyMDc1NDk1Mjk5fQ.FdP1lfa5iT-_sRDPvul3yLumWj9vbKjQnZovEoX-ODs";

    private final OkHttpClient client = new OkHttpClient();
    private boolean testMode = true;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String DEBUG_TAG = "DEBUG_APP";

    public interface ModelCallbackKhuyenMai {
        void onSuccess(List<khuyenMai> danhSach);
        void onError(String message);
    }
    public interface RatingListCallback {
        void onSuccess(List<Integer> danhSachDiem);
        void onError(String message);
    }
    public interface ModelCallbackLogin {
        void onSuccess(String message, String accessToken, String userId, String vaiTro); // <--- THÊM String vaiTro
        void onError(String message);
    }
    public interface ModelCallbackLichSu {
        void onSuccess(List<lichSuDonHang> list);
        void onError(String message);
    }
    public interface ModelCallbackSimple {
        void onSuccess(String message);
        void onError(String message);
    }
    public interface DriverOrderCallback {
        void onSuccess(List<com.example.appgiaodoan.models.donHangTaiXe> list);
        void onError(String message);
    }
    public interface DriverOrderDetailCallback {
        void onSuccess(JSONObject data);
        void onError(String message);
    }
    public interface MonAnCallback {
        void onSuccess(String message);
        void onError(String message);
    }
    public interface ModelCallbackProfile {
        void onSuccess(JSONObject profileData);
        void onError(String message);
    }
    public interface ActiveOrderCallback {
        void onFound(String idDonHang, String thoiGianDat, double phiGiaoHang);
        void onNotFound();
        void onError(String message);
    }
    public interface RevenueCallback {
        void onSuccess(List<JSONObject> rawData);
        void onError(String message);
    }
    public interface ModelCallbackDanhGiaList {
        void onSuccess(List<com.example.appgiaodoan.models.danhGia> list);
        void onError(String message);
    }
    public interface BooleanCallback {
        void onResult(boolean isLiked);
        void onError(String message);
    }
    public interface QuanAnInfoCallback {
        void onSuccess(quanAn quanInfo);
        void onError(String message);
    }
    public interface IncomeDriverCallback {
        void onSuccess(List<com.example.appgiaodoan.models.thuNhapTaiXe> list);
        void onError(String message);
    }
    public interface DonHangQuanCallback {
        void onSuccess(List<com.example.appgiaodoan.models.donHangQuan> list);
        void onError(String message);
    }
    public interface ModelCallbackDanhSach {
        void onSuccess(List<quanAn> danhSachQuanAn);
        void onError(String message);
    }
    public interface ReOrderCallback {
        void onSuccess(String idNhaHang, HashMap<String, Integer> cartItems);
        void onError(String message);
    }
    public interface TaiXeInfoCallback {
        void onSuccess(String idTaiXe);
        void onError(String message);
    }
    public interface ActiveOrderListener {
        void onActiveOrderLoaded(JSONObject orderData);
        void onNoActiveOrder();
        void onError(String message);
    }
    public interface ModelCallbackMonAnDanhSach {
        void onSuccess(List<monAn> danhSachMonAn);
        void onError(String message);
    }

    public OkHttpClient getClient() {
        return client;
    }

    private void runOnBackgroundThread(Runnable r) {
        new Thread(r).start();
    }

    private String chuanHoaSDTAuth(String sdt) {
        if (sdt.startsWith("+84")) return sdt.trim();
        if (sdt.startsWith("0")) return "+84" + sdt.substring(1).trim();
        if (sdt.startsWith("84")) return "+" + sdt.trim();
        return sdt.trim();
    }
    public void getLichSuDonHang(String userId, ModelCallbackLichSu callback) {
        runOnBackgroundThread(() -> {
            try {
                String status = URLEncoder.encode("Hoàn thành", StandardCharsets.UTF_8.toString());

                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "idnguoidung=eq." + userId.trim() +
                        "&trangthai=eq." + status +
                        "&select=iddonhang,tongtien,trangthai,thoigian,nhahang(tennhahang,anhdaidien_url)" +
                        "&order=thoigian.desc";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tải lịch sử: " + response.code());
                        return;
                    }
                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);
                    List<lichSuDonHang> list = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        String id = obj.getString("iddonhang");
                        double tongTien = obj.optDouble("tongtien", 0);
                        String trangThai = obj.optString("trangthai", "Hoàn thành");
                        String thoiGianRaw = obj.optString("thoigian", "");

                        // Xử lý hiển thị thời gian đơn giản
                        String thoiGianHienThi = thoiGianRaw.replace("T", " ").split("\\.")[0];

                        // Lấy thông tin quán từ nested object
                        String tenQuan = "Quán ăn";
                        String hinhQuan = "";

                        if (!obj.isNull("nhahang")) {
                            JSONObject nh = obj.getJSONObject("nhahang");
                            tenQuan = nh.optString("tennhahang", "Quán ăn");
                            hinhQuan = nh.optString("anhdaidien_url", "");
                        }

                        list.add(new lichSuDonHang(id, tenQuan, hinhQuan, thoiGianHienThi, tongTien, trangThai));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getTaiXeIdByUserId(String userId, TaiXeInfoCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/taixe?idnguoidung=eq." + userId + "&select=idtaixe";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tìm tài xế: " + response.code());
                        return;
                    }
                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);

                    if (arr.length() > 0) {
                        String realIdTaiXe = arr.getJSONObject(0).getString("idtaixe");
                        callback.onSuccess(realIdTaiXe);
                    } else {
                        callback.onError("Bạn chưa đăng ký làm tài xế!");
                    }
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public quanAn getNhaHangChiTiet(String maNhaHang) throws IOException {
        String url = SUPABASE_URL + "/rest/v1/nhahang?idnhahang=eq." + maNhaHang.trim() + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<quanAn>>() {}.getType();
            List<quanAn> danhSach = gson.fromJson(json, listType);

            if (danhSach != null && !danhSach.isEmpty()) {
                return danhSach.get(0);
            }
            return null;
        }
    }

    public List<monAn> getDanhSachMonAn(String maNhaHang) throws IOException {
        String url = SUPABASE_URL + "/rest/v1/monan?idnhahang=eq." + maNhaHang.trim() + "&select=*";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<monAn>>() {}.getType();
            return gson.fromJson(json, listType);
        }
    }
    public void layDuLieuDatLai(String idDonHang, ReOrderCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                Log.d(DEBUG_TAG, "♻️ BẮT ĐẦU ĐẶT LẠI CHO ĐƠN: " + idDonHang);

                // --- BƯỚC 1: LẤY ID NHÀ HÀNG TỪ BẢNG DONHANG ---
                String urlOrder = SUPABASE_URL + "/rest/v1/donhang?iddonhang=eq." + idDonHang.trim() + "&select=idnhahang";

                Log.d(DEBUG_TAG, "👉 URL STEP 1: " + urlOrder);

                Request reqOrder = new Request.Builder()
                        .url(urlOrder)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get().build();

                String idNhaHang = "";
                try (Response respOrder = client.newCall(reqOrder).execute()) {
                    if (!respOrder.isSuccessful()) {
                        String err = respOrder.body() != null ? respOrder.body().string() : "Unknown";
                        Log.e(DEBUG_TAG, "❌ LỖI STEP 1 (" + respOrder.code() + "): " + err);
                        callback.onError("Lỗi lấy thông tin quán: " + respOrder.code());
                        return;
                    }
                    String json = respOrder.body().string();
                    Log.d(DEBUG_TAG, "✅ RESPONSE STEP 1: " + json);

                    JSONArray arr = new JSONArray(json);
                    if (arr.length() == 0) {
                        callback.onError("Đơn hàng không tồn tại (Mảng rỗng)");
                        return;
                    }
                    idNhaHang = arr.getJSONObject(0).getString("idnhahang");
                }


                // Ở đây tôi dùng chữ thường theo quy ước mới nhất.
                String urlDetails = SUPABASE_URL + "/rest/v1/chitietdonhang?iddonhang=eq." + idDonHang.trim() + "&select=idmonan,soluong";

                Log.d(DEBUG_TAG, "👉 URL STEP 2: " + urlDetails);

                Request reqDetails = new Request.Builder()
                        .url(urlDetails)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get().build();

                try (Response respDetails = client.newCall(reqDetails).execute()) {
                    if (!respDetails.isSuccessful()) {
                        String err = respDetails.body() != null ? respDetails.body().string() : "Unknown";
                        Log.e(DEBUG_TAG, "❌ LỖI STEP 2 (" + respDetails.code() + "): " + err);

                        callback.onError("Lỗi lấy chi tiết món (Code " + respDetails.code() + ")");
                        return;
                    }

                    String jsonDetails = respDetails.body().string();
                    Log.d(DEBUG_TAG, "✅ RESPONSE STEP 2: " + jsonDetails);

                    JSONArray arrDetails = new JSONArray(jsonDetails);
                    HashMap<String, Integer> cartItems = new HashMap<>();

                    for (int i = 0; i < arrDetails.length(); i++) {
                        JSONObject item = arrDetails.getJSONObject(i);
                        // Chú ý: Key JSON phải là chữ thường
                        cartItems.put(item.getString("idmonan"), item.getInt("soluong"));
                    }

                    if (cartItems.isEmpty()) {
                        Log.w(DEBUG_TAG, "⚠️ CẢNH BÁO: Không tìm thấy món ăn nào trong chi tiết đơn.");
                    }

                    callback.onSuccess(idNhaHang, cartItems);
                }

            } catch (Exception e) {
                Log.e(DEBUG_TAG, "❌ EXCEPTION ĐẶT LẠI: " + e.getMessage());
                e.printStackTrace();
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    // 2. Gửi đánh giá
    public void guiDanhGia(String idDonHang, String idNguoiDung, String idNhaHang, int diem, String noiDung, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("iddonhang", idDonHang);
                json.put("idnguoidung", idNguoiDung);
                json.put("idnhahang", idNhaHang);
                json.put("diem", diem);
                json.put("noidung", noiDung);

                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/danhgia")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Đánh giá thành công!");
                    else callback.onError("Lỗi gửi đánh giá: " + response.code());
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void kiemTraDonHangDangXuLy(String idNguoiDung, ActiveOrderCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                // Query: idnguoidung = ... AND trangthai = 'Đang xử lý'
                // Sắp xếp lấy đơn mới nhất (desc)
                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "idnguoidung=eq." + idNguoiDung +
                        "&trangthai=eq." + URLEncoder.encode("Đang xử lý", StandardCharsets.UTF_8.toString()) +
                        "&order=thoigian.desc&limit=1";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi kiểm tra đơn hàng: " + response.code());
                        return;
                    }

                    String json = response.body().string();
                    JSONArray array = new JSONArray(json);

                    if (array.length() > 0) {
                        JSONObject order = array.getJSONObject(0);
                        String idDonHang = order.getString("iddonhang");
                        String thoiGian = order.getString("thoigian");
                        double phiShip = order.optDouble("phigiaohang", 15000); // Mặc định 15k nếu null

                        callback.onFound(idDonHang, thoiGian, phiShip);
                    } else {
                        callback.onNotFound();
                    }
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getChiTietNhieuMonAn(String idsMonAnCommaSeparated, ModelCallbackMonAnDanhSach callback) {
        runOnBackgroundThread(() -> {
            try {
                String queryValue = "in.(" + idsMonAnCommaSeparated + ")";
                String encodedInValue = URLEncoder.encode(queryValue, StandardCharsets.UTF_8.toString());

                String url = SUPABASE_URL + "/rest/v1/monan?idmonan=" + encodedInValue + "&select=*";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body().string();
                        callback.onError("Lỗi tải món ăn (Code " + response.code() + "): " + errorBody);
                        return;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<monAn>>() {}.getType();
                    List<monAn> danhSach = gson.fromJson(json, listType);

                    callback.onSuccess(danhSach);
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối khi tải món ăn: " + e.getMessage());
            }
        });
    }

    public void datDonHang(JSONObject thongTinChung, JSONArray chiTietMonAn, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                // 1. GỬI ĐƠN HÀNG (HEADER)
                RequestBody body = RequestBody.create(JSON, thongTinChung.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/donhang")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .post(body)
                        .build();

                String idDonHang = null;
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String err = response.body().string();
                        callback.onError("Lỗi tạo đơn: " + response.code() + " " + err);
                        return;
                    }
                    // Lấy ID đơn hàng vừa tạo
                    String respStr = response.body().string();
                    JSONArray arr = new JSONArray(respStr);
                    idDonHang = arr.getJSONObject(0).getString("iddonhang");
                }

                if (idDonHang == null) {
                    callback.onError("Không lấy được ID đơn hàng.");
                    return;
                }

                // 2. GỬI CHI TIẾT (SỬA LỖI TẠI ĐÂY)
                // Gán iddonhang vào từng món
                for (int i = 0; i < chiTietMonAn.length(); i++) {
                    JSONObject item = chiTietMonAn.getJSONObject(i);
                    item.put("iddonhang", idDonHang);
                }

                RequestBody bodyDetails = RequestBody.create(JSON, chiTietMonAn.toString());
                Request requestDetails = new Request.Builder()
                        // SỬA TÊN BẢNG: chitietdonhang (không gạch dưới)
                        .url(SUPABASE_URL + "/rest/v1/chitietdonhang")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(bodyDetails)
                        .build();

                try (Response responseDetails = client.newCall(requestDetails).execute()) {
                    if (!responseDetails.isSuccessful()) {
                        String err = responseDetails.body().string();
                        callback.onError("Lỗi lưu chi tiết món: " + err);
                    } else {
                        callback.onSuccess("Đặt hàng thành công!");
                    }
                }

            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }

    public void guiOTP(String sdt, ModelCallbackSimple callback) {
        final String phoneAuth = chuanHoaSDTAuth(sdt.trim());
        if (testMode) {
            runOnBackgroundThread(() -> {
                try { Thread.sleep(500); callback.onSuccess("Đã gửi OTP (Test Mode)"); }
                catch (InterruptedException e) { callback.onError("Lỗi test OTP!"); }
            });
            return;
        }
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("phone", phoneAuth);
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/auth/v1/otp")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Gửi OTP thành công!");
                    else callback.onError("Lỗi gửi OTP: Hết hạn ngạch.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối máy chủ!"); }
        });
    }

    public void xacThucOTP(String sdt, String token, ModelCallbackSimple callback) {
        final String phoneAuth = chuanHoaSDTAuth(sdt.trim());
        if (testMode) {
            if ("123456".equals(token)) callback.onSuccess("Xác thực OTP thành công");
            else callback.onError("OTP không đúng!");
            return;
        }
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("phone", phoneAuth);
                json.put("token", token);
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/auth/v1/verify")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Xác thực OTP thành công!");
                    else callback.onError("OTP không đúng hoặc hết hạn!");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối hoặc dữ liệu!"); }
        });
    }

    public void dangNhap(String sdt, String matKhau, ModelCallbackLogin callback) { // Đã xóa dấu phẩy thừa
        final String sdtGoc = sdt.trim();

        // --- MÔI TRƯỜNG TEST MODE ---
        if (testMode) {
            runOnBackgroundThread(() -> {
                try {
                    Thread.sleep(500);
                    // Thêm "vaitro" vào select
                    String url = SUPABASE_URL + "/rest/v1/nguoidung?sodienthoai=eq." + URLEncoder.encode(sdtGoc, StandardCharsets.UTF_8.toString()) + "&select=idnguoidung,matkhau,vaitro";

                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("apikey", SUPABASE_API_KEY)
                            .addHeader("Accept", "application/json")
                            .get()
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        String body = response.body().string();
                        JSONArray arr = new JSONArray(body); // Khai báo arr ở đây

                        if (arr.length() == 0) {
                            callback.onError("Sai SĐT hoặc tài khoản không tồn tại!");
                            return;
                        }

                        JSONObject user = arr.getJSONObject(0);
                        String matKhauCSDL = user.optString("matkhau", "");

                        if (matKhau.equals(matKhauCSDL)) {
                            String userId = user.getString("idnguoidung");
                            String vaiTro = user.optString("vaitro", "nguoidung"); // Lấy vai trò

                            // Gọi onSuccess với đủ 4 tham số
                            callback.onSuccess("Đăng nhập thành công (Test Mode)", "FAKE_TOKEN", userId, vaiTro);
                        } else {
                            callback.onError("Sai mật khẩu!");
                        }
                    }
                } catch (Exception e) {
                    callback.onError("Lỗi kết nối hoặc dữ liệu: " + e.getMessage());
                }
            });
            return;
        }

        // --- MÔI TRƯỜNG THẬT (CHẠY KHI testMode = false) ---
        runOnBackgroundThread(() -> {
            try {
                String encodedPhone = URLEncoder.encode("eq." + sdtGoc, StandardCharsets.UTF_8.toString());
                // Thêm "vaitro" vào select
                String url = SUPABASE_URL + "/rest/v1/nguoidung?sodienthoai=" + encodedPhone + "&select=idnguoidung,matkhau,vaitro";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi Server: " + response.code());
                        return;
                    }
                    String body = response.body().string();
                    JSONArray arr = new JSONArray(body); // Khai báo arr

                    if (arr.length() == 0) {
                        callback.onError("Tài khoản không tồn tại!");
                        return;
                    }

                    JSONObject user = arr.getJSONObject(0);
                    String matKhauCSDL = user.optString("matkhau", "");

                    if (matKhau.equals(matKhauCSDL)) {
                        String userId = user.getString("idnguoidung");
                        String vaiTro = user.optString("vaitro", "nguoidung"); // Lấy vai trò

                        // Gọi onSuccess với đủ 4 tham số
                        callback.onSuccess("Đăng nhập thành công!", "FAKE_TOKEN", userId, vaiTro);
                    } else {
                        callback.onError("Sai mật khẩu!");
                    }
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }


    public void dangKi(String sdt, String matKhau, ModelCallbackSimple callback) {
        if (testMode) {
            runOnBackgroundThread(() -> {
                try { Thread.sleep(500); callback.onSuccess("Đăng ký thành công (Test Mode)"); }
                catch (InterruptedException e) { callback.onError("Lỗi test Đăng ký!"); }
            });
            return;
        }
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonSignup = new JSONObject();
                jsonSignup.put("email", sdt + "@dummy.app");
                jsonSignup.put("password", matKhau);

                RequestBody bodySignup = RequestBody.create(JSON, jsonSignup.toString());
                Request requestSignup = new Request.Builder()
                        .url(SUPABASE_URL + "/auth/v1/signup")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(bodySignup)
                        .build();
                try (Response responseSignup = client.newCall(requestSignup).execute()) {
                    if (responseSignup.isSuccessful()) {
                        JSONObject userJson = new JSONObject(responseSignup.body().string());
                        String userId = userJson.getJSONObject("user").getString("id");
                        insertNguoiDungProfile(userId, sdt, sdt + "@dummy.app", matKhau, callback);
                    } else { callback.onError("Lỗi đăng ký!"); }
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối hoặc dữ liệu!"); }
        });
    }
    public void capNhatDiaChiNguoiDung(String idNguoiDung, String diaChiMoi, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonUpdate = new JSONObject();
                jsonUpdate.put("diachi", diaChiMoi);
                RequestBody body = RequestBody.create(JSON, jsonUpdate.toString());
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .patch(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật địa chỉ thành công!");
                    else callback.onError("Lỗi: Không thể cập nhật địa chỉ.");
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối khi cập nhật địa chỉ!");
            }
        });
    }
    private void insertNguoiDungProfile(String userId, String sdt, String email, String matKhau, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonProfile = new JSONObject();
                jsonProfile.put("idnguoidung", userId);
                jsonProfile.put("sodienthoai", sdt);
                jsonProfile.put("email", email);
                jsonProfile.put("tennguoidung", "Người dùng mới");
                jsonProfile.put("matkhau", matKhau);

                RequestBody bodyProfile = RequestBody.create(JSON, jsonProfile.toString());
                Request requestProfile = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/nguoidung")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .post(bodyProfile)
                        .build();
                try (Response responseProfile = client.newCall(requestProfile).execute()) {
                    if (responseProfile.isSuccessful()) callback.onSuccess("Đăng ký thành công!");
                    else callback.onError("Lỗi: Không thể tạo hồ sơ người dùng.");
                }
            } catch (Exception e) { callback.onError("Lỗi: Không thể tạo hồ sơ người dùng."); }
        });
    }

    public void capNhatTenNguoiDung(String idNguoiDung, String tenMoi, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonUpdate = new JSONObject();
                jsonUpdate.put("tennguoidung", tenMoi);
                RequestBody body = RequestBody.create(JSON, jsonUpdate.toString());
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .patch(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật tên thành công!");
                    else callback.onError("Lỗi cập nhật tên.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối khi cập nhật tên!"); }
        });
    }

    public void getNguoiDungProfile(String idNguoiDung, ModelCallbackProfile callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim()
                        + "&select=tennguoidung,diachi,email,sodienthoai,nhanthongbao,idnguoidung";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (jsonArray.length() > 0) callback.onSuccess(jsonArray.getJSONObject(0));
                    else callback.onError("Không tìm thấy profile cho ID này.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối khi lấy profile!"); }
        });
    }

    public void capNhatMatKhauProfile(String idNguoiDung, String matKhauMoi, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonUpdate = new JSONObject();
                jsonUpdate.put("matkhau", matKhauMoi);
                RequestBody body = RequestBody.create(JSON, jsonUpdate.toString());
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .patch(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Đổi mật khẩu thành công!");
                    else callback.onError("Lỗi cập nhật mật khẩu.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối khi cập nhật mật khẩu!"); }
        });
    }

    public void capNhatThongBao(String idNguoiDung, boolean nhanThongBao, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonUpdate = new JSONObject();
                jsonUpdate.put("nhanthongbao", nhanThongBao);
                RequestBody body = RequestBody.create(JSON, jsonUpdate.toString());
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .patch(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật thông báo thành công!");
                    else callback.onError("Lỗi cập nhật thông báo.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối khi cập nhật thông báo!"); }
        });
    }

    public void capNhatEmailAuthVaProfile(String idNguoiDung, String emailMoi, ModelCallbackSimple callback) {
        if (idNguoiDung == null || idNguoiDung.isEmpty()) {
            callback.onError("Lỗi: Không có ID người dùng để cập nhật email.");
            return;
        }

        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonUpdate = new JSONObject();
                jsonUpdate.put("email", emailMoi);
                RequestBody body = RequestBody.create(JSON, jsonUpdate.toString());
                String url = SUPABASE_URL + "/rest/v1/nguoidung?idnguoidung=eq." + idNguoiDung.trim();
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=representation")
                        .patch(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật email thành công!");
                    else callback.onError("Lỗi cập nhật email.");
                }
            } catch (Exception e) { callback.onError("Lỗi kết nối khi cập nhật email!"); }
        });
    }
    public void getDanhSachKhuyenMai(String idNhaHang, ModelCallbackKhuyenMai callback) {
        runOnBackgroundThread(() -> {
            try {
                String queryCondition = "or=(idnhahang.eq." + idNhaHang.trim() + ",idnhahang.is.null)";
                String now = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new java.util.Date());
                String url = SUPABASE_URL + "/rest/v1/khuyenmai?"
                        + queryCondition
                        + "&trangthai=eq.true"
                        + "&thoigianbatdau=lte." + now
                        + "&thoigianketthuc=gte." + now
                        + "&select=*";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tải khuyến mãi: " + response.code());
                        return;
                    }
                    String json = response.body().string();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<khuyenMai>>() {}.getType();
                    List<khuyenMai> danhSach = gson.fromJson(json, listType);

                    callback.onSuccess(danhSach);

                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối hoặc parsing khi tải khuyến mãi: " + e.getMessage());
            }
        });
    }
    public void getActiveOrder(String userId, ActiveOrderListener listener) {
        runOnBackgroundThread(() -> {
            try {
                // Query: Tìm đơn hàng có idnguoidung khớp VÀ trạng thái là 'Đang xử lý'
                // Sắp xếp: Lấy đơn mới nhất (thoigian DESC)
                String trangThai = URLEncoder.encode("Đang xử lý", StandardCharsets.UTF_8.toString());

                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "idnguoidung=eq." + userId.trim() +
                        "&trangthai=eq." + trangThai +
                        "&order=thoigian.desc&limit=1";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        listener.onError("Lỗi kiểm tra đơn: " + response.code());
                        return;
                    }

                    String json = response.body().string();
                    JSONArray array = new JSONArray(json);

                    if (array.length() > 0) {
                        // Có đơn hàng đang xử lý -> Trả về JSON Object của đơn đó
                        listener.onActiveOrderLoaded(array.getJSONObject(0));
                    } else {
                        // Không có đơn hàng nào
                        listener.onNoActiveOrder();
                    }
                }
            } catch (Exception e) {
                listener.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getDanhSachDanhGia(String idNhaHang, ModelCallbackDanhGiaList callback) {
        runOnBackgroundThread(() -> {
            try {
                // --- BƯỚC 1: XÂY DỰNG URL ---
                // Lưu ý: Cấu trúc &select=...,nguoidung(tennguoidung) yêu cầu
                // bảng 'danhgia' phải có khóa ngoại trỏ tới 'nguoidung'
                String url = SUPABASE_URL + "/rest/v1/danhgia?" +
                        "idnhahang=eq." + idNhaHang.trim() +
                        "&select=iddanhgia,diem,noidung,thoigian,nguoidung(tennguoidung)" +
                        "&order=thoigian.desc";

                // >>> LOG URL ĐỂ KIỂM TRA <<<
                Log.d(DEBUG_TAG, "👉 URL GET DANH GIA: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    // --- BƯỚC 2: KIỂM TRA PHẢN HỒI ---
                    if (!response.isSuccessful()) {
                        // Đọc nội dung lỗi từ Supabase
                        String errorBody = response.body() != null ? response.body().string() : "Empty Body";

                        // >>> LOG NỘI DUNG LỖI CHI TIẾT <<<
                        Log.e(DEBUG_TAG, "❌ LỖI API DANH GIA (" + response.code() + "): " + errorBody);

                        callback.onError("Lỗi Server: " + errorBody);
                        return;
                    }

                    String json = response.body().string();
                    Log.d(DEBUG_TAG, "✅ KẾT QUẢ DANH GIA: " + json);

                    JSONArray arr = new JSONArray(json);
                    List<com.example.appgiaodoan.models.danhGia> list = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        // Đảm bảo dùng key chữ thường
                        String id = obj.getString("iddanhgia");
                        int diem = obj.getInt("diem");
                        String noidung = obj.optString("noidung", "");
                        String thoigian = obj.optString("thoigian", "").split("T")[0];

                        String tenUser = "Người dùng";
                        // Xử lý Json Object lồng nhau từ bảng nguoidung
                        if (!obj.isNull("nguoidung")) {
                            JSONObject userObj = obj.getJSONObject("nguoidung");
                            tenUser = userObj.optString("tennguoidung", "Ẩn danh");
                        }

                        list.add(new com.example.appgiaodoan.models.danhGia(id, tenUser, diem, noidung, thoigian));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "❌ EXCEPTION DANH GIA: " + e.getMessage());
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getDiemDanhGia(String idNhaHang, RatingListCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                // Chỉ lấy cột 'diem' để tính toán cho nhẹ
                String url = SUPABASE_URL + "/rest/v1/danhgia?" +
                        "idnhahang=eq." + idNhaHang.trim() +
                        "&select=diem";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tải điểm: " + response.code());
                        return;
                    }
                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);
                    List<Integer> listDiem = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        listDiem.add(obj.getInt("diem"));
                    }

                    callback.onSuccess(listDiem);
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void checkYeuThich(String userId, String nhaHangId, BooleanCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/yeuthich?idnguoidung=eq." + userId + "&idnhahang=eq." + nhaHangId + "&select=count";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .head()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    // Supabase trả về header "Content-Range": "0-0/1" nếu có 1 dòng
                    String range = response.header("Content-Range");
                    boolean isLiked = range != null && !range.startsWith("*/0");
                    callback.onResult(isLiked);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void toggleYeuThich(String userId, String nhaHangId, boolean isCurrentlyLiked, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                if (isCurrentlyLiked) {
                    String url = SUPABASE_URL + "/rest/v1/yeuthich?idnguoidung=eq." + userId + "&idnhahang=eq." + nhaHangId;
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("apikey", SUPABASE_API_KEY)
                            .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                            .delete().build();
                    client.newCall(request).execute();
                    callback.onSuccess("Đã xóa khỏi yêu thích");
                } else {
                    JSONObject json = new JSONObject();
                    json.put("idnguoidung", userId);
                    json.put("idnhahang", nhaHangId);

                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .url(SUPABASE_URL + "/rest/v1/yeuthich")
                            .addHeader("apikey", SUPABASE_API_KEY)
                            .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .post(body).build();
                    client.newCall(request).execute();
                    callback.onSuccess("Đã thêm vào yêu thích");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    public void getDanhSachYeuThich(String userId, ModelCallbackDanhSach callback) {
        runOnBackgroundThread(() -> {
            try {
                // Query: Lấy bảng yeuthich, join với bảng nhahang để lấy thông tin quán
                String url = SUPABASE_URL + "/rest/v1/yeuthich?" +
                        "idnguoidung=eq." + userId.trim() +
                        "&select=nhahang(*)";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tải yêu thích: " + response.code());
                        return;
                    }

                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);
                    List<quanAn> listQuanAn = new ArrayList<>();
                    Gson gson = new Gson();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject item = arr.getJSONObject(i);
                        // Dữ liệu quán ăn nằm trong object lồng nhau "nhahang"
                        if (!item.isNull("nhahang")) {
                            JSONObject nhObj = item.getJSONObject("nhahang");
                            quanAn quan = gson.fromJson(nhObj.toString(), quanAn.class);
                            listQuanAn.add(quan);
                        }
                    }
                    callback.onSuccess(listQuanAn);
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getQuanAnByOwner(String idChuNhaHang, QuanAnInfoCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/nhahang?idchunhahang=eq." + idChuNhaHang.trim() + "&select=*";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi: " + response.code());
                        return;
                    }

                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);

                    if (arr.length() > 0) {
                        Gson gson = new Gson();
                        quanAn quan = gson.fromJson(arr.getJSONObject(0).toString(), quanAn.class);
                        callback.onSuccess(quan);
                    } else {
                        callback.onError("Bạn chưa đăng ký quán ăn nào!");
                    }
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }

    public void getDonHangCuaQuan(String idNhaHang, DonHangQuanCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/donhang?idnhahang=eq." + idNhaHang + "&order=thoigian.desc";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi: " + response.code());
                        return;
                    }
                    JSONArray arr = new JSONArray(response.body().string());
                    List<com.example.appgiaodoan.models.donHangQuan> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        list.add(new com.example.appgiaodoan.models.donHangQuan(
                                obj.getString("iddonhang"),
                                obj.optString("thoigian", ""),
                                obj.optDouble("tongtien", 0),
                                obj.optString("trangthai", "")
                        ));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void updateTrangThaiDon(String idDonHang, String trangThaiMoi, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("trangthai", trangThaiMoi);
                RequestBody body = RequestBody.create(JSON, json.toString());
                String url = SUPABASE_URL + "/rest/v1/donhang?iddonhang=eq." + idDonHang;
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .patch(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật thành công");
                    else callback.onError("Lỗi cập nhật: " + response.code());
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    public void getBaoCaoDoanhThu(String idNhaHang, RevenueCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String status = URLEncoder.encode("Hoàn thành", StandardCharsets.UTF_8.toString());
                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "idnhahang=eq." + idNhaHang.trim() +
                        "&trangthai=eq." + status +
                        "&select=tongtien,thoigian,chitietdonhang(soluong,giatien,monan(tenmonan))";
                Log.d(DEBUG_TAG, "👉 URL BÁO CÁO DOANH THU: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        Log.e(DEBUG_TAG, "❌ LỖI HTTP (" + response.code() + "): " + responseBody);

                        callback.onError("Lỗi tải báo cáo: " + response.code() + " - " + responseBody);
                        return;
                    }
                    Log.d(DEBUG_TAG, "✅ DATA BÁO CÁO: " + responseBody);

                    JSONArray arr = new JSONArray(responseBody);
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) list.add(arr.getJSONObject(i));

                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "❌ EXCEPTION BÁO CÁO: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        });
    }
    public void themMonAn(String idNhaHang, String tenMon, double gia, String moTa, String hinhAnh, MonAnCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("idnhahang", idNhaHang);
                json.put("tenmonan", tenMon);
                json.put("giatien", gia);
                json.put("mota", moTa);
                json.put("hinhanh_url", hinhAnh);
                json.put("trangthai", true);

                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/monan")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body).build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Thêm món thành công!");
                    else callback.onError("Lỗi thêm món: " + response.code());
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
    public void suaMonAn(String idMonAn, String tenMon, double gia, String moTa, String hinhAnh, MonAnCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("tenmonan", tenMon);
                json.put("giatien", gia);
                json.put("mota", moTa);
                if (!hinhAnh.isEmpty()) json.put("hinhanh_url", hinhAnh);

                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/monan?idmonan=eq." + idMonAn)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .patch(body).build(); // Dùng PATCH để sửa

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Cập nhật thành công!");
                    else callback.onError("Lỗi cập nhật: " + response.code());
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
    public void xoaMonAn(String idMonAn, MonAnCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/monan?idmonan=eq." + idMonAn)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .delete().build(); // Dùng DELETE

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) callback.onSuccess("Đã xóa món ăn!");
                    else callback.onError("Lỗi xóa: " + response.code());
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
    public void getDanhGiaCuaQuan(String idNhaHang, ModelCallbackDanhGiaList callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/danhgia?" +
                        "idnhahang=eq." + idNhaHang.trim() +
                        "&select=iddanhgia,diem,noidung,thoigian,nguoidung(tennguoidung)" +
                        "&order=thoigian.desc";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi tải đánh giá: " + response.code());
                        return;
                    }
                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);
                    List<com.example.appgiaodoan.models.danhGia> list = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String id = obj.getString("iddanhgia");
                        int diem = obj.getInt("diem");
                        String noidung = obj.optString("noidung", "");
                        String thoigian = obj.optString("thoigian", "");
                        if (thoigian.length() > 10) thoigian = thoigian.substring(0, 10);
                        String tenUser = "Khách hàng";
                        if (!obj.isNull("nguoidung")) {
                            tenUser = obj.getJSONObject("nguoidung").optString("tennguoidung", "Khách hàng");
                        }

                        list.add(new com.example.appgiaodoan.models.danhGia(id, tenUser, diem, noidung, thoigian));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void updateTrangThaiNhaHang(String idNhaHang, boolean isOpen, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("trangthai", isOpen);

                RequestBody body = RequestBody.create(JSON, json.toString());
                String url = SUPABASE_URL + "/rest/v1/nhahang?idnhahang=eq." + idNhaHang;

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Prefer", "return=minimal")
                        .patch(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        callback.onSuccess("Cập nhật trạng thái quán thành công!");
                    } else {
                        callback.onError("Lỗi cập nhật: " + response.code());
                    }
                }
            } catch (Exception e) {
                callback.onError("Lỗi kết nối: " + e.getMessage());
            }
        });
    }
    public void getDonHangChoTaiXe(DriverOrderCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String status = URLEncoder.encode("Đang xử lý", StandardCharsets.UTF_8.toString());
                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "trangthai=eq." + status +
                        "&idtaixe=is.null" +
                        "&select=iddonhang,phigiaohang,tongtien," +
                        "nhahang(tennhahang,diachi,anhdaidien_url)," +
                        "nguoidung(diachi)," + // Lấy địa chỉ khách
                        "chitietdonhang(count)";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) { callback.onError("Lỗi: " + response.code()); return; }

                    String json = response.body().string();
                    JSONArray arr = new JSONArray(json);
                    List<com.example.appgiaodoan.models.donHangTaiXe> list = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String id = obj.getString("iddonhang");
                        double phiShip = obj.optDouble("phigiaohang", 0);
                        double tongTien = obj.optDouble("tongtien", 0);
                        String tenQuan = "Quán ăn";
                        String diaChiQuan = "";
                        String anhQuan = "";
                        if (!obj.isNull("nhahang")) {
                            JSONObject nh = obj.getJSONObject("nhahang");
                            tenQuan = nh.optString("tennhahang", "Quán ăn");
                            diaChiQuan = nh.optString("diachi", "");
                            anhQuan = nh.optString("anhdaidien_url", "");
                        }
                        String diaChiKhach = "";
                        if (!obj.isNull("nguoidung")) {
                            JSONObject nd = obj.getJSONObject("nguoidung");
                            diaChiKhach = nd.optString("diachi", "");
                        }
                        int soLuongMon = 1;
                        if (!obj.isNull("chitietdonhang")) {
                            JSONArray details = obj.getJSONArray("chitietdonhang");
                            soLuongMon = details.length();
                        }

                        list.add(new com.example.appgiaodoan.models.donHangTaiXe(
                                id, tenQuan, anhQuan, diaChiQuan, diaChiKhach, soLuongMon, tongTien, phiShip
                        ));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
    public void taiXeNhanDon(String idDonHang, String idTaiXe, double tienLoi, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonDonHang = new JSONObject();
                jsonDonHang.put("idtaixe", idTaiXe);
                jsonDonHang.put("trangthai", "Đang giao");

                RequestBody bodyDonHang = RequestBody.create(JSON, jsonDonHang.toString());
                String urlDonHang = SUPABASE_URL + "/rest/v1/donhang?iddonhang=eq." + idDonHang;

                Request reqDonHang = new Request.Builder()
                        .url(urlDonHang)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .patch(bodyDonHang).build();

                Response respDonHang = client.newCall(reqDonHang).execute();
                if (!respDonHang.isSuccessful()) {
                    callback.onError("Lỗi cập nhật đơn: " + respDonHang.code());
                    return;
                }
                JSONObject jsonGiaoHang = new JSONObject();
                jsonGiaoHang.put("iddonhang", idDonHang);
                jsonGiaoHang.put("tienloi", tienLoi);
                RequestBody bodyGiaoHang = RequestBody.create(JSON, jsonGiaoHang.toString());
                String urlGiaoHang = SUPABASE_URL + "/rest/v1/chitietgiaohang";

                Request reqGiaoHang = new Request.Builder()
                        .url(urlGiaoHang)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(bodyGiaoHang).build();

                Response respGiaoHang = client.newCall(reqGiaoHang).execute();
                if (respGiaoHang.isSuccessful()) {
                    callback.onSuccess("Đã nhận đơn và lưu hồ sơ giao hàng!");
                } else {
                    callback.onSuccess("Đã nhận đơn (Lỗi lưu chi tiết giao hàng)");
                    Log.e(DEBUG_TAG, "Lỗi tạo chi tiết giao hàng: " + respGiaoHang.body().string());
                }

            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
    public void taiXeHoanThanhDon(String idDonHang, ModelCallbackSimple callback) {
        runOnBackgroundThread(() -> {
            try {
                JSONObject jsonDon = new JSONObject();
                jsonDon.put("trangthai", "Hoàn thành");

                Request req1 = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/donhang?iddonhang=eq." + idDonHang)
                        .addHeader("apikey", SUPABASE_API_KEY).addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .patch(RequestBody.create(JSON, jsonDon.toString())).build();

                client.newCall(req1).execute();
                JSONObject jsonGiao = new JSONObject();
                String now = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new java.util.Date());
                jsonGiao.put("thoigianketthuc", now);

                Request req2 = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/chitietgiaohang?iddonhang=eq." + idDonHang)
                        .addHeader("apikey", SUPABASE_API_KEY).addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .patch(RequestBody.create(JSON, jsonGiao.toString())).build();

                client.newCall(req2).execute();

                callback.onSuccess("Đơn hàng đã hoàn thành!");
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
    public void getChiTietDonHangTaiXe(String idDonHang, DriverOrderDetailCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "iddonhang=eq." + idDonHang +
                        "&select=iddonhang,phigiaohang,tongtien,trangthai," +
                        "nhahang(tennhahang,diachi,sodienthoai)," +
                        "nguoidung(tennguoidung,diachi,sodienthoai)," +
                        "chitietdonhang(soluong,monan(tenmonan))";
                Log.d(DEBUG_TAG, "👉 URL CHI TIẾT GIAO HÀNG: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (!response.isSuccessful()) {
                        // === LOG LỖI CHI TIẾT ===
                        Log.e(DEBUG_TAG, "❌ LỖI HTTP (" + response.code() + "): " + responseBody);
                        callback.onError("Lỗi tải chi tiết: " + responseBody);
                        return;
                    }
                    Log.d(DEBUG_TAG, "✅ DATA CHI TIẾT: " + responseBody);

                    JSONArray arr = new JSONArray(responseBody);
                    if (arr.length() > 0) {
                        callback.onSuccess(arr.getJSONObject(0));
                    } else {
                        callback.onError("Không tìm thấy đơn hàng với ID này");
                    }
                }
            } catch (Exception e) {
                Log.e(DEBUG_TAG, "❌ EXCEPTION: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        });
    }
    public void getLichSuHoanThanhTaiXe(String idTaiXe, IncomeDriverCallback callback) {
        runOnBackgroundThread(() -> {
            try {
                String status = URLEncoder.encode("Hoàn thành", StandardCharsets.UTF_8.toString());
                String url = SUPABASE_URL + "/rest/v1/donhang?" +
                        "idtaixe=eq." + idTaiXe +
                        "&trangthai=eq." + status +
                        "&select=iddonhang,phigiaohang,thoigian,nhahang(tennhahang)" +
                        "&order=thoigian.desc";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Accept", "application/json")
                        .get().build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        callback.onError("Lỗi: " + response.code());
                        return;
                    }
                    JSONArray arr = new JSONArray(response.body().string());
                    List<com.example.appgiaodoan.models.thuNhapTaiXe> list = new ArrayList<>();

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        String id = obj.getString("iddonhang");
                        double phi = obj.optDouble("phigiaohang", 0);
                        String time = obj.optString("thoigian", "");

                        String tenQuan = "Quán ăn";
                        if (!obj.isNull("nhahang")) {
                            tenQuan = obj.getJSONObject("nhahang").optString("tennhahang", "Quán ăn");
                        }

                        list.add(new com.example.appgiaodoan.models.thuNhapTaiXe(id, tenQuan, time, phi));
                    }
                    callback.onSuccess(list);
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        });
    }
}