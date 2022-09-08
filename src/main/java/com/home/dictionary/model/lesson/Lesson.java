package com.home.dictionary.model.lesson;

import lombok.*;
import one.util.streamex.StreamEx;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_seq")
    @SequenceGenerator(name = "lesson_seq", sequenceName = "lesson_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    private Instant startAt;

    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    private Long parentPlanId;

    private String description;

    @Setter(AccessLevel.PUBLIC)
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonItem> lessonItems;

    public Lesson(Instant startAt, Long parentPlanId, String description) {
        this.startAt = startAt;
        this.status = LessonStatus.NOT_STARTED;
        this.parentPlanId = parentPlanId;
        this.description = description;
    }

    public Optional<LessonItem> nextItem() {
        return StreamEx.of(getLessonItems())
                .sortedBy(LessonItem::getItemOrder)
                .findFirst(item -> item.getStatus() == LessonItemStatus.NOT_STARTED);
    }

    public Optional<LessonItem> findItemById(Long lessonItemId) {
        return StreamEx.of(getLessonItems())
                .findFirst(item -> Objects.equals(lessonItemId, item.getId()));
    }

    public void tryToStart() {
        if (this.status == LessonStatus.STARTED) {
            return;
        }
        this.status = LessonStatus.STARTED;
    }

    public void finish() {
        if (this.status == LessonStatus.FINISHED) {
            return;
        }
        this.status = LessonStatus.FINISHED;
    }

}
