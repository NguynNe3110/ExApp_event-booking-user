# Payment APIs

Tai lieu nay mo ta luong thanh toan hien tai de team app co the tich hop dung cach.

## Tong quan luong thanh toan

### PAYOS flow

1. Frontend goi API checkout trong nhom bookings voi `paymentMethod = PAYOS`.
2. Backend tao don hang o trang thai `PENDING` va tra ve `paymentUrl`.
3. Frontend redirect nguoi dung sang `paymentUrl` cua PayOS.
4. PayOS goi webhook ve backend sau khi nguoi dung thanh toan xong.
5. Backend cap nhat don sang `PAID` va `CONFIRMED`.

### VietQR flow

1. Frontend goi API checkout trong nhom bookings voi `paymentMethod = VIETQR`.
2. Backend tao don hang o trang thai `PENDING` va tra ve `paymentUrl` chua QR string (duoc ma hoa).
3. Frontend giai ma va hien thi QR code cho nguoi dung.
4. Nguoi dung scan QR code tren app ngan hang va hoan thanh thanh toan.
5. Webhook tu ngan hang hoac app thanh toan se cap nhat trang thai don sang `PAID`.
6. Backend cap nhat don sang `CONFIRMED`.

## Checkout co tra paymentUrl

- POST /bookings/checkout?paymentMethod={paymentMethod}&voucherCode={voucherCode}
    - Query `paymentMethod`: MOMO | VNPAY | PAYOS | VIETQR
    - Query `voucherCode`: optional
    - Response: ApiResponse<OrderResponse>
    - Ghi chu: chi khi `paymentMethod = PAYOS` hoac `VIETQR` thi `result.paymentUrl` moi co gia tri.
        - PAYOS: paymentUrl la URL cua trang thanh toan PayOS
        - VIETQR: paymentUrl la QR string (duoc ma hoa), frontend phai giai ma va hien thi

- POST /bookings/checkout-selected?paymentMethod={paymentMethod}&voucherCode={voucherCode}
    - Body: List<Long> itemIds
    - Query `paymentMethod`: MOMO | VNPAY | PAYOS | VIETQR
    - Query `voucherCode`: optional
    - Response: ApiResponse<OrderResponse>
    - Ghi chu: chi khi `paymentMethod = PAYOS` hoac `VIETQR` thi `result.paymentUrl` moi co gia tri.

## PayOS webhook

- POST /api/v1/payments/payos-webhook
    - Auth: public
    - Body: webhook payload tu PayOS
    - Response: `200 OK`
    - Ghi chu: endpoint nay duoc backend dung de nhan callback thong bao giao dich thanh cong.

## VietQR webhook

- POST /api/v1/payments/vietqr-webhook
    - Auth: public
    - Body: webhook payload tu ngan hang hoac payment gateway
    - Response: `200 OK`
    - Ghi chu: endpoint nay duoc backend dung de nhan callback thong bao giao dich thanh cong.

## Example: OrderResponse khi thanh toan PAYOS

```json
{
    "code": 1000,
    "message": null,
    "result": {
        "id": "0192837465",
        "organizerAmount": 187500,
        "platformFeeRate": 0.25,
        "serviceFee": 62500,
        "totalAmount": 250000,
        "paymentMethod": "PAYOS",
        "paymentStatus": "PENDING",
        "orderStatus": "PENDING",
        "orderDate": "2026-05-02T10:00:00",
        "paymentUrl": "https://pay.payos.vn/web/abc123"
    }
}
```

## Example: OrderResponse khi thanh toan VietQR

```json
{
    "code": 1000,
    "message": null,
    "result": {
        "id": "0192837466",
        "organizerAmount": 187500,
        "platformFeeRate": 0.25,
        "serviceFee": 62500,
        "totalAmount": 250000,
        "paymentMethod": "VIETQR",
        "paymentStatus": "PENDING",
        "orderStatus": "PENDING",
        "orderDate": "2026-05-02T10:05:00",
        "paymentUrl": "00020101021238540010A000000727012700069704160413ORDER-ID5802VN5913Company Name6009Ho Chi Minh61080101062208090612345678670360010A0000072703140112345678901520412620000000000VND63041D7C"
    }
}
```

Note: `paymentUrl` trong VietQR response la QR string theo chuan VIETQR. Frontend phai:

1. Giai ma string nay thanh QR code
2. Hien thi QR code de nguoi dung scan bang ung dung ngan hang
3. Hoac truyen string nay toi library sinh QR code de hien thi

## Gia tri can luu y cho frontend

- `paymentUrl`:
    - PAYOS: URL de mo trang thanh toan PayOS.
    - VIETQR: QR string de hien thi QR code hoac truyen cho library sinh QR code.
- `paymentStatus`: luc moi tao don la `PENDING`.
- `orderStatus`: luc moi tao don la `PENDING`.
- Sau webhook thanh cong, backend se cap nhat don sang `PAID` va `CONFIRMED`.
