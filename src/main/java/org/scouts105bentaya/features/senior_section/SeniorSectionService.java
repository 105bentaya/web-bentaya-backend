package org.scouts105bentaya.features.senior_section;

import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Service
public class SeniorSectionService {

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
            this.emailService.sendSimpleEmailWithHtml("Sección Sénior - Nuevo Formulario", html, seniorForm.getEmail());
        } catch (Exception e) {
            log.error("Error sending email form: {}", e.getMessage());
        }
    }

    private Context getContextForEmail(SeniorFormDto formDto) {
        Context context = new Context();
        context.setVariable("name", formDto.name());
        context.setVariable("surname", formDto.surname());
        context.setVariable("email", formDto.email());
        context.setVariable("phone", formDto.phone());
        context.setVariable("observations", formDto.observations());
        context.setVariable("acceptMessageGroup", formDto.acceptMessageGroup());
        context.setVariable("acceptNewsletter", formDto.acceptNewsletter());
        return context;
    }

    public void delete(Integer id) {
        this.seniorFormRepository.deleteById(id);
    }
}
