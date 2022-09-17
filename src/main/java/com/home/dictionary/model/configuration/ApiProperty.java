package com.home.dictionary.model.configuration;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

@Entity
@Table(name = "api_property")
public class ApiProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_property_seq")
    @SequenceGenerator(name = "api_property_seq", sequenceName = "api_property_seq", allocationSize = 10)
    @EqualsAndHashCode.Include
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    private String key;

    private String value;

    public ApiProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
