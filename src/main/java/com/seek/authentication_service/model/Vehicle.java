package com.seek.authentication_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET is_deleted = true, deleted_at = NOW() WHERE uuid = ?")
@Where(clause = "is_deleted = false")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle extends BaseModel {
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "dni")
    private String dni; // Identificación del usuario

    @Column(name = "full_name")
    private String fullName; // Nombre completo del usuario

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // Teléfono celular del usuario

    @Column(name = "secondary_phone_number")
    private String secondaryPhoneNumber; // Segundo teléfono opcional

    @Column(name = "registration_date", updatable = false)
    private LocalDateTime registrationDate;

    @Builder.Default()
    @Column(name = "parked_time")
    private Long parkedTime = 0L; // Almacena el tiempo en milisegundos

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ParkingStatus parkingStatus; // Valor por defecto

    @Column(name = "amount_charged", nullable = false, precision = 10, scale = 2) // Para dinero
    private BigDecimal amountCharged;

    @Column(name = "rate", nullable = false, precision = 10, scale = 2) // Para dinero
    private BigDecimal rate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    @NotNull
    private User user;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now(); // Establece la fecha de registro al crear
    }

}