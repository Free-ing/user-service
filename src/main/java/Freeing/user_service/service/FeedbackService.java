package Freeing.user_service.service;

import Freeing.user_service.dto.AnswerDto;
import Freeing.user_service.dto.FeedbackStatus;
import Freeing.user_service.dto.InquiriesDto;
import Freeing.user_service.repository.AnswerEntity;
import Freeing.user_service.repository.InquiriesEntity;

import java.util.List;

public interface FeedbackService {
    public InquiriesEntity addInquiries(InquiriesDto inquiriesDto);
    public AnswerEntity addAnswer(AnswerDto answerDto);
    public List<InquiriesEntity> getInquiriesList();
    public List<InquiriesEntity> getInquiriesList_user(Long userId);
    public InquiriesEntity getInquiry(Long inquiriesId);
    public AnswerEntity updateAnswer(String content, Long answerId);

    public InquiriesEntity updateStatus(Long inquiriesId, FeedbackStatus feedbackStatus);
}
