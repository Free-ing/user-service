package Freeing.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<InquiriesEntity, Long> {

    public List<InquiriesEntity>findByUserId(Long userId);
}
