package org.scouts105bentaya.service;

import org.scouts105bentaya.converter.SeniorFormConverter;
import org.scouts105bentaya.dto.SeniorFormDto;
import org.scouts105bentaya.entity.SeniorForm;
import org.scouts105bentaya.repository.SeniorFormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class SeniorSectionService {

    private static final Logger log = LoggerFactory.getLogger(SeniorSectionService.class);
    private final SeniorFormConverter seniorFormConverter;
    private final TemplateEngine template;
    private final EmailService emailService;
    private final SeniorFormRepository seniorFormRepository;

    public SeniorSectionService(
        TemplateEngine template,
        EmailService emailService,
        SeniorFormConverter seniorFormConverter,
        SeniorFormRepository seniorFormRepository
    ) {
        this.template = template;
        this.emailService = emailService;
        this.seniorFormConverter = seniorFormConverter;
        this.seniorFormRepository = seniorFormRepository;
    }

    public List<SeniorForm> getAll() {
        return seniorFormRepository.findAll();
    }

    public void saveSeniorForm(SeniorFormDto formDto) {
        SeniorForm seniorForm = seniorFormConverter.convertFromDto(formDto);
        seniorFormRepository.save(seniorForm);

        try {
            Context context = getContextForEmail(formDto);
            String html = this.template.process("new-senior-form", context);
            this.emailService.sendSimpleEmailWithHtml(seniorForm.getEmail(), "Sección Sénior - Nuevo Formulario", html);
        } catch (Exception e) {
            log.error("Error sending email form: {}", e.getMessage());
        }
    }

    private Context getContextForEmail(SeniorFormDto formDto) {
        Context context = new Context();
        context.setVariable("name", formDto.getName());
        context.setVariable("surname", formDto.getSurname());
        context.setVariable("email", formDto.getEmail());
        context.setVariable("phone", formDto.getPhone());
        context.setVariable("observations", formDto.getObservations());
        context.setVariable("acceptMessageGroup", formDto.getAcceptMessageGroup());
        context.setVariable("acceptNewsletter", formDto.getAcceptNewsletter());
        return context;
    }

    public void delete(Integer id) {
        this.seniorFormRepository.deleteById(id);
    }
}
