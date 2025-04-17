package org.scouts105bentaya.features.invoice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private LocalDate invoiceDate;
    @NotNull
    private String issuer;
    @NotNull
    private String invoiceNumber;
    @NotNull
    private String nif;
    @NotNull
    private Integer amount;
    private boolean receipt;
    private boolean complies;
    @NotNull
    private LocalDate paymentDate;
    @NotNull
    private String method;
    private boolean liquidated;
    private String observations;
    @ManyToOne
    @NotNull
    private InvoiceExpenseType expenseType;
    @ManyToOne
    private InvoiceGrant grant;
    @ManyToOne
    @NotNull
    private InvoicePayer payer;
    @OneToMany(mappedBy = "invoice")
    private List<InvoiceFile> files;
}
