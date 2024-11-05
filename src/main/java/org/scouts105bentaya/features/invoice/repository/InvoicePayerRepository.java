package org.scouts105bentaya.features.invoice.repository;

import org.scouts105bentaya.features.invoice.entity.InvoicePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoicePayerRepository extends JpaRepository<InvoicePayer, Integer> {
}
