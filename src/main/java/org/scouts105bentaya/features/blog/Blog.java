package org.scouts105bentaya.features.blog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String title;
    private String description;
    private String image;
    @Column(columnDefinition = "TEXT")
    private String data;
    private ZonedDateTime modificationDate;
    private ZonedDateTime endDate;
    private boolean event;
    private boolean published;
}
