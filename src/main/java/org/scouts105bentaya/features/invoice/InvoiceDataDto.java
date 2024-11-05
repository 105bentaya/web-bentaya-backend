package org.scouts105bentaya.features.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.scouts105bentaya.features.invoice.entity.InvoiceExpenseType;
import org.scouts105bentaya.features.invoice.entity.InvoiceGrant;
import org.scouts105bentaya.features.invoice.entity.InvoicePayer;

import java.util.List;

@Getter
@Setter
@Builder
public class InvoiceDataDto {
    private List<InvoiceExpenseType> expenseTypes;
    private List<InvoiceGrant> grants;
    private List<InvoicePayer> payers;
}
