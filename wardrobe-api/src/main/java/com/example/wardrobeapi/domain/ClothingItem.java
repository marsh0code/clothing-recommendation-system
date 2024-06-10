package com.example.wardrobeapi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "CLOTHING_ITEMS")
public class ClothingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "warmth")
    private Double warmth;

    @Column(name = "wind_resistance")
    private Double windResistance;

    @Column(name = "water_resistance")
    private Double waterResistance;

    @Column(name = "style_coefficient")
    private Double styleCoefficient;

    @Enumerated(EnumType.STRING)
    @Column(name = "clothing_type")
    private ClothingType clothingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "style_type")
    private StyleType styleType;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ClothingItem that = (ClothingItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
