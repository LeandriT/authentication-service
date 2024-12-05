package com.seek.authentication_service.model;

import com.seek.authentication_service.model.enums.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "locations")
@SQLDelete(sql = "UPDATE locations SET is_deleted = true, deleted_at = NOW() WHERE uuid = ?")
@Where(clause = "is_deleted = false")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Location extends BaseModel {

    @Column(nullable = false, length = 20)
    private String code; // Código único de la localidad

    @Column(length = 20)
    private String parentCode; // Código del nivel superior, puede ser NULL

    @Column(nullable = false, length = 100)
    private String name; // Nombre de la localidad

    @Column(nullable = false, length = 10)
    private Status status; // Estado (ACTIVO, INACTIVO)
    @Builder.Default()
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>(); // Relación OneToMany con User
}