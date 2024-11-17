package Freeing.user_service.repository;

import Freeing.user_service.dto.FeedbackStatus;
import Freeing.user_service.dto.InquiriesCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Inquiries")
public class InquiriesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiriesId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiriesCategory category;
    @Column(nullable = false)
    private String inquiriesTitle;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus feedbackStatus;

    // Answer와의 일대일 관계 설정
    @OneToOne(mappedBy = "inquiries", cascade = CascadeType.ALL)
    private AnswerEntity answer;
}
