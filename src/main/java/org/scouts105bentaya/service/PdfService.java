package org.scouts105bentaya.service;

import jakarta.mail.util.ByteArrayDataSource;
import org.scouts105bentaya.entity.Complaint;
import org.scouts105bentaya.entity.PreScout;
import org.scouts105bentaya.entity.PreScouter;


public interface PdfService {
    ByteArrayDataSource generatePreScoutPDF(PreScout preScout);
    ByteArrayDataSource generatePreScouterPDF(PreScouter preScouter);
    ByteArrayDataSource generateComplaintPDF(Complaint complaint);
}
