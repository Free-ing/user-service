package Freeing.user_service.controller;

import Freeing.user_service.error.ForbiddenException;
import Freeing.user_service.repository.AnnouncementEntity;
import Freeing.user_service.security.JwtTokenProvider;
import Freeing.user_service.service.AnnouncementService;
import Freeing.user_service.vo.RequestCreateAnnouncement;
import Freeing.user_service.vo.RequestUpdateAnnouncement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service")
@Slf4j
@RequiredArgsConstructor
public class AnnouncementController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AnnouncementService announcementService;

    @PostMapping("/announcement/add")
    public ResponseEntity<String> createAnnouncement(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                     @RequestBody @Valid
                                                     RequestCreateAnnouncement requestcreateAnnouncement) {

        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        if(role != 0){
            log.warn("공지사항 작성 권한이 없는 사용자가 접근했습니다. 사용자: "+userId);

            throw new ForbiddenException("공지사항 작성 권한이 없습니다.");
        }

        String response = announcementService.saveAnnouncement(requestcreateAnnouncement);

        log.info("공지사항 생성: "+ requestcreateAnnouncement.getTitle());



        return ResponseEntity.status(HttpStatus.CREATED).body(response+": 생성 완료");


    }

    @GetMapping("/announcement/list")
    public ResponseEntity<List<AnnouncementEntity>> getAnnouncementList(){

        List<AnnouncementEntity> returnList = announcementService.getAnnouncementList();
        if (returnList.isEmpty()){
            log.info("공지사항 목록이 비었습니다.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(returnList);
        }

        log.info("공지사항 리스트 조회 성공");
        return ResponseEntity.status(HttpStatus.OK).body(returnList);
    }

    @GetMapping("/announcement/{announcementId}")
    public ResponseEntity<AnnouncementEntity> getAnnouncement(@PathVariable Long announcementId){

        AnnouncementEntity announcementEntity = announcementService.getAnnouncement(announcementId);

        log.info("공지사항 상세 조회 완료: "+announcementId);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(announcementEntity);

    }

    @PutMapping("/announcement/update")
    public ResponseEntity<String> updateAnnouncement(
            @RequestBody @Valid RequestUpdateAnnouncement requestUpdateAnnouncement,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            ){

        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        if(role != 0){

            log.warn("공지사항 수정 권한이 없는 사용자가 접근했습니다. 사용자: "+userId);

            throw new ForbiddenException("공지사항 수정 권한이 없습니다.");
        }

        AnnouncementEntity announcementEntity = announcementService.updateAnnouncement(requestUpdateAnnouncement);

        log.info("공지사항 수정 완료: "+announcementEntity.getAnnouncementId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(announcementEntity.getTitle()+" 업데이트 성공");
    }

    @DeleteMapping("/announcement/delete/{announcementId}")
    public ResponseEntity<String> deleteAnnouncement(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable Long announcementId
    ){
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        if(role != 0){
            log.warn("공지사항 삭제 권한이 없는 사용자가 접근했습니다. 사용자: "+userId);

            throw new ForbiddenException("공지사항 삭제 권한이 없습니다.");
        }

        announcementService.deleteAnnouncement(announcementId);

        log.info("공지사항 삭제 완료: "+announcementId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(announcementId+": 성공적으로 삭제 되었습니다.");



    }
}
