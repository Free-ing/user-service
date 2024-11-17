package Freeing.user_service.service;

import Freeing.user_service.repository.AnnouncementEntity;
import Freeing.user_service.vo.RequestCreateAnnouncement;
import Freeing.user_service.vo.RequestUpdateAnnouncement;

import java.util.List;

public interface AnnouncementService {
    String saveAnnouncement(RequestCreateAnnouncement requestcreateAnnouncement) ;

    List<AnnouncementEntity> getAnnouncementList();

    AnnouncementEntity getAnnouncement(Long announcementId);

    AnnouncementEntity updateAnnouncement(RequestUpdateAnnouncement updateRequest);

    void deleteAnnouncement(Long announcementId);
    }
