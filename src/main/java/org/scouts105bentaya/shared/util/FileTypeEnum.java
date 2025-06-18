package org.scouts105bentaya.shared.util;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

public enum FileTypeEnum {
    DOC_TYPE(FileUtils.DOC_TYPES),
    IMG_TYPE(FileUtils.IMG_TYPES),
    EXCEL_TYPE(FileUtils.EXCEL_TYPES),
    PDF_TYPE(FileUtils.PDF_TYPES);

    private final Map<String, String> validTypes;

    FileTypeEnum(Map<String, String> validTypes) {
        this.validTypes = validTypes;
    }

    public boolean fileIsValid(MultipartFile file) {
        if (file.getContentType() == null) return false;
        return validTypes.containsValue(file.getContentType());
    }

    public Set<String> validExtensions() {
        return validTypes.keySet();
    }
}
