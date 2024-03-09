package org.authentication.Auth.Util;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    USER, // Can update delete self object, read anything
    ADMIN
}
