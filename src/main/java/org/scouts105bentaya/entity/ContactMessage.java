package org.scouts105bentaya.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Entity
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    @Length(max = 65535)
    @Column(columnDefinition = "TEXT")
    private String message;
}
