package com.seek.authentication_service.model;

import com.seek.authentication_service.model.enums.ParkingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString(of = "plate")
public class Vehicle extends BaseModel {
    @Column(name = "plate", nullable = false, unique = true)
    private String plate;

    @Column(name = "dni")
    private String dni; // Identificación del usuario

    @Column(name = "full_name")
    private String fullName; // Nombre completo del usuario

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // Teléfono celular del usuario

    @Column(name = "secondary_phone_number")
    private String secondaryPhoneNumber; // Segundo teléfono opcional
    @Builder.Default
    @Column(name = "parking_date", updatable = false)
    private LocalDateTime parkingDate = LocalDateTime.now();
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Builder.Default()
    @Column(name = "parked_time")
    private Long parkedTime = 0L; // Almacena el tiempo en minutos
    @Builder.Default()
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ParkingStatus parkingStatus = ParkingStatus.PARKED; // Valor por defecto
    @Builder.Default()
    @Column(name = "amount_charged", nullable = false, precision = 10, scale = 2) // Para dinero
    private BigDecimal amountCharged = BigDecimal.ZERO;
    @Builder.Default()
    @Column(name = "amount_calculated", nullable = false, precision = 10, scale = 2) // Para dinero
    private BigDecimal amountCalculated = BigDecimal.ZERO;

    @Column(name = "rate", nullable = false, precision = 10, scale = 2) // Para dinero
    private BigDecimal rate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_uuid", referencedColumnName = "uuid", nullable = false)
    private Location location;

    @Column(name = "brand")
    private String brand;
    @Column(name = "model")
    private String model;
    @Column(name = "model_year")
    private String modelYear;
    @Column(name = "manufacturing_country")
    private String manufacturingCountry;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_uuid")
    @NotNull
    private User user;


}