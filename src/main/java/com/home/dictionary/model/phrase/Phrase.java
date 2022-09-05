package com.home.dictionary.model.phrase;

import com.home.dictionary.model.lesson.Lesson;
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
@Table(name = "phrase")
public class Phrase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phrase_seq")
    @SequenceGenerator(name = "phrase_seq", sequenceName = "phrase_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private String source;

    @Enumerated(EnumType.STRING)
    private Lang sourceLang;

    private String transcription;

    private String target;

    @Enumerated(EnumType.STRING)
    private Lang targetLang;



}
