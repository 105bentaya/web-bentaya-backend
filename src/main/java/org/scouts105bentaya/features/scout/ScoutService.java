package org.scouts105bentaya.features.scout;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.dto.OldScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.dto.form.ContactFormDto;
import org.scouts105bentaya.features.scout.dto.form.IdDocumentFormDto;
import org.scouts105bentaya.features.scout.dto.form.InsuranceHolderForm;
import org.scouts105bentaya.features.scout.dto.form.MedicalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutInfoFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRecordFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRegistrationDateFormDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.InsuranceHolder;
import org.scouts105bentaya.features.scout.entity.MedicalData;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutContact;
import org.scouts105bentaya.features.scout.entity.ScoutFile;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.PersonType;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.user.User;
import org.scouts105bentaya.features.user.UserService;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.scouts105bentaya.shared.util.dto.FileTransferDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ScoutService {

    private final ScoutRepository scoutRepository;
    private final AuthService authService;
    private final UserService userService;
    private final EventService eventService;
    private final ConfirmationService confirmationService;
    private final PreScoutService preScoutService;
    private final ScoutConverter scoutConverter;
    private final BlobService blobService;
    private final ScoutFileRepository scoutFileRepository;
    private final GroupRepository groupRepository;
    private final ScoutRecordRepository scoutRecordRepository;

    public ScoutService(
        ScoutRepository scoutRepository,
        ScoutConverter scoutConverter,
        AuthService authService,
        UserService userService,
        @Lazy EventService eventService,
        ConfirmationService confirmationService,
        PreScoutService preScoutService,
        BlobService blobService,
        ScoutFileRepository scoutFileRepository,
        GroupRepository groupRepository,
        ScoutRecordRepository scoutRecordRepository) {
        this.scoutRepository = scoutRepository;
        this.scoutConverter = scoutConverter;
        this.authService = authService;
        this.userService = userService;
        this.eventService = eventService;
        this.confirmationService = confirmationService;
        this.preScoutService = preScoutService;
        this.blobService = blobService;
        this.scoutFileRepository = scoutFileRepository;
        this.groupRepository = groupRepository;
        this.scoutRecordRepository = scoutRecordRepository;
    }

    public List<Scout> findAll() {
        return scoutRepository.findAllByActiveIsTrue();
    }

    public List<Scout> adminFindAll() {
        return scoutRepository.findAll();
    }

    public List<Scout> findAllWithFalseImageAuthorization() {
        return scoutRepository.findAllByImageAuthorizationAndActiveIsTrue(false);
    }

    public List<Scout> findAllByLoggedScouterGroupId() {
        return scoutRepository.findAllByGroupAndActiveIsTrue(Objects.requireNonNull(authService.getLoggedUser().getGroup()));
    }

    public List<String> findScoutUsernames(Integer id) {
        Scout scout = this.findActiveById(id);
        return scout.getUserList().stream().map(User::getUsername).toList();
    }

    public Set<Scout> findCurrentByUser() {
        return authService.getLoggedUser().getScoutList();
    }

    public Scout findActiveById(Integer id) {
        return scoutRepository.findByIdAndActiveIsTrue(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public Scout findById(Integer id) {
        return scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public ScoutDto findMember(Integer id) {
        return ScoutDto.fromScout(findById(id));
    }

    public Scout save(OldScoutDto oldScoutDto) {
//        OldScout scoutToSave = scoutConverter.convertFromDto(oldScoutDto);
//        scoutToSave.setEnabled(true);
//        OldScout savedScout = scoutRepository.save(scoutToSave);
//        savedScout.getContactList().forEach(contact -> {
//            contact.setScout(savedScout);
//            contactRepository.save(contact);
//        });
//        this.createConfirmationForFutureEvents(savedScout);
//        return savedScout;
        return null;
    }

    public Scout saveFromPreScoutAndDelete(OldScoutDto oldScoutDto, Integer preScoutId) {
        Scout scout = save(oldScoutDto);
        this.preScoutService.saveAsAssigned(preScoutId);
        return scout;
    }

    public Scout update(OldScoutDto oldScoutDto) {
//        OldScout scoutToUpdate = scoutConverter.convertFromDto(oldScoutDto);
//        OldScout scoutDB = this.findActiveById(oldScoutDto.id());
//        boolean hasChangedGroup = !scoutDB.getGroup().getId().equals(scoutToUpdate.getGroup().getId());
//
//        scoutDB.setDni(scoutToUpdate.getDni());
//        scoutDB.setName(scoutToUpdate.getName());
//        scoutDB.setMedicalData(scoutToUpdate.getMedicalData());
//        scoutDB.setGroup(scoutToUpdate.getGroup());
//        scoutDB.setBirthday(scoutToUpdate.getBirthday());
//        scoutDB.setSurname(scoutToUpdate.getSurname());
//        scoutDB.setGender(scoutToUpdate.getGender());
//        scoutDB.setShirtSize(scoutToUpdate.getShirtSize());
//        scoutDB.setMunicipality(scoutToUpdate.getMunicipality());
//        scoutDB.setCensus(scoutToUpdate.getCensus());
//        scoutDB.setObservations(scoutToUpdate.getObservations());
//        scoutDB.setImageAuthorization(scoutToUpdate.isImageAuthorization());
//        scoutDB.setProgressions(scoutToUpdate.getProgressions());
//
//        this.deleteContacts(scoutDB.getContactList(), scoutToUpdate.getContactList());
//
//        scoutDB.setContactList(scoutToUpdate.getContactList());
//        scoutDB.getContactList().forEach(contact -> contact.setScout(scoutDB));
//
//        OldScout savedScout = scoutRepository.save(scoutDB);
//
//        if (hasChangedGroup) {
//            this.deleteFutureConfirmations(savedScout);
//            this.createConfirmationForFutureEvents(scoutDB);
//        }
//
//        return savedScout;
        return null;
    }

    public void updateScoutUsers(Integer scoutId, List<String> scoutUsers) {
        Scout scout = this.findActiveById(scoutId);
        scout.getUserList().stream()
            .filter(user -> scoutUsers.stream()
                .noneMatch(username -> username.equalsIgnoreCase(user.getUsername())))
            .forEach(user -> userService.removeScoutFromUser(user, scout));


        scoutUsers.forEach(username -> {
            if (scout.getUserList().stream().noneMatch(user -> user.getUsername().equalsIgnoreCase(username))) {
                try {
                    User user = userService.findByUsername(username.toLowerCase());
                    log.info("updateScoutUsers - adding scout to user {}", username);
                    userService.addScoutToUser(user, scout);
                } catch (WebBentayaUserNotFoundException ex) {
                    log.info("updateScoutUsers - user {} does not exist, creating new user", username);
                    userService.addNewUserRoleUser(username.toLowerCase(), scout);
                }
            }
        });
    }

//    private void deleteContacts(List<Contact> currentContacts, List<Contact> newContacts) {
//        currentContacts.stream().filter(
//            contact -> newContacts.stream().noneMatch(
//                contact1 -> contact1.getId() != null && contact1.getId().equals(contact.getId()))
//        ).forEach(contactRepository::delete);
//    }

    private void deleteFutureConfirmations(Scout scout) { //todo this method should be in confirmation service
        confirmationService.deleteAll(scout.getConfirmationList().stream()
            .filter(confirmation -> !confirmation.getEvent().eventHasEnded())
            .toList()
        );
    }

    private void createConfirmationForFutureEvents(Scout scout) { //todo this method should be in confirmation service
        eventService.findAllByGroup(scout.getGroup()).stream()
            .filter(event -> !event.isForScouters() && event.isActiveAttendanceList() && !event.eventHasEnded())
            .forEach(e -> {
                Confirmation confirmation = new Confirmation();
                confirmation.setScout(scout);
                confirmation.setEvent(e);
                if (e.isActiveAttendancePayment()) confirmation.setPayed(false);
                confirmationService.save(confirmation);
            });
    }

    public ScoutFormUserUpdateDto getScoutFormUpdateUserMessage(Integer scoutId, List<String> newUsers) {
        ScoutFormUserUpdateDto result = new ScoutFormUserUpdateDto();
        if (scoutId != null) {
            Scout scout = this.findActiveById(scoutId);
            List<String> currentUsers = scout.getUserList().stream().map(User::getUsername).toList();
            result.setAddedUsers(newUsers.stream().filter(newUser -> !currentUsers.contains(newUser)).toList());
            result.setDeletedUsers(currentUsers.stream().filter(currentUser -> !newUsers.contains(currentUser)).toList());
        } else {
            result.setAddedUsers(newUsers);
        }
        result.setAddedNewUsers(result.getAddedUsers().stream().filter(this::userDoesNotExist).toList());
        return result;
    }

    private boolean userDoesNotExist(String username) {
        try {
            userService.findByUsername(username);
        } catch (WebBentayaUserNotFoundException e) {
            return true;
        }
        return false;
    }

    @Transactional
    public void disable(Integer id) {
        Scout scout = this.findActiveById(id);
        scout.getUserList().forEach(user -> userService.removeScoutFromUser(user, scout));
        this.deleteFutureConfirmations(scout);
        scout.setActive(false);
        scoutRepository.save(scout);
    }

    @Transactional
    public void delete(Integer id) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        scout.getUserList().forEach(user -> userService.removeScoutFromUser(user, scout));
        scoutRepository.deleteById(id);
    }

    //NEW

    public Scout updateMemberPersonalData(Integer id, PersonalDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        PersonalData data = scout.getPersonalData();
        data.setSurname(form.surname());
        data.setName(form.name());
        data.setFeltName(form.feltName());
        data.setBirthday(form.birthday());
        data.setBirthplace(form.birthplace());
        data.setBirthProvince(form.birthProvince());
        data.setNationality(form.nationality());
        data.setAddress(form.address());
        data.setCity(form.city());
        data.setProvince(form.province());
        data.setPhone(form.phone());
        data.setLandline(form.landline());
        data.setEmail(form.email());
        data.setShirtSize(form.shirtSize());
        data.setResidenceMunicipality(form.residenceMunicipality());
        data.setGender(form.gender());

        data.setIdDocument(updateIdDocument(data.getIdDocument(), form.idDocument()));
        data.setObservations(form.observations());

        return scoutRepository.save(scout);
    }

    @Synchronized
    public ScoutFile uploadPersonalDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = createScoutFile(file);
        scout.getPersonalData().getDocuments().add(scoutFile);

        scoutRepository.save(scout);
        return scoutFile;
    }

    public void deletePersonalDataFile(Integer memberId, Integer fileId) {
        Scout scout = scoutRepository.findById(memberId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> scoutFiles = scout.getPersonalData().getDocuments();

        ScoutFile scoutFile = scoutFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        scoutFiles.remove(scoutFile);
        scoutRepository.save(scout);

        scoutFileRepository.deleteById(fileId);
    }

    public ResponseEntity<byte[]> downloadMemberFile(Integer id) {
        ScoutFile file = scoutFileRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        return new FileTransferDto(blobService.getBlob(file.getUuid()), file.getName(), file.getMimeType()).asResponseEntity();
    }

    @Synchronized
    public ScoutFile uploadMedicalDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = createScoutFile(file);

        scout.getMedicalData().getDocuments().add(scoutFile);

        scoutRepository.save(scout);
        return scoutFile;
    }

    public void deleteMedicalDataFile(Integer memberId, Integer fileId) {
        Scout scout = scoutRepository.findById(memberId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> medicalFiles = scout.getMedicalData().getDocuments();

        ScoutFile scoutFile = medicalFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        medicalFiles.remove(scoutFile);
        scoutRepository.save(scout);

        scoutFileRepository.deleteById(fileId);
    }

    public Scout updateScoutContactData(Integer id, @Valid List<ContactFormDto> contactList) {
        if (contactList == null || contactList.isEmpty() || contactList.size() > 3) {
            throw new WebBentayaBadRequestException("La lista de contactos debe contener entre 1 y 3 contactos");
        }

        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        List<ScoutContact> newContacts = new ArrayList<>();
        contactList.forEach(contactFormDto -> {
            if (contactFormDto.id() != null) {
                ScoutContact existingContact = scout.getContactList().stream()
                    .filter(contact -> contact.getId().equals(contactFormDto.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                this.updateExistingContact(existingContact, contactFormDto);
            } else {
                newContacts.add(this.newContact(contactFormDto, scout));
            }
        });

        scout.getContactList().removeIf(contact -> contactList.stream().noneMatch(form -> contact.getId().equals(form.id())));
        scout.getContactList().addAll(newContacts);

        return scoutRepository.save(scout);
    }

    private ScoutContact newContact(ContactFormDto contactFormDto, Scout scout) {
        ScoutContact newContact = new ScoutContact();
        updateContact(contactFormDto, newContact);
        newContact.setScout(scout);
        return newContact;
    }

    private void updateExistingContact(ScoutContact existingContact, ContactFormDto contactFormDto) {
        updateContact(contactFormDto, existingContact);
    }

    private void updateContact(ContactFormDto contactFormDto, ScoutContact contact) {
        contact.setPersonType(contactFormDto.personType());
        if (contact.getPersonType() == PersonType.REAL) {
            contact.setCompanyName(null);
            contact.setStudies(contactFormDto.studies());
            contact.setProfession(contactFormDto.profession());
            contact.setRelationship(contactFormDto.relationship());
        } else if (contact.getPersonType() == PersonType.JURIDICAL) {
            contact.setCompanyName(contactFormDto.companyName());
            contact.setStudies(null);
            contact.setProfession(null);
            contact.setRelationship(null);
        }

        contact.setName(contactFormDto.name());
        contact.setSurname(contactFormDto.surname());
        contact.setEmail(contactFormDto.email());
        contact.setPhone(contactFormDto.phone());
        contact.setDonor(contactFormDto.donor());
        contact.setObservations(contactFormDto.observations());
        contact.setIdDocument(updateIdDocument(contact.getIdDocument(), contactFormDto.idDocument()));
    }

    private IdentificationDocument updateIdDocument(IdentificationDocument identificationDocument, IdDocumentFormDto idForm) {
        if (idForm == null) {
            return null;
        }
        if (identificationDocument != null) {
            identificationDocument.setNumber(idForm.number());
            identificationDocument.setIdType(idForm.idType());
            return identificationDocument;
        } else {
            return new IdentificationDocument()
                .setNumber(idForm.number())
                .setIdType(idForm.idType());
        }
    }

    public Scout updateMedicalData(Integer id, MedicalDataFormDto form) {
        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        MedicalData medicalData = scout.getMedicalData();
        medicalData.setFoodIntolerances(form.foodIntolerances());
        medicalData.setFoodAllergies(form.foodAllergies());
        medicalData.setFoodProblems(form.foodProblems());
        medicalData.setFoodMedication(form.foodMedication());
        medicalData.setFoodDiet(form.foodDiet());
        medicalData.setMedicalIntolerances(form.medicalIntolerances());
        medicalData.setMedicalAllergies(form.medicalAllergies());
        medicalData.setMedicalDiagnoses(form.medicalDiagnoses());
        medicalData.setMedicalPrecautions(form.medicalPrecautions());
        medicalData.setMedicalMedications(form.medicalMedications());
        medicalData.setMedicalEmergencies(form.medicalEmergencies());
        medicalData.setAddictions(form.addictions());
        medicalData.setTendencies(form.tendencies());
        medicalData.setRecords(form.records());
        medicalData.setBullyingProtocol(form.bullyingProtocol());
        medicalData.setBloodType(form.bloodType());

        if (form.socialSecurityNumber() != null) {
            medicalData.setSocialSecurityNumber(form.socialSecurityNumber());
            medicalData.setSocialSecurityHolder(form.socialSecurityHolder() != null ?
                updateInsuranceHolder(medicalData.getSocialSecurityHolder(), form.socialSecurityHolder(), scout) :
                null
            );
        } else {
            medicalData.setSocialSecurityNumber(null);
            medicalData.setSocialSecurityHolder(null);
        }

        if (form.privateInsuranceNumber() != null) {
            medicalData.setPrivateInsuranceNumber(form.privateInsuranceNumber());
            medicalData.setPrivateInsuranceEntity(form.privateInsuranceEntity());
            medicalData.setPrivateInsuranceHolder(form.privateInsuranceHolder() != null ?
                updateInsuranceHolder(medicalData.getPrivateInsuranceHolder(), form.privateInsuranceHolder(), scout)
                : null
            );
        } else {
            medicalData.setPrivateInsuranceNumber(null);
            medicalData.setPrivateInsuranceEntity(null);
            medicalData.setPrivateInsuranceHolder(null);
        }

        return scoutRepository.save(scout);
    }

    private InsuranceHolder updateInsuranceHolder(InsuranceHolder existing, InsuranceHolderForm form, Scout scout) {
        return form != null ?
            updateInsuranceHolderData(Objects.requireNonNullElseGet(existing, InsuranceHolder::new), form, scout) :
            null;
    }

    private InsuranceHolder updateInsuranceHolderData(@NotNull InsuranceHolder insuranceHolder, InsuranceHolderForm form, Scout scout) {
        if (form.contactId() != null) {
            return insuranceHolder.setContact(
                    scout.getContactList().stream()
                        .filter(contact -> contact.getId().equals(form.contactId()))
                        .findFirst().orElseThrow(WebBentayaNotFoundException::new)
                )
                .setName(null)
                .setSurname(null)
                .setEmail(null)
                .setPhone(null)
                .setIdDocument(null);
        }
        return insuranceHolder.setContact(null)
            .setName(form.name())
            .setSurname(form.surname())
            .setEmail(form.email())
            .setPhone(form.phone())
            .setIdDocument(updateIdDocument(insuranceHolder.getIdDocument(), form.idDocument()));
    }

    public Scout updateScoutInfo(Integer id, ScoutInfoFormDto form) {
        this.validateScoutGroup(form);
        this.validateCensus(form, id);
        this.validateRegistrationDates(form);

        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        scout.setCensus(form.census());
        scout.setScoutType(form.scoutType());

        if (scout.getScoutType() == ScoutType.INACTIVE) {
            scout.setActive(false);
            scout.setFederated(false);
        } else {
            scout.setActive(true);
            scout.setFederated(form.federated());
        }

        scout.setGroup(scout.getScoutType().hasGroup() && form.groupId() != 0 ?
            groupRepository.findById(form.groupId()).orElseThrow(WebBentayaNotFoundException::new) :
            null
        );

        this.updateScoutRegistrationDates(scout, form);

        return scoutRepository.save(scout);
    }

    private void updateScoutRegistrationDates(Scout scout, ScoutInfoFormDto form) {
        List<ScoutRegistrationDates> registrationDates = scout.getRegistrationDates();
        List<ScoutRegistrationDates> newDates = new ArrayList<>();

        form.registrationDates().forEach(newDate -> {
            if (newDate.id() != null) {
                ScoutRegistrationDates existingDate = registrationDates.stream()
                    .filter(date -> date.getId().equals(newDate.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                existingDate.setRegistrationDate(newDate.registrationDate());
                existingDate.setUnregistrationDate(newDate.unregistrationDate());
            } else {
                newDates.add(new ScoutRegistrationDates()
                    .setRegistrationDate(newDate.registrationDate())
                    .setUnregistrationDate(newDate.unregistrationDate())
                    .setScout(scout)
                );
            }
        });

        registrationDates.removeIf(date -> form.registrationDates().stream()
            .noneMatch(newDate -> date.getId().equals(newDate.id()))
        );
        registrationDates.addAll(newDates);
    }

    private void validateCensus(ScoutInfoFormDto form, Integer scoutId) {
        Optional<Scout> existingCensus = scoutRepository.findFirstByCensus(form.census());
        if (existingCensus.isPresent() && !existingCensus.get().getId().equals(scoutId)) {
            throw new WebBentayaConflictException("Este censo ya está asignado");
        }
    }

    private void validateScoutGroup(ScoutInfoFormDto form) {
        if (form.scoutType() == ScoutType.SCOUT && (form.groupId() == null || form.groupId() == 0)) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educanda");
        }
        if (form.scoutType() == ScoutType.SCOUTER && form.groupId() == null) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educadora");
        }
    }

    private void validateRegistrationDates(ScoutInfoFormDto form) {
        List<ScoutRegistrationDateFormDto> dates = form.registrationDates();

        if (dates.stream()
            .filter(date -> date.unregistrationDate() != null)
            .anyMatch(date -> !date.registrationDate().isBefore(date.unregistrationDate()))
        ) {
            throw new WebBentayaBadRequestException("Una fecha de baja debe ser posterior a la fecha de alta correspondiente");
        }

        if (dates.stream().filter(date -> date.unregistrationDate() == null).count() > 1) {
            throw new WebBentayaBadRequestException("Hay dos o más fechas de baja sin especificar");
        }

        List<Interval> intervals = dates.stream()
            .map(date -> IntervalUtils.intervalFromLocalDates(
                date.registrationDate(), Optional.ofNullable(date.unregistrationDate()).orElse(date.registrationDate())
            ))
            .toList();

        if (IntervalUtils.intervalsOverlapOrAbut(intervals)) {
            throw new WebBentayaBadRequestException("Hay fechas de alta y baja que se superponen");
        }
    }

    public ScoutRecord uploadScoutRecord(Integer scoutId, ScoutRecordFormDto recordForm) {
        Scout scout = this.findById(scoutId);

        ScoutRecord scoutRecord = new ScoutRecord()
            .setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations())
            .setScout(scout);

        scoutRecord = scoutRecordRepository.save(scoutRecord);

        scout.getRecordList().add(scoutRecord);
        scoutRepository.save(scout);

        return scoutRecord;
    }

    public ScoutRecord updateScoutRecord(Integer scoutId, Integer recordId, ScoutRecordFormDto recordForm) {
        Scout scout = this.findById(scoutId);

        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations());

        return scoutRecordRepository.save(scoutRecord);
    }

    @Synchronized
    public ScoutFile uploadRecordFile(Integer recordId, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        ScoutRecord scoutRecord = scoutRecordRepository.findById(recordId).orElseThrow(WebBentayaNotFoundException::new);

        ScoutFile scoutFile = createScoutFile(file);

        scoutFile = scoutFileRepository.save(scoutFile);
        scoutRecord.getFiles().add(scoutFile);
        scoutRecordRepository.save(scoutRecord);
        return scoutFile;
    }

    public void deleteRecordFile(Integer recordId, Integer fileId) {
        ScoutRecord scoutRecord = scoutRecordRepository.findById(recordId).orElseThrow(WebBentayaNotFoundException::new);
        List<ScoutFile> recordFiles = scoutRecord.getFiles();

        ScoutFile scoutFile = recordFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(scoutFile.getUuid());

        recordFiles.remove(scoutFile);
        scoutRecordRepository.save(scoutRecord);

        scoutFileRepository.deleteById(fileId);
    }

    private ScoutFile createScoutFile(MultipartFile file) {
        ScoutFile scoutFile = new ScoutFile();
        scoutFile.setName(file.getOriginalFilename());
        scoutFile.setMimeType(file.getContentType());
        scoutFile.setUuid(blobService.createBlob(file));
        scoutFile.setUploadDate(ZonedDateTime.now());
        return scoutFile;
    }

    public void deleteScoutRecord(Integer scoutId, Integer recordId) {
        Scout scout = this.findById(scoutId);
        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.getFiles().forEach(file -> blobService.deleteBlob(file.getUuid()));
        scoutRecordRepository.deleteById(recordId);
    }
}
