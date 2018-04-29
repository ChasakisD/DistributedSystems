package gr.aueb.dist.Models;

import com.google.gson.annotations.SerializedName;

public enum MessageType {

    @SerializedName("0")
    HELLO_WORLD (0),
    @SerializedName("1")
    TRANSFER_MATRICES (1),
    @SerializedName("2")
    CALCULATE_X (2),
    @SerializedName("3")
    CALCULATE_Y (3),
    @SerializedName("4")
    X_CALCULATED (4),
    @SerializedName("5")
    Y_CALCULATED (5),
    @SerializedName("6")
    ASK_RECOMMENDATION (6),
    @SerializedName("7")
    REPLY_RECOMMENDATION (7);

    private final int value;
    public int getValue() {
        return value;
    }

    MessageType(int value) {
        this.value = value;
    }
}
