package com.keteso.repository;

import com.keteso.entity.CustomerProfile;
import com.keteso.entity.SlnDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByNationalId(String nationalId);
    boolean existsByIdentificationTypeAndNationalId(SlnDocuments identificationType, String nationalId);

    Optional<Object> findByMobileNumber(String identifier);
}


