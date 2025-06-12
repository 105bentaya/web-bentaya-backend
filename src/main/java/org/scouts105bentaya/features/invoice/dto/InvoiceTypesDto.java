package org.scouts105bentaya.features.invoice.dto;

import org.scouts105bentaya.features.invoice.entity.InvoiceExpenseType;
import org.scouts105bentaya.features.invoice.entity.InvoiceIncomeType;

import java.util.List;

public record InvoiceTypesDto(
    List<InvoiceExpenseType> expenseTypes,
    List<InvoiceIncomeType> incomeTypes
) {
}