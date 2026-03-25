package com.example.ecommerceproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.example.ecommerceproject.audit.Auditable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "users",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false, length = 30)
    String firstName;

    @Column(length = 30)
    String middleName;

    @Column(nullable = false, length = 30)
    String lastName;

    @Column(name = "password", nullable = false)
    String passwordHash;

    @Column(name = "is_deleted")
    boolean isDeleted;

    @Column(name = "is_active")
    boolean isActive;

    @Column(name = "is_expired")
    boolean isExpired;

    @Column(name = "is_locked")
    boolean isLocked;

    @Column(name = "invalid_attempt_count")
    Integer invalidAttemptCount = 0;

    @Column(name = "password_update_date")
    LocalDateTime passwordUpdateDate;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    Set<UserRole> userRoles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Address> addresses = new ArrayList<>();
}
