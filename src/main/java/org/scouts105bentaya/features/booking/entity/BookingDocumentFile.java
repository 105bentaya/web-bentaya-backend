package org.scouts105bentaya.features.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.user.User;

import java.util.List;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class BookingDocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mimeType;

    @OneToMany(mappedBy = "file")
    private List<BookingDocument> bookingDocuments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
}
