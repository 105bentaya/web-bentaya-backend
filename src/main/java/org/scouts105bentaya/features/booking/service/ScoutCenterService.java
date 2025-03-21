package org.scouts105bentaya.features.booking.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.dto.ScoutCenterDto;
import org.scouts105bentaya.features.booking.entity.ScoutCenter;
import org.scouts105bentaya.features.booking.entity.ScoutCenterFile;
import org.scouts105bentaya.features.booking.repository.ScoutCenterFileRepository;
import org.scouts105bentaya.features.booking.repository.ScoutCenterRepository;
import org.scouts105bentaya.shared.service.BlobService;
import org.scouts105bentaya.shared.service.GeneralBlobService;
import org.scouts105bentaya.shared.service.PublicBlobService;
import org.scouts105bentaya.shared.util.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.scouts105bentaya.shared.util.FileUtils.validateFilesIsImg;

@Service
public class ScoutCenterService {

    private final ScoutCenterRepository scoutCenterRepository;
    private final BlobService blobService;
    private final PublicBlobService publicBlobService;
    private final ScoutCenterFileRepository scoutCenterFileRepository;

    public ScoutCenterService(ScoutCenterRepository scoutCenterRepository, BlobService blobService, PublicBlobService publicBlobService, ScoutCenterFileRepository scoutCenterFileRepository) {
        this.scoutCenterRepository = scoutCenterRepository;
        this.blobService = blobService;
        this.publicBlobService = publicBlobService;
        this.scoutCenterFileRepository = scoutCenterFileRepository;
    }

    public ResponseEntity<byte[]> getRulePDF(int centerId) {
        ScoutCenterFile ruleFile = Optional.ofNullable(scoutCenterRepository.get(centerId).getRulePdf())
            .orElseThrow(WebBentayaNotFoundException::new);
        return FileUtils.getFileResponseEntity(blobService.getBlob(ruleFile.getUuid()), ruleFile.getName(), MediaType.APPLICATION_PDF);
    }

    public ResponseEntity<byte[]> getIncidenceFile(int centerId) {
        ScoutCenterFile incidenceFile = Optional.ofNullable(scoutCenterRepository.get(centerId).getIncidencesDoc())
            .orElseThrow(WebBentayaNotFoundException::new);
        return FileUtils.getFileResponseEntity(blobService.getBlob(incidenceFile.getUuid()), incidenceFile.getName(), incidenceFile.getMimeType());
    }

    public ResponseEntity<byte[]> getAttendanceFile(int centerId) {
        ScoutCenterFile attendanceFile = Optional.ofNullable(scoutCenterRepository.get(centerId).getAttendanceDoc())
            .orElseThrow(WebBentayaNotFoundException::new);
        return FileUtils.getFileResponseEntity(blobService.getBlob(attendanceFile.getUuid()), attendanceFile.getName(), attendanceFile.getMimeType());
    }

    public ScoutCenterFile uploadRuleFile(int centerId, @NotNull MultipartFile file) {
        FileUtils.validateFileIsPdf(file);
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        ScoutCenterFile ruleFile = updateScoutCenterFile(file, scoutCenter.getRulePdf(), blobService);
        scoutCenter.setRulePdf(ruleFile);
        return scoutCenterRepository.save(scoutCenter).getRulePdf();
    }

    public ScoutCenterFile uploadIncidenceFile(int centerId, @NotNull MultipartFile file) {
        FileUtils.validateFileIsDoc(file);
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        ScoutCenterFile incidenceFile = updateScoutCenterFile(file, scoutCenter.getIncidencesDoc(), blobService);
        scoutCenter.setIncidencesDoc(incidenceFile);
        return scoutCenterRepository.save(scoutCenter).getIncidencesDoc();
    }

    public ScoutCenterFile uploadAttendanceFile(int centerId, @NotNull MultipartFile file) {
        FileUtils.validateFileIsDoc(file);
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        ScoutCenterFile attendance = updateScoutCenterFile(file, scoutCenter.getAttendanceDoc(), blobService);
        scoutCenter.setAttendanceDoc(attendance);
        return scoutCenterRepository.save(scoutCenter).getAttendanceDoc();
    }

    public ScoutCenterFile uploadMainPhotoFile(int centerId, @NotNull MultipartFile file) {
        FileUtils.validateFileIsImg(file);
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        ScoutCenterFile mainPhoto = updateScoutCenterFile(file, scoutCenter.getMainPhoto(), publicBlobService);
        scoutCenter.setMainPhoto(mainPhoto);
        return scoutCenterRepository.save(scoutCenter).getMainPhoto();
    }

    private ScoutCenterFile updateScoutCenterFile(@NotNull MultipartFile file, ScoutCenterFile exisingFile, GeneralBlobService blobService) {
        if (exisingFile == null) {
            String uuid = blobService.createBlob(file);
            exisingFile = new ScoutCenterFile();
            exisingFile.setUuid(uuid);
        } else {
            blobService.updateBlob(file, exisingFile.getUuid());
        }

        exisingFile.setName(file.getOriginalFilename());
        exisingFile.setMimeType(file.getContentType());
        return exisingFile;
    }

    public ResponseEntity<byte[]> getPhoto(String uuid) {
        ScoutCenterFile file = scoutCenterFileRepository.findByUuid(uuid).orElseThrow(WebBentayaNotFoundException::new);
        return FileUtils.getFileResponseEntity(
            publicBlobService.getBlob(uuid),
            file.getName(),
            file.getMimeType()
        );
    }

    public List<ScoutCenterFile> uploadPhotos(int centerId, @NotEmpty List<MultipartFile> files) {
        validateFilesIsImg(files);
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        List<ScoutCenterFile> photos = scoutCenter.getPhotos();

        photos.addAll(
            files.stream().map(file -> {
                ScoutCenterFile photoFile = new ScoutCenterFile();
                photoFile.setUuid(publicBlobService.createBlob(file));
                photoFile.setName(file.getOriginalFilename());
                photoFile.setMimeType(file.getContentType());
                return photoFile;
            }).toList()
        );

        scoutCenter = scoutCenterRepository.save(scoutCenter);

        return scoutCenter.getPhotos();
    }

    @Transactional
    public void deletePhoto(int centerId, int photoId) {
        ScoutCenter scoutCenter = scoutCenterRepository.get(centerId);
        ScoutCenterFile file = scoutCenterFileRepository.get(photoId);
        scoutCenter.getPhotos().remove(file);
        scoutCenterFileRepository.deleteById(photoId);
        publicBlobService.deleteBlob(file.getUuid());
    }

    public ScoutCenter updateScoutCenter(Integer id, ScoutCenterDto newScoutCenter) {
        ScoutCenter scoutCenter = scoutCenterRepository.get(id);
        scoutCenter.setName(newScoutCenter.name())
            .setPlace(newScoutCenter.place())
            .setPrice(newScoutCenter.price())
            .setMaxCapacity(newScoutCenter.maxCapacity())
            .setMinExclusiveCapacity(newScoutCenter.minExclusiveCapacity())
            .setInformation(newScoutCenter.information())
            .setFeatures(newScoutCenter.features());
        return scoutCenterRepository.save(scoutCenter);
    }
}
