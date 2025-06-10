package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ScoutHistory {
    @Id
    @JsonIgnore
    private Integer scoutId;

    @Column(columnDefinition = "text")
    private String observations;
    @Column(columnDefinition = "text")
    private String progressions;

    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "scout_id")
    @JsonIgnore
    private Scout scout;
}
