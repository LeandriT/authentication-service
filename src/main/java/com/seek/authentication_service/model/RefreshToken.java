package com.seek.authentication_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Entity representing a refresh token for authentication purposes.
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
@SQLDelete(sql = "UPDATE refresh_tokens SET is_deleted = true, deleted_at = NOW() WHERE uuid = ?")
@Where(clause = "is_deleted = false")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseModel {

    // Constants for database column names
    public static final String USER_ID_COLUMN = "user_uuid";

    /**
     * The refresh token string.
     */
    @NotBlank(message = "Token cannot be blank")
    @Column(name = "token_uuid")
    private String token;

    /**
     * The expiry date of the refresh token.
     */
    @NotNull(message = "Expiration date cannot be null")
    private Instant expirationDate;

    /**
     * The user associated with this refresh token.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID_COLUMN, referencedColumnName = "uuid")
    @NotNull(message = "User cannot be null")
    private User user;
}