package org.scouts105bentaya.shared.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailUtils {

    private EmailUtils() {
    }

    public static String subjectWithDateTime(String subject) {
        return "%s - [%s]".formatted(subject, LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy-HH:mm:ss")));
    }
}
