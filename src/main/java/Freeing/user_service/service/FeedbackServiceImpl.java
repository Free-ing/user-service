package Freeing.user_service.service;

import Freeing.user_service.dto.AnswerDto;
import Freeing.user_service.dto.FeedbackStatus;
import Freeing.user_service.dto.InquiriesDto;
import Freeing.user_service.error.DuplicateAnswerException;
import Freeing.user_service.error.NotFoundException;
import Freeing.user_service.repository.AnswerEntity;
import Freeing.user_service.repository.AnswerRepository;
import Freeing.user_service.repository.FeedbackRepository;
import Freeing.user_service.repository.InquiriesEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackServiceImpl implements FeedbackService{
    private  final FeedbackRepository feedbackRepository;
    private final AnswerRepository answerRepository;
    @Override
    public InquiriesEntity addInquiries(InquiriesDto inquiriesDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        InquiriesEntity inquiriesEntity = mapper.map(inquiriesDto, InquiriesEntity.class);
        inquiriesEntity.setCreatedAt(LocalDateTime.now());
        inquiriesEntity.setFeedbackStatus(FeedbackStatus.NEW);

        return feedbackRepository.save(inquiriesEntity);
    }

    @Override
    public AnswerEntity addAnswer(AnswerDto answerDto) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // 문의 사항 조회
        InquiriesEntity inquiriesEntity = feedbackRepository.findById(answerDto.getInquiriesId())
                .orElseThrow(() -> new NotFoundException("해당 문의 사항을 찾을 수 없습니다."));

        if(inquiriesEntity.getAnswer()!=null){
            throw new DuplicateAnswerException("이미 답변이 존재합니다.");

        }
        // AnswerEntity 설정
        AnswerEntity answerEntity = mapper.map(answerDto, AnswerEntity.class);
        answerEntity.setInquiries(inquiriesEntity);  // 연관 관계 설정
        answerEntity.setCreatedAt(LocalDateTime.now());

        // 문의 사항에 답변 연결
        inquiriesEntity.setAnswer(answerEntity);

        // 답변 저장 (연관 관계에 따라 문의 사항도 자동으로 업데이트)
        return answerRepository.save(answerEntity);
    }

    @Override
    public List<InquiriesEntity> getInquiriesList() {

        return feedbackRepository.findAll();
    }

    @Override
    public List<InquiriesEntity> getInquiriesList_user(Long userId) {
        return feedbackRepository.findByUserId(userId);
    }

    @Override
    public InquiriesEntity getInquiry(Long inquiriesId) {
        return feedbackRepository.findById(inquiriesId)
                        .orElseThrow(
                                ()-> new NotFoundException("해당 문의사항을 찾을 수 없습니다.")
                        );
    }

    @Override
    public AnswerEntity updateAnswer(String content, Long answerId) {
        AnswerEntity answerEntity = answerRepository.findById(answerId)
                .orElseThrow(()-> new NotFoundException("해당 답변을 찾을 수 없습니다."));
        answerEntity.setContent(content);

        return answerRepository.save(answerEntity);
    }

    @Override
    public InquiriesEntity updateStatus(Long inquiriesId, FeedbackStatus feedbackStatus) {
        InquiriesEntity inquiriesEntity = feedbackRepository.findById(inquiriesId)
                .orElseThrow(()->new NotFoundException("해당 문의 사항을 찾을 수 없습니다."));
        inquiriesEntity.setFeedbackStatus(feedbackStatus);

        return feedbackRepository.save(inquiriesEntity);
    }
}
