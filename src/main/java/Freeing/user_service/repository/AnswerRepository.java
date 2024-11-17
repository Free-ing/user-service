package Freeing.user_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    AnswerEntity findByInquiries_InquiriesId(Long inquiriesId);  // 특정 문의에 대한 답변 조회
}
