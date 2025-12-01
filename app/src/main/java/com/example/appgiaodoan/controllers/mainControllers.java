package com.example.appgiaodoan.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import com.example.appgiaodoan.models.database;
import com.example.appgiaodoan.models.khuyenMai;
import com.example.appgiaodoan.views.dangki;
import com.example.appgiaodoan.views.dangnhap;
import com.example.appgiaodoan.views.nhapmatkhau;
import com.example.appgiaodoan.models.quanAn;
import com.example.appgiaodoan.models.monAn;
import com.example.appgiaodoan.views.trangchu;
import com.example.appgiaodoan.views.xacthucotp;
import com.example.appgiaodoan.models.lichSuDonHang;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;


public class mainControllers {

    private final database mDatabase;
    private final ktPass mKtPass;
    private boolean OTPMode = false;
    public enum OTP { DANG_NHAP, DANG_KI }

    private static final String MOMO_PARTNER_CODE = "MOMO";
    private static final String MOMO_SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String MOMO_API_URL = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String MOMO_ACCESS_KEY = "F8BBA842ECF85";

    public interface TrangChuViewListener {
        void hienThiDanhSachQuanAn(List<quanAn> danhSach);
        void showError(String message);
    }

    public interface RatingCalculationListener {
        void onRatingCalculated(float ratingTrungBinh, int tongLuotDanhGia);
    }
    public interface TrangChuActiveOrderListener {
        void onShowActiveOrder(String idDonHang, String duKienGiao);
        void onHideActiveOrder();
    }
    public interface DoanhThuListener {
        void onCalculated(double todayRevenue, double monthRevenue, List<com.example.appgiaodoan.models.doanhThuMon> topItems);
        void onError(String message);
    }
    public interface DanhGiaListListener {
        void onDanhGiaLoaded(List<com.example.appgiaodoan.models.danhGia> list);
        void onError(String message);
    }
    public interface LichSuListener {
        void onLichSuLoaded(List<lichSuDonHang> list);
        void onLichSuError(String message);
    }
    public interface AuthViewListenerLogin {
        void showLoading(boolean isLoading);
        void showError(String message);
        void showSuccess(String message, String accessToken, String userId,String vaiTro);
        void dieuHuong(Class<?> activityClass, String... extras);
    }

    public interface KhuyenMaiViewListener {
        void onKhuyenMaiLoaded(List<khuyenMai> danhSach);
        void onKhuyenMaiLoadError(String message);
        void onGiamGiaCalculated(double tongGiam, String maKhuyenMai);
        void onApplyError(String message);
    }

    public interface GioHangListener {
        void onGioHangLoaded(List<monAn> danhSachMonAn, String maNhaHang);
        void onGioHangLoadError(String message);
        void onGioHangCleared(String message);
        void onSuccess(String message);
    }
    public interface QuanLyMonListener {
        void onActionSuccess(String message);
        void onError(String message);
    }
    public interface QuanAnDashboardListener {
        void onInfoLoaded(quanAn quanInfo);
        void onOrdersLoaded(List<com.example.appgiaodoan.models.donHangQuan> list);
        void onError(String message);
        void onStatusUpdated(String message);
    }
    public interface DriverDetailListener {
        void onDetailLoaded(String tenQuan, String dcQuan, String tenKhach, String dcKhach, String sdtKhach, String dsMon, double thuNhap, String trangThaiHienTai);
        void onError(String message);
    }
    public interface AuthViewListenerSimple {
        void showLoading(boolean isLoading);
        void showError(String message);
        void showSuccess(String message);
        void dieuHuong(Class<?> activityClass, String... extras);
    }
    public interface DriverHomeListener {
        void onOrdersLoaded(List<com.example.appgiaodoan.models.donHangTaiXe> list);
        void onOrderAccepted(String message);
        void onError(String message);
    }
    public interface QuanLyDanhGiaListener {
        void onListLoaded(List<com.example.appgiaodoan.models.danhGia> list);
        void onError(String message);
    }
    public interface QuanLyDonListener {
        void onListLoaded(List<com.example.appgiaodoan.models.donHangQuan> list);
        void onStatusUpdated(String message);
        void onError(String message);
    }
    public interface ProfileViewListener {
        void onProfileLoaded(String tenNguoiDung, String diaChi, boolean nhanThongBao);
        void onProfileLoadError(String message);
    }
    public interface MomoPaymentListener {
        void onPaymentUrlReceived(String payUrl);
        void onError(String message);
    }

    public interface ProfileUpdateListener {
        void onUpdateSuccess(String message, String tenMoi);
        void onUpdateError(String message);
    }

