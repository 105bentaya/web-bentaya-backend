package org.scouts105bentaya.features.invoice;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.invoice.dto.InvoiceDataDto;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.scouts105bentaya.features.invoice.specification.InvoiceSpecificationFilter;
import org.scouts105bentaya.shared.GenericConverter;
import org.scouts105bentaya.shared.specification.PageDto;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("api/invoice")
@PreAuthorize("hasAnyRole('SCOUTER', 'GROUP_SCOUTER')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public PageDto<Invoice> getAllInvoices(InvoiceSpecificationFilter filter) {
        log.info("getAllInvoices - filter:{}{}", filter, SecurityUtils.getLoggedUserUsernameForLog());
        return GenericConverter.convertListToPageDto(invoiceService.findAll(filter));
    }

    @GetMapping("/{id}")
    public Invoice getInvoice(@PathVariable Integer id) {
        log.info("getInvoice - id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.findById(id);
    }

    @GetMapping("/data")
    public InvoiceDataDto getInvoiceData() {
        log.info("getInvoiceData{}", SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.getInvoiceData();
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Integer fileId) {
        log.info("getFile - fileId:{}{}", fileId, SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.getFile(fileId);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody @Valid Invoice invoice) {
        log.info("createInvoice{}", SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.save(invoice);
    }

    @PostMapping(value = "/file/{invoiceId}", consumes = "multipart/form-data")
    public void uploadFile(
        @PathVariable Integer invoiceId,
        @RequestParam("file") MultipartFile file
    ) {
        log.info("uploadFile - invoiceId:{}{}", invoiceId, SecurityUtils.getLoggedUserUsernameForLog());
        invoiceService.saveDocument(invoiceId, file);
    }

    @PutMapping
    public Invoice updateInvoice(@RequestBody @Valid Invoice invoice) {
        log.info("updateInvoice - invoiceId:{}{}", invoice.getId(), SecurityUtils.getLoggedUserUsernameForLog());
        return invoiceService.update(invoice);
    }

    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable Integer id) {
        log.info("deleteInvoice - id:{}{}", id, SecurityUtils.getLoggedUserUsernameForLog());
        invoiceService.delete(id);
    }

    @DeleteMapping("/file/{fileId}")
    public void deleteFile(@PathVariable Integer fileId) {
        log.info("deleteFile - fileId:{}{}", fileId, SecurityUtils.getLoggedUserUsernameForLog());
        invoiceService.deleteFile(fileId);
    }
}
