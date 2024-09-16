package com.keteso.repository;


import com.keteso.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenEntityRepo extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByToken(String token);

    List<TokenEntity> findByIdentifierAndStatus(String identifier, int status);

}
