package org.scouts105bentaya.features.contact_message;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/contact-message")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    public ContactMessageController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping("/form")
    public void sendComplaintMail(@RequestBody @Valid ContactMessage contactMessage) {
        log.info("METHOD ContactMessageController.sendContactMessageEmail");
        this.contactMessageService.sendContactMessageEmail(contactMessage);
    }
}
