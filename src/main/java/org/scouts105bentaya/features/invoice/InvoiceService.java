package org.scouts105bentaya.features.invoice;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.scouts105bentaya.features.invoice.entity.InvoicePayer;
import org.scouts105bentaya.features.invoice.repository.InvoiceExpenseTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceGrantRepository;
import org.scouts105bentaya.features.invoice.repository.InvoicePayerRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceRepository;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecification;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecificationFilter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceExpenseTypeRepository invoiceExpenseTypeRepository;
    private final InvoiceGrantRepository invoiceGrantRepository;
    private final InvoicePayerRepository invoicePayerRepository;

    public InvoiceService(
        InvoiceRepository invoiceRepository,
        InvoiceExpenseTypeRepository invoiceExpenseTypeRepository,
        InvoiceGrantRepository invoiceGrantRepository,
        InvoicePayerRepository invoicePayerRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceExpenseTypeRepository = invoiceExpenseTypeRepository;
        this.invoiceGrantRepository = invoiceGrantRepository;
        this.invoicePayerRepository = invoicePayerRepository;
    }

    public Page<Invoice> findAll(InvoiceSpecificationFilter filter) {
        return invoiceRepository.findAll(new InvoiceSpecification(filter), filter.getPageable());
    }

    public Invoice findById(Integer id) {
        return invoiceRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public InvoiceDataDto getInvoiceData() {
        return InvoiceDataDto.builder()
            .expenseTypes(invoiceExpenseTypeRepository.findAll())
            .grants(invoiceGrantRepository.findAll())
            .payers(invoicePayerRepository.findAll().stream().sorted(this::invoiceComparator).collect(Collectors.toList()))
            .build();
    }

    private int invoiceComparator(InvoicePayer a, InvoicePayer b) {
        if (a.getGroup() == null && b.getGroup() == null) return 0;
        if (b.getGroup() == null) return -1;
        if (a.getGroup() == null) return 1;
        return a.getGroup().getOrder() - b.getGroup().getOrder();
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice update(Invoice invoice) {
        this.findById(invoice.getId());
        return invoiceRepository.save(invoice);
    }

    public void delete(Integer id) {
        invoiceRepository.deleteById(id);
    }
}
