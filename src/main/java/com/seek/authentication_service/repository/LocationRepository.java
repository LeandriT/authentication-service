package com.seek.authentication_service.repository;

import com.seek.authentication_service.model.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    @Query("""
            select l from Location l 
            WHERE 
                (:location is null or lower(l.name) like lower(concat('%', :location, '%'))) 
                and length(l.code) = 8
                order by l.name asc
            """)
    List<Location> findByLocation(@Param("location") String location);
}
