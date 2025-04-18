package org.scouts105bentaya.features.invoice.repository;

import org.scouts105bentaya.features.invoice.dto.IssuerNifDto;
import org.scouts105bentaya.features.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {

    @Query(value = """
        SELECT issuer, nif
           FROM (SELECT i.issuer,
                        i.nif,
                        ROW_NUMBER() OVER (PARTITION BY i.nif ORDER BY i.id DESC) AS rn
                 FROM invoice i
                 WHERE i.nif <> '?') latest_invoices
           WHERE rn = 1;
        """, nativeQuery = true)
    List<IssuerNifDto> findAllIssuerNif();
}
