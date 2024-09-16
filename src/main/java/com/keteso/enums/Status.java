package com.keteso.enums;

import lombok.Getter;

@Getter
public enum Status {
    INACTIVE(0), ACTIVE(1), DELETE(2), FREEZE(3), UNFREEZE(4), SUSPEND(5), UNSUSPEND(6), OPTOUT(7);
    private final int value;

    public int getValue() {
        return value;
    }

    Status(int value) {
        this.value = value;
    }
}

