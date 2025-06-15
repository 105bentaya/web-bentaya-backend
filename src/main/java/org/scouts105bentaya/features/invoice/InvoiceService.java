package org.scouts105bentaya.features.invoice;

import jakarta.transaction.Transactional;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.invoice.dto.InvoiceDataDto;
import org.scouts105bentaya.features.invoice.dto.InvoiceTypesDto;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.scouts105bentaya.features.invoice.entity.InvoiceFile;
import org.scouts105bentaya.features.invoice.entity.InvoicePayer;
import org.scouts105bentaya.features.invoice.repository.InvoiceExpenseTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceFileRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceGrantRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceIncomeTypeRepository;
import org.scouts105bentaya.features.invoice.repository.InvoicePayerRepository;
import org.scouts105bentaya.features.invoice.repository.InvoiceRepository;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecification;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecificationFilter;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileTypeEnum;
import org.scouts105bentaya.shared.util.FileUtils;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceExpenseTypeRepository invoiceExpenseTypeRepository;
    private final InvoiceGrantRepository invoiceGrantRepository;
    private final InvoicePayerRepository invoicePayerRepository;
    private final BlobService blobService;
    private final InvoiceFileRepository invoiceFileRepository;
    private final InvoiceIncomeTypeRepository invoiceIncomeTypeRepository;

    public InvoiceService(
        InvoiceRepository invoiceRepository,
        InvoiceExpenseTypeRepository invoiceExpenseTypeRepository,
        InvoiceGrantRepository invoiceGrantRepository,
        InvoicePayerRepository invoicePayerRepository,
        BlobService blobService,
        InvoiceFileRepository invoiceFileRepository,
        InvoiceIncomeTypeRepository invoiceIncomeTypeRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceExpenseTypeRepository = invoiceExpenseTypeRepository;
        this.invoiceGrantRepository = invoiceGrantRepository;
        this.invoicePayerRepository = invoicePayerRepository;
        this.blobService = blobService;
        this.invoiceFileRepository = invoiceFileRepository;
        this.invoiceIncomeTypeRepository = invoiceIncomeTypeRepository;
    }

    public Page<Invoice> findAll(InvoiceSpecificationFilter filter) {
        return invoiceRepository.findAll(new InvoiceSpecification(filter), filter.getPageable());
    }

    public Invoice findById(Integer id) {
        return invoiceRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public InvoiceDataDto getInvoiceData() {
        return new InvoiceDataDto(
            invoiceExpenseTypeRepository.findAll(),
            invoiceGrantRepository.findAll(),
            invoicePayerRepository.findAll().stream().sorted(this::invoiceComparator).collect(Collectors.toList()),
            invoiceRepository.findAllIssuerNif()
        );
    }

    public InvoiceTypesDto getInvoicesTypes() {
        return new InvoiceTypesDto(
            invoiceExpenseTypeRepository.findAll(),
            invoiceIncomeTypeRepository.findAll()
        );
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
        Invoice invoice = this.findById(id);
        invoice.getFiles().forEach(this::deleteFile);
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public void saveDocument(Integer invoiceId, MultipartFile file) {
        FileUtils.validateFileType(file, FileTypeEnum.IMG_TYPE, FileTypeEnum.PDF_TYPE);

        Invoice invoice = findById(invoiceId);

        InvoiceFile invoiceFile = new InvoiceFile();
        invoiceFile.setInvoice(invoice);
        invoiceFile.setName(file.getOriginalFilename());
        invoiceFile.setMimeType(file.getContentType());
        invoiceFile.setUuid(blobService.createBlob(file));

        invoiceFileRepository.save(invoiceFile);
    }

    public void deleteFile(Integer fileId) {
        this.deleteFile(getInvoiceFile(fileId));
    }

    private void deleteFile(InvoiceFile invoiceFile) {
        blobService.deleteBlob(invoiceFile.getUuid());
        invoiceFileRepository.delete(invoiceFile);
    }

    public ResponseEntity<byte[]> getFile(Integer fileId) {
        InvoiceFile file = getInvoiceFile(fileId);
        return new FileTransferDto(blobService.getBlob(file.getUuid()), file.getName(), file.getMimeType()).asResponseEntity();
    }

    private InvoiceFile getInvoiceFile(Integer fileId) {
        return invoiceFileRepository.findById(fileId).orElseThrow(WebBentayaNotFoundException::new);
    }
}
