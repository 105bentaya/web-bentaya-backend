package org.scouts105bentaya.features.poll;


import org.scouts105bentaya.features.poll.dto.PublicPollDto;
import org.scouts105bentaya.features.contact_message.ContactMessage;
import org.scouts105bentaya.core.exception.WebBentayaException;
import org.scouts105bentaya.features.poll.entity.Poll;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("api/poll")
public class PollController {

    private static final Logger log = LoggerFactory.getLogger(PollController.class);

    @Value("${bentaya.email.it}")
    private String email;

    private final PollService pollService;
    private final EmailService emailService;
    private final PublicPollConverter publicPollConverter;

    public PollController(
        PollService pollService,
        EmailService emailService,
        PublicPollConverter publicPollConverter
    ) {
        this.pollService = pollService;
        this.emailService = emailService;
        this.publicPollConverter = publicPollConverter;
    }

    @GetMapping("public/{id}")
    public PublicPollDto getPublicById(@PathVariable Integer id) {
        return publicPollConverter.convertFromEntity(pollService.findById(id));
    }

    @PostMapping("public/vote/{optionId}")
    public void vote(@PathVariable Integer optionId) {
        log.info("METHOD PollController.vote --- PARAMS optionId: {}", optionId);
        pollService.vote(optionId);
    }

    @DeleteMapping("public/vote/{id}")
    public void deleteVote(@PathVariable Integer id) {
        log.info("METHOD PollController.deleteVote --- PARAMS optionId: {}", id);
        pollService.deleteVote(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Poll> getAll() {
        return pollService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Poll getById(@PathVariable Integer id) {
        return pollService.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    public void update(@RequestBody Poll poll) {
        log.info("METHOD PollController.update --- PARAMS optionId: {}{}", poll.getId(), SecurityUtils.getLoggedUserUsernameForLog());
        pollService.update(poll);
    }

    @PostMapping("public/mail")
    public void sendMail(@RequestBody ContactMessage contactMessage) {
        log.info("METHOD PollController.sendMail");
        emailService.sendSimpleEmail(email, "Canción Disco Bentaya", String.format("""
            %s con correo %s nos envía la canción %s:
            %s
            """, contactMessage.getName(), contactMessage.getEmail(), contactMessage.getSubject(), contactMessage.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebBentayaException.class)
    public Map<String, String> handlePdfException(WebBentayaException e) {
        return Map.of("bentayaMessage", e.getMessage());
    }
}
