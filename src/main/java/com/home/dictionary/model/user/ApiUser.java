package com.home.dictionary.model.user;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "api_user")
public class ApiUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_user_seq")
    @SequenceGenerator(name = "api_user_seq", sequenceName = "api_user_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    private String username;

    private String password;

    private String email;

    private boolean enabled;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Authority> authorities;

    private Instant created;

    @Setter(AccessLevel.PUBLIC)
    @Nullable
    private Long currentLessonId;

    @Nullable
    private String refreshToken;

    public ApiUser(
            String username,
            String password,
            String email,
            boolean enabled,
            List<Authority> authorities,
            Instant created
    ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.authorities = authorities;
        this.created = created;
    }

    public void enable() {
        if (this.enabled) {
            return;
        }
        this.enabled = true;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
