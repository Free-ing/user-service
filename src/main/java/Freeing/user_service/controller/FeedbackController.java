package Freeing.user_service.controller;

import Freeing.user_service.dto.AnswerDto;
import Freeing.user_service.dto.InquiriesDto;
import Freeing.user_service.error.ForbiddenException;
import Freeing.user_service.repository.AnswerEntity;
import Freeing.user_service.repository.InquiriesEntity;
import Freeing.user_service.security.JwtTokenProvider;
import Freeing.user_service.service.FeedbackService;
import Freeing.user_service.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user-service")
public class FeedbackController {

    private final JwtTokenProvider jwtTokenProvider;
    private final FeedbackService feedbackService;
    @PostMapping("/inquiries")
    public ResponseEntity<ResponseInquiries> addInquiries(@Valid @RequestBody RequestInquiriesAdd requestInquiriesAdd,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                          String authorizationHeader){

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        InquiriesDto inquiriesDto = mapper.map(requestInquiriesAdd, InquiriesDto.class);
        inquiriesDto.setUserId(userId);
        inquiriesDto.setCategory(requestInquiriesAdd.getCategory());

        ResponseInquiries responseInquiries =
                mapper
                .map(
                        feedbackService.addInquiries(inquiriesDto),
                        ResponseInquiries.class
                );

        log.info("문의 사항 생성 완료: "+responseInquiries.getInquiriesId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseInquiries);



    }

    @PatchMapping("/inquiries/update/status")
    public ResponseEntity<String>updateStatus(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                  String authorizationHeader,
                                              @RequestBody @Valid RequestUpdateStatus_FeedBack requestUpdateStatus_feedBack){
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        if(role!=0){
            log.warn("문의사항 상태 변경 권한이 없는 사용자가 접근하였습니다. 사용자: "+userId);
            throw new ForbiddenException("문의사항 상태 변경 권한이 없습니다.");
        }

        InquiriesEntity inquiriesEntity = feedbackService.updateStatus(
                requestUpdateStatus_feedBack.getInquiriesId(), requestUpdateStatus_feedBack.getFeedbackStatus()
        );



        return ResponseEntity.status(HttpStatus.OK).body(
                inquiriesEntity.getInquiriesTitle()+"의 상태: "+inquiriesEntity.getFeedbackStatus()
        );
    }

    @PostMapping("/inquiries/answer")
    public ResponseEntity<ResponseAnswer> addAnswer(@Valid @RequestBody RequestAnswerAdd requestAnswerAdd,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                    String authorizationHeader){


        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        if(role!=0){
            throw new ForbiddenException("답변 작성 권한이 없습니다.");
        }


        // DTO 설정
        AnswerDto answerDto = new AnswerDto();
        answerDto.setInquiriesId(requestAnswerAdd.getInquiriesId());
        answerDto.setContent(requestAnswerAdd.getContent());

        // 답변 저장
        AnswerEntity answerEntity = feedbackService.addAnswer(answerDto);

        // 응답 객체 생성
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ResponseAnswer responseAnswer = mapper.map(answerEntity, ResponseAnswer.class);

        responseAnswer.setInquiriesId(answerEntity.getInquiries().getInquiriesId());
        log.info("답변 생성 완료");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseAnswer);
    }

    @GetMapping("/inquiries/list/admin")
    public ResponseEntity<List<InquiriesEntity>>getInquiriesList(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                     String authorizationHeader){
        String token = authorizationHeader.substring(7);

        int role = jwtTokenProvider.getRoleFromToken(token);
        if(role!=0){
            throw new ForbiddenException("전체 문의사항 조회 권한이 없습니다.");
        }

        List<InquiriesEntity> returnList = feedbackService.getInquiriesList();
        if(returnList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(returnList);

        }
        return ResponseEntity.status(HttpStatus.OK).body(returnList);
    }

    @GetMapping("/inquiries/list")
    public ResponseEntity<List<InquiriesEntity>>getInquiriesList_user(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                                                          String authorizationHeader){
        String token = authorizationHeader.substring(7);

        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        List<InquiriesEntity> returnList = feedbackService.getInquiriesList_user(userId);
        if(returnList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(returnList);
        }

        return ResponseEntity.status(HttpStatus.OK).body(returnList);
    }

    @GetMapping("/inquiries/{inquiriesId}")
    public ResponseEntity<InquiriesEntity>getInquiry(@PathVariable Long inquiriesId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        feedbackService
                                .getInquiry(inquiriesId)
                );

    }

    @PatchMapping("/inquiries/response/{answerId}")
    public ResponseEntity<String>updateResponse(@PathVariable Long answerId,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                String authorizationHeader,
                                                @Valid @RequestBody RequestAnswerUpdate requestAnswerUpdate
                                                ){
        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        if(role != 0){
            throw new ForbiddenException("답변 수정 권한이 없습니다.");

        }

        feedbackService.updateAnswer(requestAnswerUpdate.getContent(),answerId);

        return ResponseEntity.status(HttpStatus.OK).body(answerId+": 수정 완료");

    }

}
