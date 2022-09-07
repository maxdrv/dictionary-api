package com.home.dictionary.model.lesson;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "lesson_item")
public class LessonItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_item_seq")
    @SequenceGenerator(name = "lesson_item_seq", sequenceName = "lesson_item_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private LessonItemStatus status;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private Long parentPhraseId;

    private Integer itemOrder;

    private String question;

    private String answerCorrect;

    @Nullable
    private String answerUser;

}
