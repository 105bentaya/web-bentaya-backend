package org.scouts105bentaya.features.group;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Getter
@Setter
@Table(name = "bentaya_group")
@Accessors(chain = true)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    @Unique
    @Column(name = "group_order")
    private int order;
}
