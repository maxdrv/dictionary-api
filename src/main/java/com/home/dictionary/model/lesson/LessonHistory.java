package com.home.dictionary.model.lesson;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "lesson_history")
public class LessonHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_history_seq")
    @SequenceGenerator(name = "lesson_history_seq", sequenceName = "lesson_history_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    private Long lessonId;

    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    private Instant statusUpdatedAt;

    public LessonHistory(Long lessonId, LessonStatus status, Instant statusUpdatedAt) {
        this.lessonId = lessonId;
        this.status = status;
        this.statusUpdatedAt = statusUpdatedAt;
    }

}
