package org.scouts105bentaya.controller;

import org.scouts105bentaya.entity.Complaint;
import org.scouts105bentaya.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/complaint")
public class ComplaintController {

    private final Logger log = LoggerFactory.getLogger(ComplaintController.class);
    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/form")
    public void sendComplaintMail(@RequestBody Complaint complaint) {
        log.info("METHOD ComplaintController.sendComplaintEmail");
        this.complaintService.sendComplaintEmail(complaint);
    }

}
