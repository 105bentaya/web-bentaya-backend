package org.scouts105bentaya.shared;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class GenericConstants {

    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    public static final ZoneId CANARY_ZONE_ID = ZoneId.of("Atlantic/Canary");
    public static final String FAKE_PASSWORD = "fake_password";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final String NOT_IMPLEMENTED = "Method not implemented";
    public static final int MYSQL_BASIC_VARCHAR_LENGTH = 255;
    public static final int MYSQL_TEXT_LENGTH = 65535;

    private GenericConstants() {
    }
}
