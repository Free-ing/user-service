package Freeing.user_service.service;

import Freeing.user_service.error.NotFoundException;
import Freeing.user_service.repository.AnnouncementEntity;
import Freeing.user_service.repository.AnnouncementRepository;
import Freeing.user_service.vo.RequestCreateAnnouncement;
import Freeing.user_service.vo.RequestUpdateAnnouncement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService{

    private final AnnouncementRepository announcementRepository;
    @Override
    public String saveAnnouncement(RequestCreateAnnouncement requestcreateAnnouncement) {

        AnnouncementEntity announcementEntity =requestcreateAnnouncement.toEntity();
        return announcementRepository.save(announcementEntity).getTitle();
    }

    @Override
    public List<AnnouncementEntity> getAnnouncementList() {
        return announcementRepository.findAll();
    }

    @Override
    public AnnouncementEntity getAnnouncement(Long announcementId) {
        return announcementRepository.findById(announcementId)
                .orElseThrow(
                        ()-> new NotFoundException(
                                announcementId+": 해당 공지사항을 찾을 수 없습니다."
                        )
                );
    }

    @Override
    public AnnouncementEntity updateAnnouncement(RequestUpdateAnnouncement updateRequest) {
        AnnouncementEntity announcement = announcementRepository.findById(updateRequest.getAnnouncementId())
                .orElseThrow(()-> new NotFoundException(
                        updateRequest.getAnnouncementId()+": 해당 공지사항을 찾을 수 없습니다."
                ));

        announcement.setTitle(updateRequest.getTitle());
        announcement.setContent(updateRequest.getContent());
        announcement.setCategory(updateRequest.getCategory());

        return announcementRepository.save(announcement);
    }

    @Override
    public void deleteAnnouncement(Long announcementId) {
        AnnouncementEntity announcementEntity = announcementRepository.findById(announcementId)
                .orElseThrow(()-> new NotFoundException(announcementId+": 해당 공지사항을 찾을 수 없습니다."));
        announcementRepository.delete(announcementEntity);


    }
}
