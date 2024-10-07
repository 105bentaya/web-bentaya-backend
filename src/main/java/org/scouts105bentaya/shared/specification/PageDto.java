package org.scouts105bentaya.shared.specification;

import java.util.List;

public record PageDto<T>(long count, List<T> data) {
}
