package org.scouts105bentaya.features.scout.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.scouts105bentaya.features.invoice.entity.InvoiceExpenseType;
import org.scouts105bentaya.features.invoice.entity.InvoiceIncomeType;
import org.scouts105bentaya.features.scout.enums.EntryType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class EconomicEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne
    private InvoiceExpenseType expenseType;

    @ManyToOne
    private InvoiceIncomeType incomeType;

    private String account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EntryType type;

    @Column(length = 511)
    private String observations;

    @ManyToOne(optional = false)
    @JsonIgnore
    private EconomicData economicData;

    @OneToOne(mappedBy = "economicEntry", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private EconomicEntryDonor donor;
}
