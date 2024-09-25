package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.Complaint;

import java.util.List;

public interface ComplaintService {
    List<Complaint> findAll();
    Complaint save(Complaint complaint);
    void sendComplaintEmail(Complaint complaint);
}
