package org.scouts105bentaya.utils;

import org.scouts105bentaya.features.group.Group;

public class GroupUtils {

    public static Group basicGroup() {
        return new Group()
            .setId(1)
            .setName("GARAJONAY")
            .setEmail("correo@correo.com")
            .setOrder(1);
    }

    public static Group groupOfId(int id) {
        return new Group()
            .setId(id)
            .setName("GARAJONAY" + id)
            .setEmail("correo@correo.com" + id)
            .setOrder(id);
    }
}
