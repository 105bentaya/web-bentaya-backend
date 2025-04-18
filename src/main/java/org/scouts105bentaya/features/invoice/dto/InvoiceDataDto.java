package org.scouts105bentaya.features.invoice.dto;

import org.scouts105bentaya.features.invoice.entity.InvoiceExpenseType;
import org.scouts105bentaya.features.invoice.entity.InvoiceGrant;
import org.scouts105bentaya.features.invoice.entity.InvoicePayer;

import java.util.List;

public record InvoiceDataDto(
    List<InvoiceExpenseType> expenseTypes,
    List<InvoiceGrant> grants,
    List<InvoicePayer> payers,
    List<IssuerNifDto> autocompleteOptions
) {
}