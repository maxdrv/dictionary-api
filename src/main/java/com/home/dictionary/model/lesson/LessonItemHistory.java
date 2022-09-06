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
@Table(name = "lesson_item_history")
public class LessonItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_item_history_seq")
    @SequenceGenerator(name = "lesson_item_history_seq", sequenceName = "lesson_item_history_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    private Long lessonItemId;

    @Enumerated(EnumType.STRING)
    private LessonItemStatus status;

    private Instant statusUpdatedAt;

    public LessonItemHistory(Long lessonItemId, LessonItemStatus status, Instant statusUpdatedAt) {
        this.lessonItemId = lessonItemId;
        this.status = status;
        this.statusUpdatedAt = statusUpdatedAt;
    }

}
