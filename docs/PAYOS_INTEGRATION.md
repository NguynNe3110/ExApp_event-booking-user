# PayOS Integration Setup

## App Configuration

### Android Deep Link Setup (Đã hoàn tất)

App đã hỗ trợ nhận callback từ PayOS thông qua deep link:

- **Scheme**: `customer`
- **Host**: `payment-status`
- **Format**: `customer://payment-status?orderCode=<orderCode>&status=<status>`

#### File cấu hình:

- `app/src/main/AndroidManifest.xml` - khai báo intent-filter
- `app/src/main/java/com/uzuu/customer/feature/MainActivity.kt` - xử lý deep link
- `app/src/main/java/com/uzuu/customer/core/constants/PaymentConstants.kt` - constants cấu hình

### Backend cần cấu hình ở PayOS Dashboard

#### 1. Return URL (thanh toán thành công)

```
https://<backend-public-domain>/api/v1/payments/redirect?orderCode=<orderCode>&status=success
```

Backend sẽ redirect URL này về app deep link:

```
customer://payment-status?orderCode=<orderCode>&status=success
```

#### 2. Cancel URL (người dùng hủy)

```
https://<backend-public-domain>/api/v1/payments/redirect?orderCode=<orderCode>&status=cancel
```

Backend sẽ redirect URL này về app deep link:

```
customer://payment-status?orderCode=<orderCode>&status=cancel
```

### Backend Endpoint Implementation

Backend cần tạo endpoint `/api/v1/payments/redirect` để:

1. Nhận `orderCode` và `status` từ PayOS redirect
2. Cập nhật trạng thái đơn hàng trong DB
3. Redirect browser quay lại app thông qua deep link:

```
Location: customer://payment-status?orderCode=<orderCode>&status=<status>
```

### Mobile App Checkout Flow (Cập nhật mới)

**Quan trọng:** Khi mobile app gọi checkout endpoint, PHẢI gửi parameter `platform=mobile` để backend biết cách xử lý redirect:

#### Step 1: Mobile app gọi checkout endpoint

```
POST https://be-event-mng-v3-production.up.railway.app/bookings/checkout?paymentMethod=PAYOS&platform=mobile
```

**Ghi chú:** Parameter `platform=mobile` là bắt buộc để backend biết redirect về mobile deep link.

#### Step 2: Backend trả về OrderResponse với paymentUrl

```json
{
    "code": 1000,
    "message": null,
    "result": {
        "id": "ORDER123",
        "paymentUrl": "https://pay.payos.vn/web/abc123...",
        "paymentStatus": "PENDING",
        "orderStatus": "PENDING"
    }
}
```

#### Step 3: Mobile app mở PayOS payment link

App dùng deep link hoặc browser intent để mở:

```
https://pay.payos.vn/web/abc123...
```

#### Ví dụ flow chi tiết:

```
1. Mobile app gọi checkout endpoint và gửi platform=mobile:
   POST https://be-event-mng-v3-production.up.railway.app/bookings/checkout?paymentMethod=PAYOS&platform=mobile

2. Backend tạo order và trả về paymentUrl:
   Response: { paymentUrl: "https://pay.payos.vn/web/abc123..." }

3. Mobile app mở PayOS payment link:
   https://pay.payos.vn/web/abc123...

4. Người dùng thanh toán xong, PayOS redirect đến backend:
   https://<backend-public-domain>/api/v1/payments/redirect?orderCode=ORDER123&status=success

5. Backend nhận request từ PayOS, kiểm tra platform từ ORDER123:
   - Nếu platform=mobile: Backend redirect về deep link
     Location: customer://payment-status?orderCode=ORDER123&status=success

   - Nếu platform=web: Backend redirect về web URL
     Location: https://frontend-web.com/payment-status?orderCode=ORDER123&status=success

6. Trên mobile, Android mở deep link:
   customer://payment-status?orderCode=ORDER123&status=success
   → App nhận intent và hiển thị kết quả thanh toán
```

### Query Parameters

| Parameter   | Giá trị             | Mô tả                 |
| ----------- | ------------------- | --------------------- |
| `orderCode` | string              | Mã đơn hàng từ PayOS  |
| `status`    | `success`, `cancel` | Trạng thái thanh toán |

### App Response Handler

Khi app nhận deep link:

- **Status = success/paid**: Hiển thị "Thanh toán thành công: #<orderCode>"
- **Status = cancel/canceled**: Hiển thị "Đã hủy thanh toán: #<orderCode>"
- **Khác**: Hiển thị "Đã quay lại ứng dụng"

### Nếu cần thay đổi Deep Link Scheme/Host

Tôi sửa 2 chỗ:

1. **PaymentConstants.kt**:

```kotlin
const val PAYMENT_DEEP_LINK_SCHEME = "customer"  // thay đổi nếu cần
const val PAYMENT_DEEP_LINK_HOST = "payment-status"  // thay đổi nếu cần
```

2. **AndroidManifest.xml**:

```xml
<data
    android:scheme="customer"
    android:host="payment-status" />
```

3. **Backend returnUrl/cancelUrl** - thay đổi scheme+host tương ứng.

### Notes

- ✅ App đã hỗ trợ deep link callback
- ✅ App đã chuẩn hóa payment method (VietQR → PAYOS)
- ✅ **Backend đã thêm parameter `platform` vào checkout endpoints** - Mobile app PHẢI gửi `platform=mobile` để backend biết cách redirect
- ✅ Endpoint production hiện tại: `https://be-event-mng-v3-production.up.railway.app/bookings/checkout?paymentMethod=PAYOS&platform=mobile`
- ⏳ Backend cần cấu hình returnUrl/cancelUrl ở PayOS dashboard
- ⏳ Backend cần implement `/api/v1/payments/redirect` endpoint
- ⚠️ Backend URL phải là public HTTPS (không localhost)
