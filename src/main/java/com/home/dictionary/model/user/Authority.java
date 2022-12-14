package com.home.dictionary.model.user;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "authority")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_seq")
    @SequenceGenerator(name = "authority_seq", sequenceName = "authority_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private AuthorityType type;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private ApiUser user;

    public Authority(AuthorityType type, ApiUser user) {
        this.type = type;
        this.user = user;
    }

}
