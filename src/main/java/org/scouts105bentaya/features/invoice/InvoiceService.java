package org.scouts105bentaya.features.invoice;

import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.scouts105bentaya.features.invoice.repository.InvoiceExpenseTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceGrantRepository;
import org.scouts105bentaya.features.invoice.repository.InvoicePayerRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceRepository;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecification;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecificationFilter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
            .payers(invoicePayerRepository.findAll())
            .build();
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
