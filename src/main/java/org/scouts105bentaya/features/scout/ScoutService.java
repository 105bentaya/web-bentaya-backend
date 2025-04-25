package org.scouts105bentaya.features.scout;

import jakarta.transaction.Transactional;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.core.exception.WebBentayaUserNotFoundException;
import org.scouts105bentaya.features.confirmation.Confirmation;
import org.scouts105bentaya.features.confirmation.service.ConfirmationService;
import org.scouts105bentaya.features.event.service.EventService;
import org.scouts105bentaya.features.pre_scout.service.PreScoutService;
import org.scouts105bentaya.features.scout.converter.ScoutConverter;
import org.scouts105bentaya.features.scout.dto.MemberDto;
import org.scouts105bentaya.features.scout.dto.ScoutDto;
import org.scouts105bentaya.features.scout.dto.ScoutFormUserUpdateDto;
import org.scouts105bentaya.features.scout.dto.form.IdDocumentFormDto;
import org.scouts105bentaya.features.scout.dto.form.PersonalDataFormDto;
import org.scouts105bentaya.features.scout.entity.IdentificationDocument;
import org.scouts105bentaya.features.scout.entity.Member;
import org.scouts105bentaya.features.scout.entity.MemberFile;
import org.scouts105bentaya.features.scout.entity.PersonalData;
import org.scouts105bentaya.features.scout.entity.RealPersonalData;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.enums.MemberType;
import org.scouts105bentaya.features.scout_contact.Contact;
import org.scouts105bentaya.features.scout_contact.ContactRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class ScoutService {

    private final ScoutRepository scoutRepository;
    private final ContactRepository contactRepository;
    private final AuthService authService;
    private final UserService userService;
    private final EventService eventService;
    private final ConfirmationService confirmationService;
    private final PreScoutService preScoutService;
    private final ScoutConverter scoutConverter;
    private final MemberRepository memberRepository;
    private final BlobService blobService;
    private final MemberFileRepository memberFileRepository;

    public ScoutService(
        ScoutRepository scoutRepository,
        ContactRepository contactRepository,
        ScoutConverter scoutConverter,
        AuthService authService,
        UserService userService,
        @Lazy EventService eventService,
        ConfirmationService confirmationService,
        PreScoutService preScoutService,
        MemberRepository memberRepository,
        BlobService blobService,
        MemberFileRepository memberFileRepository
    ) {
        this.scoutRepository = scoutRepository;
        this.contactRepository = contactRepository;
        this.scoutConverter = scoutConverter;
        this.authService = authService;
        this.userService = userService;
        this.eventService = eventService;
        this.confirmationService = confirmationService;
        this.preScoutService = preScoutService;
        this.memberRepository = memberRepository;
        this.blobService = blobService;
        this.memberFileRepository = memberFileRepository;
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
        Scout scout = this.findById(id);
        return scout.getUserList().stream().map(User::getUsername).toList();
    }

    public Set<Scout> findCurrentByUser() {
        return authService.getLoggedUser().getScoutList();
    }

    public Scout findById(Integer id) {
        return scoutRepository.findByIdAndActiveIsTrue(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public MemberDto findMember(Integer id) {
        return MemberDto.fromScout(findById(id));
    }

    public Scout save(ScoutDto scoutDto) {
//        OldScout scoutToSave = scoutConverter.convertFromDto(scoutDto);
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

    public Scout saveFromPreScoutAndDelete(ScoutDto scoutDto, Integer preScoutId) {
        Scout scout = save(scoutDto);
        this.preScoutService.saveAsAssigned(preScoutId);
        return scout;
    }

    public Scout update(ScoutDto scoutDto) {
//        OldScout scoutToUpdate = scoutConverter.convertFromDto(scoutDto);
//        OldScout scoutDB = this.findById(scoutDto.id());
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
        Scout scout = this.findById(scoutId);
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

    private void deleteContacts(List<Contact> currentContacts, List<Contact> newContacts) {
        currentContacts.stream().filter(
            contact -> newContacts.stream().noneMatch(
                contact1 -> contact1.getId() != null && contact1.getId().equals(contact.getId()))
        ).forEach(contactRepository::delete);
    }

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
            Scout scout = this.findById(scoutId);
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
        Scout scout = this.findById(id);
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

    public Member updateMemberPersonalData(Integer id, PersonalDataFormDto form) {
        Member member = memberRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        if (member.getType() != form.type()) {
            throw new WebBentayaBadRequestException("No se puede cambiar el tipo de persona");
        }

        if (member.getType() == MemberType.REAL) {
            if (form.realData() == null) {
                throw new WebBentayaBadRequestException("No se han aportado los datos para actualizar a una persona física");
            }
            RealPersonalData data = (RealPersonalData) member.getPersonalData();
            data.setSurname(form.realData().surname());
            data.setName(form.realData().name());
            data.setFeltName(form.realData().feltName());
            data.setBirthday(form.realData().birthday());
            data.setBirthplace(form.realData().birthplace());
            data.setBirthProvince(form.realData().birthProvince());
            data.setNationality(form.realData().nationality());
            data.setAddress(form.realData().address());
            data.setCity(form.realData().city());
            data.setProvince(form.realData().province());
            data.setPhone(form.realData().phone());
            data.setLandline(form.realData().landline());
            data.setEmail(form.realData().email());
            data.setShirtSize(form.realData().shirtSize());
            data.setResidenceMunicipality(form.realData().residenceMunicipality());
            data.setGender(form.realData().gender());
        } else if (member.getType() == MemberType.JURIDICAL && form.juridicalData() == null) {
            //todo finish
        }

        PersonalData personalData = member.getPersonalData();
        IdDocumentFormDto idDocumentDto = form.idDocument();
        if (idDocumentDto == null) {
            personalData.setIdDocument(null);
        } else {
            personalData.setIdDocument(
                new IdentificationDocument().setIdType(idDocumentDto.idType()).setNumber(idDocumentDto.number())
            );
        }
        personalData.setObservations(form.observations());

        return memberRepository.save(member);
    }

    @Synchronized
    public MemberFile uploadPersonalDataFile(Integer id, MultipartFile file) {
        FileUtils.validateFileIsPdf(file); //todo check
        Member member = memberRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        MemberFile memberFile = new MemberFile();
        memberFile.setName(file.getOriginalFilename());
        memberFile.setMimeType(file.getContentType());
        memberFile.setUuid(blobService.createBlob(file));
        memberFile.setUploadDate(ZonedDateTime.now());

        member.getPersonalData().getDocuments().add(memberFile);

        memberRepository.save(member);
        return memberFile;
    }

    public void deletePersonalDataFile(Integer memberId, Integer fileId) {
        Member member = memberRepository.findById(memberId).orElseThrow(WebBentayaNotFoundException::new);
        List<MemberFile> memberFiles = member.getPersonalData().getDocuments();

        MemberFile memberFile = memberFiles.stream()
            .filter(document -> document.getId().equals(fileId))
            .findFirst().orElseThrow(WebBentayaNotFoundException::new);

        blobService.deleteBlob(memberFile.getUuid());

        memberFiles.remove(memberFile);
        memberRepository.save(member);

        memberFileRepository.deleteById(fileId);
    }

    public ResponseEntity<byte[]> downloadMemberFile(Integer id) {
        MemberFile file = memberFileRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
        return new FileTransferDto(blobService.getBlob(file.getUuid()), file.getName(), file.getMimeType()).asResponseEntity();
    }
}
