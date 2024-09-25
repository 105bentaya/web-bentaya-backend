package org.scouts105bentaya.specification.util;

import java.util.List;

public record PageDto<T>(long count, List<T> data) {
}
