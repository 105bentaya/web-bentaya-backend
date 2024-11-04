package org.scouts105bentaya.features.complaint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/complaint")
public class ComplaintController {

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