    public interface UpdateListener {
        void onUpdateSuccess(String message);
        void onUpdateError(String message);
    }
    public interface YeuThichListener {
        void onYeuThichLoaded(List<quanAn> list);
        void onError(String message);
    }
    public interface ChiTietNhaHangViewListener {
        void hienThiChiTietNhaHang(quanAn nhaHang, List<monAn> danhSachMonAn);
        void showError(String message);
    }
    public interface ReOrderViewListener {
        void onReOrderSuccess(String idNhaHang);
        void onError(String message);
    }
    public interface TimKiemListener {
        void onSuccess(List<quanAn> danhSach);
        void onError(String message);
    }
    public interface DriverIncomeListener {
        void onIncomeLoaded(double today, double month, List<com.example.appgiaodoan.models.thuNhapTaiXe> list);
        void onError(String message);
    }
    public interface SettingsUpdateListener {
        void onSettingsUpdated();
        void onUpdateSuccess(String message);
        void onUpdateError(String message);
        void onEmailUpdateSuccess(String message, String newEmail);
    }

    public mainControllers() {
        this.mDatabase = new database();
        this.mKtPass = new ktPass();
    }

    private void runOnUIThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public boolean isOTPMode() {
        return OTPMode;
    }

    public boolean chinhTrangThaiDangNhap() {
        OTPMode = !OTPMode;
        return OTPMode;
    }
    public void layLichSuDonHang(String userId, LichSuListener listener) {
        mDatabase.getLichSuDonHang(userId, new database.ModelCallbackLichSu() {
            @Override
            public void onSuccess(List<lichSuDonHang> list) {
                runOnUIThread(() -> listener.onLichSuLoaded(list));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onLichSuError(message));
            }
        });
    }

    public void checkDonHangActive(String userId, TrangChuActiveOrderListener listener) {
        mDatabase.kiemTraDonHangDangXuLy(userId, new database.ActiveOrderCallback() {
            @Override
            public void onFound(String idDonHang, String thoiGianDat, double phiGiaoHang) {
                runOnUIThread(() -> listener.onShowActiveOrder(idDonHang, thoiGianDat));
            }

            @Override
            public void onNotFound() {
                runOnUIThread(listener::onHideActiveOrder);
            }

            @Override
            public void onError(String message) {
                runOnUIThread(listener::onHideActiveOrder);
            }
        });
    }
    public void timKiemQuanAnSupabase(String tuKhoa, TimKiemListener listener) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String query = (tuKhoa == null || tuKhoa.isEmpty())
                        ? "?select=*"
                        : "?tennhahang=ilike.*" + tuKhoa + "*.";

                String url = "https://hkjqvbgrjqxenugjuhni.supabase.co/rest/v1/nhahang" + query;

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", database.SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + database.SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    runOnUIThread(() -> listener.onError("Lỗi tải danh sách quán: " + response.code()));
                    return;
                }

                String json = response.body().string();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<quanAn>>() {}.getType();
                List<quanAn> danhSach = gson.fromJson(json, listType);

                runOnUIThread(() -> listener.onSuccess(danhSach));

            } catch (Exception e) {
                runOnUIThread(() -> listener.onError("Lỗi khi tìm kiếm quán ăn: " + e.getMessage()));
            }
        }).start();
    }

    public void layDanhSachQuanAn(TrangChuViewListener listener) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = "https://hkjqvbgrjqxenugjuhni.supabase.co/rest/v1/nhahang?select=*";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", database.SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + database.SUPABASE_API_KEY)
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    runOnUIThread(() -> listener.showError("Lỗi tải danh sách quán: " + response.code()));
                    return;
                }

                String json = response.body().string();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<quanAn>>() {}.getType();
                List<quanAn> danhSach = gson.fromJson(json, listType);

                runOnUIThread(() -> listener.hienThiDanhSachQuanAn(danhSach));

            } catch (Exception e) {
                runOnUIThread(() -> listener.showError("Lỗi khi tải quán ăn: " + e.getMessage()));
            }
        }).start();
    }

    public void layChiTietNhaHang(String maNhaHang, ChiTietNhaHangViewListener listener) {
        new Thread(() -> {
            try {
                quanAn nhaHangDuocTai = mDatabase.getNhaHangChiTiet(maNhaHang);
                List<monAn> danhSachMonAnDuocTai = mDatabase.getDanhSachMonAn(maNhaHang);

                if (nhaHangDuocTai == null) {
                    runOnUIThread(() -> listener.showError("Không tìm thấy thông tin nhà hàng có ID: " + maNhaHang));
                    return;
                }

                runOnUIThread(() -> listener.hienThiChiTietNhaHang(nhaHangDuocTai, danhSachMonAnDuocTai));

            } catch (Exception e) {
                runOnUIThread(() -> listener.showError("Lỗi khi tải chi tiết quán ăn: " + e.getMessage()));
            }
        }).start();
    }

    public void layChiTietGioHang(HashMap<String, Integer> cartQuantities, String maNhaHang, GioHangListener listener) {
        if (cartQuantities.isEmpty()) {
            listener.onGioHangLoaded(new ArrayList<>(), maNhaHang);
            return;
        }

        StringBuilder idsBuilder = new StringBuilder();
        for (String id : cartQuantities.keySet()) {
            if (cartQuantities.getOrDefault(id, 0) > 0) {
                idsBuilder.append(id).append(",");
            }
        }
        String idsMonAn = idsBuilder.length() > 0 ? idsBuilder.substring(0, idsBuilder.length() - 1) : "";

        if (idsMonAn.isEmpty()) {
            listener.onGioHangLoaded(new ArrayList<>(), maNhaHang);
            return;
        }

        mDatabase.getChiTietNhieuMonAn(idsMonAn, new database.ModelCallbackMonAnDanhSach() {
            @Override
            public void onSuccess(List<monAn> danhSach) {
                runOnUIThread(() -> listener.onGioHangLoaded(danhSach, maNhaHang));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onGioHangLoadError(message));
            }
        });
    }

    public void xoaGioHangTam(Context context, GioHangListener listener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        runOnUIThread(() -> listener.onGioHangCleared("Đã xóa giỏ hàng khỏi bộ nhớ tạm."));
    }

    public void xuLyDatDon(Context context, String userId, String maNhaHang, double tongTien, double phiGiaoHang,
                           String sdtNguoiNhan, String phuongThucTT, String maKhuyenMai,
                           HashMap<String, Integer> cartItems,
                           GioHangListener listener) {

        layChiTietGioHang(cartItems, maNhaHang, new GioHangListener() {
            @Override
            public void onGioHangLoaded(List<monAn> danhSachMonAn, String maNhaHang) {
                new Thread(() -> {
                    try {
                        JSONObject thongTinChung = new JSONObject();
                        thongTinChung.put("sodienthoai", sdtNguoiNhan);
                        thongTinChung.put("idnguoidung", userId);
                        thongTinChung.put("idnhahang", maNhaHang);
                        thongTinChung.put("tongtien", tongTien);
                        thongTinChung.put("phigiaohang", phiGiaoHang);
                        thongTinChung.put("phuongthucthanhtoan", phuongThucTT);
                        thongTinChung.put("idtaixe", JSONObject.NULL);

                        if (maKhuyenMai != null && !maKhuyenMai.isEmpty()) {
                            thongTinChung.put("idkhuyenmai", maKhuyenMai);
                        } else {
                            thongTinChung.put("idkhuyenmai", JSONObject.NULL);
                        }
                        thongTinChung.put("trangthai", "Đang xử lý");

                        // --- TẠO JSON CHI TIẾT (SỬA TẠI ĐÂY) ---
                        JSONArray chiTietArray = new JSONArray();
                        for (monAn mon : danhSachMonAn) {
                            int soLuong = cartItems.getOrDefault(mon.getIdMonAn(), 0);
                            if (soLuong > 0) {
                                JSONObject chiTiet = new JSONObject();
                                chiTiet.put("idmonan", mon.getIdMonAn());
                                chiTiet.put("soluong", soLuong);

                                // SỬA: Đổi 'dongia' thành 'giatien' khớp với SQL
                                chiTiet.put("giatien", mon.getGia());

                                chiTietArray.put(chiTiet);
                            }
                        }

                        mDatabase.datDonHang(thongTinChung, chiTietArray, new database.ModelCallbackSimple() {
                            @Override
                            public void onSuccess(String message) {
                                xoaGioHangTam(context, listener);
                                runOnUIThread(() -> listener.onSuccess(message));
                            }

                            @Override
                            public void onError(String message) {
                                runOnUIThread(() -> listener.onGioHangLoadError(message));
                            }
                        });

                    } catch (Exception e) {
                        runOnUIThread(() -> listener.onGioHangLoadError("Lỗi dữ liệu: " + e.getMessage()));
                    }
                }).start();
            }

            @Override
            public void onGioHangLoadError(String message) {
                listener.onGioHangLoadError(message);
            }
            @Override
            public void onGioHangCleared(String message) {}
            @Override
            public void onSuccess(String message) { listener.onSuccess(message); }
        });
    }

    public GiaoHang phiGiaoHang(double kcKM, String loai) {
        if (kcKM < 0) {
            kcKM = 0;
        }
        double Phi_Base = 15000 + (kcKM * 3000);
        double TG_Base = 15 + (kcKM * 2);

        double Phi_Final = Phi_Base;
        double TG_Final = TG_Base;

        switch (loai) {
            case "UuTien":
                Phi_Final = Phi_Base * 1.15;
                TG_Final = TG_Base * 0.80;
                break;
            case "BinhThuong":
                break;
            case "TietKiem":
                Phi_Final = Phi_Base * 0.80;
                TG_Final = TG_Base * 1.30;
                break;
            default:
                break;
        }

        Phi_Final = Math.max(5000, Phi_Final);
        TG_Final = Math.max(10, TG_Final);
        Phi_Final = Math.ceil(Phi_Final / 1000) * 1000;

        return new GiaoHang(Phi_Final, TG_Final);
    }

    public static class GiaoHang {
        public final double phi;
        public final double thoigian;

        public GiaoHang(double phi, double thoigian) {
            this.phi = phi;
            this.thoigian = thoigian;
        }
    }

    public void taoThanhToanMomo(double amount, String orderId, MomoPaymentListener listener) {
        new Thread(() -> {
            try {
                String endpoint = MOMO_API_URL;
                String partnerCode = MOMO_PARTNER_CODE;
                String accessKey = MOMO_ACCESS_KEY;
                String secretKey = MOMO_SECRET_KEY;
                String requestId = String.valueOf(System.currentTimeMillis());
                String orderInfo = "Thanh toán đơn hàng " + orderId;
                String redirectUrl = "https://momo.vn/return";
                String ipnUrl = "https://momo.vn/notify";
                String extraData = "";
                String requestType = "captureWallet";

                String amountStr = String.valueOf((long) amount);

                String rawSignature =
                        "accessKey=" + accessKey +
                                "&amount=" + amountStr +
                                "&extraData=" + extraData +
                                "&ipnUrl=" + ipnUrl +
                                "&orderId=" + orderId +
                                "&orderInfo=" + orderInfo +
                                "&partnerCode=" + partnerCode +
                                "&redirectUrl=" + redirectUrl +
                                "&requestId=" + requestId +
                                "&requestType=" + requestType;

                String signature = HmacSHA256(rawSignature, secretKey);

                JSONObject json = new JSONObject();
                json.put("partnerCode", partnerCode);
                json.put("partnerName", "MoMoTest");
                json.put("storeId", "MoMoTestStore");
                json.put("requestId", requestId);
                json.put("amount", amountStr);
                json.put("orderId", orderId);
                json.put("orderInfo", orderInfo);
                json.put("redirectUrl", redirectUrl);
                json.put("ipnUrl", ipnUrl);
                json.put("lang", "vi");
                json.put("extraData", extraData);
                json.put("requestType", requestType);
                json.put("signature", signature);

                URL url = new URL(endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject response = new JSONObject(sb.toString());

                if (response.has("payUrl")) {
                    listener.onPaymentUrlReceived(response.getString("payUrl"));
                } else {
                    listener.onError("Không nhận được payUrl từ MoMo");
                }

            } catch (Exception e) {
                runOnUIThread(() -> listener.onError("Lỗi MoMo: " + e.getMessage()));
            }
        }).start();
    }

    private String HmacSHA256(String data, String key) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes());
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public void dangNhap(String sdt, String matKhau, AuthViewListenerLogin listener) {
        listener.showLoading(true);

        if (!OTPMode) {
            if (sdt.isEmpty() || matKhau.isEmpty()) {
                listener.showError("Vui lòng nhập đầy đủ SĐT và Mật khẩu!");
                return;
            }
            mDatabase.dangNhap(sdt, matKhau, new database.ModelCallbackLogin() {
                @Override
                public void onSuccess(String message, String accessToken, String userId,String vaitro) {
                    runOnUIThread(() -> {
                        listener.showSuccess(message, accessToken, userId, vaitro);
                        listener.showLoading(false);
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUIThread(() -> {
                        listener.showError(message);
                        listener.showLoading(false);
                    });
                }
            });
        } else {
            if (sdt.isEmpty()) {
                listener.showError("Vui lòng nhập Số điện thoại!");
                listener.showLoading(false);
                return;
            }
            guiOTP(sdt, (AuthViewListenerSimple) listener, xacthucotp.class, OTP.DANG_NHAP);
        }
    }

    public void guiOTP(String sdt, AuthViewListenerSimple listener, Class<?> classRedirect, OTP chuyen) {
        if (sdt.isEmpty()) {
            listener.showError("Vui lòng nhập số điện thoại!");
            return;
        }
        listener.showLoading(true);
        mDatabase.guiOTP(sdt, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> {
                    listener.showSuccess(message);
                    listener.showLoading(false);
                    listener.dieuHuong(classRedirect, "phone_number", sdt);
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> {
                    listener.showError(message);
                    listener.showLoading(false);
                });
            }
        });
    }

    public void xacThucOTP(String sdt, String otp, AuthViewListenerSimple listener, OTP chuyen) {
        if (otp.isEmpty() || otp.length() != 6) {
            listener.showError("Vui lòng nhập OTP 6 ký tự!");
            return;
        }
        listener.showLoading(true);
        mDatabase.xacThucOTP(sdt, otp, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> {
                    listener.showSuccess(message);
                    listener.showLoading(false);
                    if (chuyen == OTP.DANG_KI) {
                        listener.dieuHuong(nhapmatkhau.class, "phone_number", sdt);
                    } else if (chuyen == OTP.DANG_NHAP) {
                        listener.dieuHuong(trangchu.class);
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> {
                    listener.showError(message);
                    listener.showLoading(false);
                });
            }
        });
    }

    public void dangKiTaiKhoanMoi(String sdt, String matKhau, String xacNhanMatKhau, AuthViewListenerSimple listener) {
        String validateError = mKtPass.ktDoPhucTap(matKhau);
        if (validateError != null) {
            listener.showError(validateError);
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            listener.showError("Mật khẩu không trùng khớp!");
            return;
        }

        listener.showLoading(true);
        mDatabase.dangKi(sdt, matKhau, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> {
                    listener.showSuccess(message);
                    listener.showLoading(false);
                    listener.dieuHuong(dangnhap.class);
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> {
                    listener.showError(message);
                    listener.showLoading(false);
                });
            }
        });
    }

    public void layThongTinNguoiDung(String idNguoiDung, ProfileViewListener listener) {
        mDatabase.getNguoiDungProfile(idNguoiDung, new database.ModelCallbackProfile() {
            @Override
            public void onSuccess(JSONObject profileData) {
                runOnUIThread(() -> {
                    String ten = profileData.optString("tennguoidung", "Người dùng");
                    String diaChi = profileData.optString("diachi", null);
                    boolean nhanThongBao = profileData.optBoolean("nhanthongbao", true);
                    listener.onProfileLoaded(ten, diaChi, nhanThongBao);
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onProfileLoadError(message));
            }
        });
    }

    public void xuLyCapNhatTen(String idNguoiDung, String tenMoi, ProfileUpdateListener listener) {
        if (tenMoi.isEmpty()) {
            listener.onUpdateError("Tên không được để trống.");
            return;
        }

        mDatabase.capNhatTenNguoiDung(idNguoiDung, tenMoi, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onUpdateSuccess(message, tenMoi));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onUpdateError(message));
            }
        });
    }

    public void xuLyDoiMatKhau(String idNguoiDung, String matKhauMoi, String xacNhanMKMoi, SettingsUpdateListener listener) {
        String validateError = mKtPass.ktDoPhucTap(matKhauMoi);
        if (validateError != null) {
            listener.onUpdateError(validateError);
            return;
        }
        if (!matKhauMoi.equals(xacNhanMKMoi)) {
            listener.onUpdateError("Mật khẩu không trùng khớp!");
            return;
        }

        mDatabase.capNhatMatKhauProfile(idNguoiDung, matKhauMoi, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String messageProfile) {
                runOnUIThread(() -> listener.onUpdateSuccess("Đổi mật khẩu thành công!"));
            }

            @Override
            public void onError(String messageProfile) {
                runOnUIThread(() -> listener.onUpdateError(messageProfile));
            }
        });
    }

    public void xuLyDoiEmail(String idNguoiDung, String emailMoi, SettingsUpdateListener listener) {
        if (emailMoi.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailMoi).matches()) {
            listener.onUpdateError("Vui lòng nhập email hợp lệ!");
            return;
        }

        mDatabase.capNhatEmailAuthVaProfile(idNguoiDung, emailMoi, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onEmailUpdateSuccess(message, emailMoi));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onUpdateError(message));
            }
        });
    }

    public void xuLyDoiThongBao(String idNguoiDung, boolean nhanThongBao, SettingsUpdateListener listener) {
        mDatabase.capNhatThongBao(idNguoiDung, nhanThongBao, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onUpdateSuccess(message));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onUpdateError(message));
            }
        });
    }

    public void chuyenTrangDangKi(Context context) {
        Intent intent = new Intent(context, dangki.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void xuLyCapNhatDiaChi(String idNguoiDung, String diaChiNhap, AuthViewListenerSimple listener) {
        if (diaChiNhap.isEmpty()) {
            listener.showError("Vui lòng nhập địa chỉ.");
            return;
        }

        listener.showLoading(true);

        mDatabase.capNhatDiaChiNguoiDung(idNguoiDung, diaChiNhap, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> {
                    listener.showSuccess(message);
                    listener.showLoading(false);
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> {
                    listener.showError(message);
                    listener.showLoading(false);
                });
            }
        });
    }

    public void layDanhSachKhuyenMai(String idNhaHang, KhuyenMaiViewListener listener) {
        mDatabase.getDanhSachKhuyenMai(idNhaHang, new database.ModelCallbackKhuyenMai() {
            @Override
            public void onSuccess(List<khuyenMai> danhSach) {
                runOnUIThread(() -> listener.onKhuyenMaiLoaded(danhSach));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onKhuyenMaiLoadError(message));
            }
        });
    }

    public void tinhToanGiamGia(String maKhuyenMai, double tongTienDonHang, khuyenMai khuyenMai, KhuyenMaiViewListener listener) {
        if (khuyenMai == null) {
            listener.onApplyError("Mã khuyến mãi không tồn tại hoặc đã hết hạn.");
            return;
        }
        double tongGiam = 0;
        double giaTriToiThieu = khuyenMai.getGiaTri();
        if (giaTriToiThieu > 0 && tongTienDonHang < giaTriToiThieu) {
            listener.onApplyError("Đơn hàng chưa đạt giá trị tối thiểu " + String.format("%,.0f VNĐ", giaTriToiThieu));
            return;
        }

        String loai = khuyenMai.getLoaiKhuyenMai();

        if ("FIXED_AMOUNT".equals(loai)) {
            tongGiam = khuyenMai.getSoTienKhuyenMai();

        } else if ("PERCENTAGE".equals(loai)) {
            double phanTramGiam = khuyenMai.getSoTienKhuyenMai();

            double giamToiDa = khuyenMai.getGiamToiDa();

            tongGiam = tongTienDonHang * phanTramGiam;
            if (giamToiDa > 0 && tongGiam > giamToiDa) {
                tongGiam = giamToiDa;
            }
        }

        double finalTongGiam = tongGiam;
        runOnUIThread(() -> listener.onGiamGiaCalculated(finalTongGiam, maKhuyenMai));
    }
    public void xuLyDatLai(Context context, String idDonHang, ReOrderViewListener listener) {
        mDatabase.layDuLieuDatLai(idDonHang, new database.ReOrderCallback() {
            @Override
            public void onSuccess(String idNhaHang, HashMap<String, Integer> cartItems) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("GioHangPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String jsonQuantities = new Gson().toJson(cartItems);
                editor.putString("CURRENT_CART_ITEMS", jsonQuantities);
                editor.putString("CURRENT_MA_NHA_HANG", idNhaHang);
                editor.apply();

                runOnUIThread(() -> listener.onReOrderSuccess(idNhaHang));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }

    public void xuLyDanhGia(String idDonHang, String idNguoiDung, int diem, String noiDung, UpdateListener listener) {

        mDatabase.getLichSuDonHang(idNguoiDung, new database.ModelCallbackLichSu() {
            @Override
            public void onSuccess(List<com.example.appgiaodoan.models.lichSuDonHang> list) {
                String idNhaHang = "";
                for(com.example.appgiaodoan.models.lichSuDonHang item : list) {
                    if(item.getIdDonHang().equals(idDonHang)) {

                    }
                }
            }
            @Override public void onError(String message) {}
        });
    }

    public void xuLyDanhGiaTrucTiep(String idDonHang, String idNguoiDung, String idNhaHang, int diem, String noiDung, UpdateListener listener) {
        mDatabase.guiDanhGia(idDonHang, idNguoiDung, idNhaHang, diem, noiDung, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onUpdateSuccess(message));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onUpdateError(message));
            }
        });
    }
    public void layDanhSachDanhGia(String idNhaHang, DanhGiaListListener listener) {
        mDatabase.getDanhSachDanhGia(idNhaHang, new database.ModelCallbackDanhGiaList() {
            @Override
            public void onSuccess(List<com.example.appgiaodoan.models.danhGia> list) {
                runOnUIThread(() -> listener.onDanhGiaLoaded(list));
            }
            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void tinhDiemDanhGia(String idNhaHang, RatingCalculationListener listener) {
        mDatabase.getDiemDanhGia(idNhaHang, new database.RatingListCallback() {
            @Override
            public void onSuccess(List<Integer> danhSachDiem) {
                if (danhSachDiem.isEmpty()) {
                    runOnUIThread(() -> listener.onRatingCalculated(5.0f, 0));
                    return;
                }

                double tongDiem = 0;
                for (int diem : danhSachDiem) {
                    tongDiem += diem;
                }
                double trungBinh = tongDiem / danhSachDiem.size();
                float ketQuaLamTron = Math.round(trungBinh);

                runOnUIThread(() -> listener.onRatingCalculated(ketQuaLamTron, danhSachDiem.size()));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onRatingCalculated(4.5f, 0));
            }
        });
    }
    public void layDanhSachYeuThich(String userId, YeuThichListener listener) {
        mDatabase.getDanhSachYeuThich(userId, new database.ModelCallbackDanhSach() {
            @Override
            public void onSuccess(List<quanAn> danhSachQuanAn) {
                runOnUIThread(() -> listener.onYeuThichLoaded(danhSachQuanAn));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void taiDuLieuQuanAn(String userId, QuanAnDashboardListener listener) {
        mDatabase.getQuanAnByOwner(userId, new database.QuanAnInfoCallback() {
            @Override
            public void onSuccess(quanAn quanInfo) {
                runOnUIThread(() -> {
                    listener.onInfoLoaded(quanInfo);

                    mDatabase.getDonHangCuaQuan(quanInfo.getIdNhaHang(), new database.DonHangQuanCallback() {
                        @Override
                        public void onSuccess(List<com.example.appgiaodoan.models.donHangQuan> list) {
                            runOnUIThread(() -> listener.onOrdersLoaded(list));
                        }
                        @Override
                        public void onError(String message) {
                            runOnUIThread(() -> listener.onError(message));
                        }
                    });
                });
            }
            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }

    public void capNhatTrangThaiDon(String idDonHang, String trangThai, QuanAnDashboardListener listener) {
        mDatabase.updateTrangThaiDon(idDonHang, trangThai, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onStatusUpdated(message));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void layDanhSachDonCuaQuan(String idNhaHang, QuanLyDonListener listener) {
        mDatabase.getDonHangCuaQuan(idNhaHang, new database.DonHangQuanCallback() {
            @Override
            public void onSuccess(List<com.example.appgiaodoan.models.donHangQuan> list) {
                runOnUIThread(() -> listener.onListLoaded(list));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }

    public void capNhatTrangThaiDonQuan(String idDonHang, String trangThai, QuanLyDonListener listener) {
        mDatabase.updateTrangThaiDon(idDonHang, trangThai, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onStatusUpdated(message));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void tinhToanDoanhThu(String idNhaHang, DoanhThuListener listener) {
        mDatabase.getBaoCaoDoanhThu(idNhaHang, new database.RevenueCallback() {
            @Override
            public void onSuccess(List<JSONObject> rawData) {
                double totalToday = 0;
                double totalMonth = 0;
                HashMap<String, com.example.appgiaodoan.models.doanhThuMon> mapMonAn = new HashMap<>();

                // Ngày hiện tại
                String today = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new java.util.Date());
                String currentMonth = today.substring(0, 7); // yyyy-MM

                try {
                    for (JSONObject order : rawData) {
                        double tongTienDon = order.optDouble("tongtien", 0);
                        String thoiGian = order.optString("thoigian", ""); // 2025-11-27T...

                        // Tính tổng doanh thu
                        if (thoiGian.startsWith(today)) totalToday += tongTienDon;
                        if (thoiGian.startsWith(currentMonth)) totalMonth += tongTienDon;
                        if (thoiGian.startsWith(currentMonth)) {
                            JSONArray details = order.optJSONArray("chitietdonhang");
                            if (details != null) {
                                for (int j = 0; j < details.length(); j++) {
                                    JSONObject det = details.getJSONObject(j);
                                    int sl = det.optInt("soluong", 0);
                                    double gia = det.optDouble("giatien", 0);

                                    // Lấy tên món từ nested object
                                    String tenMon = "Món đã xóa";
                                    if (!det.isNull("monan")) {
                                        tenMon = det.getJSONObject("monan").optString("tenmonan", "Không tên");
                                    }

                                    // Cộng dồn vào Map
                                    if (mapMonAn.containsKey(tenMon)) {
                                        mapMonAn.get(tenMon).add(sl, sl * gia);
                                    } else {
                                        mapMonAn.put(tenMon, new com.example.appgiaodoan.models.doanhThuMon(tenMon, sl, sl * gia));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }

                List<com.example.appgiaodoan.models.doanhThuMon> listItems = new ArrayList<>(mapMonAn.values());
                listItems.sort((o1, o2) -> Double.compare(o2.getTongTien(), o1.getTongTien()));

                double finalToday = totalToday;
                double finalMonth = totalMonth;
                runOnUIThread(() -> listener.onCalculated(finalToday, finalMonth, listItems));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void layDanhSachMonCuaQuan(String idNhaHang, ChiTietNhaHangViewListener listener) {
        new Thread(() -> {
            try {
                List<monAn> list = mDatabase.getDanhSachMonAn(idNhaHang);
                runOnUIThread(() -> {
                    listener.hienThiChiTietNhaHang(new quanAn(), list);
                });

            } catch (Exception e) {
                runOnUIThread(() -> listener.showError("Lỗi tải món: " + e.getMessage()));
            }
        }).start();
    }

    public void themMonMoi(String idNhaHang, String ten, double gia, String moTa, String linkAnh, QuanLyMonListener listener) {
        mDatabase.themMonAn(idNhaHang, ten, gia, moTa, linkAnh, new database.MonAnCallback() {
            @Override public void onSuccess(String message) { runOnUIThread(() -> listener.onActionSuccess(message)); }
            @Override public void onError(String message) { runOnUIThread(() -> listener.onError(message)); }
        });
    }

    public void capNhatMon(String idMonAn, String ten, double gia, String moTa, String linkAnh, QuanLyMonListener listener) {
        mDatabase.suaMonAn(idMonAn, ten, gia, moTa, linkAnh, new database.MonAnCallback() {
            @Override public void onSuccess(String message) { runOnUIThread(() -> listener.onActionSuccess(message)); }
            @Override public void onError(String message) { runOnUIThread(() -> listener.onError(message)); }
        });
    }

    public void xoaMon(String idMonAn, QuanLyMonListener listener) {
        mDatabase.xoaMonAn(idMonAn, new database.MonAnCallback() {
            @Override public void onSuccess(String message) { runOnUIThread(() -> listener.onActionSuccess(message)); }
            @Override public void onError(String message) { runOnUIThread(() -> listener.onError(message)); }
        });
    }
    public void layDanhGiaCuaQuan(String idNhaHang, QuanLyDanhGiaListener listener) {
        mDatabase.getDanhGiaCuaQuan(idNhaHang, new database.ModelCallbackDanhGiaList() {
            @Override
            public void onSuccess(List<com.example.appgiaodoan.models.danhGia> list) {
                runOnUIThread(() -> listener.onListLoaded(list));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void capNhatTrangThaiCuaHang(String idNhaHang, boolean isOpen, QuanAnDashboardListener listener) {
        mDatabase.updateTrangThaiNhaHang(idNhaHang, isOpen, new database.ModelCallbackSimple() {
            @Override
            public void onSuccess(String message) {
                runOnUIThread(() -> listener.onStatusUpdated(message));
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void layDonHangChoTaiXe(DriverHomeListener listener) {
        mDatabase.getDonHangChoTaiXe(new database.DriverOrderCallback() {
            @Override
            public void onSuccess(List<com.example.appgiaodoan.models.donHangTaiXe> list) {
                runOnUIThread(() -> listener.onOrdersLoaded(list));
            }
            @Override public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }

    public void taiXeNhanDon(String idDonHang, String userId, double phiGiaoHang, DriverHomeListener listener) {

        mDatabase.getTaiXeIdByUserId(userId, new database.TaiXeInfoCallback() {
            @Override
            public void onSuccess(String realIdTaiXe) {

                // TÍNH TIỀN LỜI (40% phí ship)
                double tienLoi = phiGiaoHang * 0.40;

                // Gọi database với tiền lời đã tính
                mDatabase.taiXeNhanDon(idDonHang, realIdTaiXe, tienLoi, new database.ModelCallbackSimple() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUIThread(() -> listener.onOrderAccepted(message));
                    }

                    @Override
                    public void onError(String message) {
                        runOnUIThread(() -> listener.onError(message));
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void layChiTietDonTaiXe(String idDonHang, DriverDetailListener listener) {
        mDatabase.getChiTietDonHangTaiXe(idDonHang, new database.DriverOrderDetailCallback() {
            @Override
            public void onSuccess(JSONObject data) {
                try {
                    double phiShip = data.optDouble("phigiaohang", 0);
                    double thuNhap = phiShip * 0.4; // 40%
                    String trangThai = data.optString("trangthai", "");

                    JSONObject nh = data.optJSONObject("nhahang");
                    String tenQuan = nh != null ? nh.optString("tennhahang") : "";
                    String dcQuan = nh != null ? nh.optString("diachi") : "";

                    JSONObject kh = data.optJSONObject("nguoidung");
                    String tenKhach = kh != null ? kh.optString("tennguoidung") : "Khách hàng";
                    String dcKhach = kh != null ? kh.optString("diachi") : "";
                    String sdtKhach = kh != null ? kh.optString("sodienthoai") : "";
                    StringBuilder sbMon = new StringBuilder();
                    JSONArray ctdh = data.optJSONArray("chitietdonhang");
                    if (ctdh != null) {
                        for (int i = 0; i < ctdh.length(); i++) {
                            JSONObject item = ctdh.getJSONObject(i);
                            int sl = item.optInt("soluong");
                            JSONObject mon = item.optJSONObject("monan");
                            String tenMon = mon != null ? mon.optString("tenmonan") : "";
                            sbMon.append("- ").append(tenMon).append(" (x").append(sl).append(")\n");
                        }
                    }

                    runOnUIThread(() -> listener.onDetailLoaded(tenQuan, dcQuan, tenKhach, dcKhach, sdtKhach, sbMon.toString(), thuNhap, trangThai));

                } catch (Exception e) {
                    runOnUIThread(() -> listener.onError("Lỗi xử lý dữ liệu: " + e.getMessage()));
                }
            }
            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
    public void tinhThuNhapTaiXe(String userId, DriverIncomeListener listener) {
        mDatabase.getTaiXeIdByUserId(userId, new database.TaiXeInfoCallback() {
            @Override
            public void onSuccess(String idTaiXe) {
                // B2: Lấy lịch sử đơn
                mDatabase.getLichSuHoanThanhTaiXe(idTaiXe, new database.IncomeDriverCallback() {
                    @Override
                    public void onSuccess(List<com.example.appgiaodoan.models.thuNhapTaiXe> list) {
                        double totalToday = 0;
                        double totalMonth = 0;
                        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new java.util.Date());
                        String currentMonth = today.substring(0, 7);

                        for (com.example.appgiaodoan.models.thuNhapTaiXe item : list) {
                            double loi = item.getTienLoi();
                            if (item.getThoiGian().startsWith(today)) totalToday += loi;
                            if (item.getThoiGian().startsWith(currentMonth)) totalMonth += loi;
                        }
                        double finalToday = totalToday;
                        double finalMonth = totalMonth;
                        runOnUIThread(() -> listener.onIncomeLoaded(finalToday, finalMonth, list));
                    }
                    @Override
                    public void onError(String message) {
                        runOnUIThread(() -> listener.onError(message));
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUIThread(() -> listener.onError(message));
            }
        });
    }
}