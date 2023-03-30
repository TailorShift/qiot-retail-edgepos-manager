package io.hackfest.web.error;

public enum ErrorCode {
    SIGNATURE_VERIFICATION_FAILED(100, "Signature verification failed"),
    SIGNATURE_MISMATCH(101, "Signature mismatch"),
    SIGNATURE_INVALID_TIMESTAMP(102, "Signing time deviates more than 10 seconds from local time"),
    UNKNOWN_DEVICE_ID(103, "Unknown device serial: {0}"),
    UNKNOWN_PRODUCT_ID(200, "Unknown product id: {0,number,#}"),
    INVALID_PRODUCT_COLOR(201, "Color `{0}` is not valid for product id `{1,number,#}`"),
    INSUFFICIENT_STOCK(202, "Stock not sufficient for product id `{0,number,#}` for size `{1}` with color `{2}`");

    private final int code;
    private final String messsage;

    ErrorCode(int code, String title) {
        this.code = code;
        this.messsage = title;
    }

    public int getCode() {
        return code;
    }

    public String getMesssage() {
        return messsage;
    }
}
