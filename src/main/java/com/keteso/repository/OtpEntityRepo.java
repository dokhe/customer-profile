package com.keteso.repository;


import com.keteso.entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpEntityRepo extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByChannelIdAndIdentifier(Long channelId, String identifier);

    Optional<OtpEntity> findByChannelIdAndIdentifierAndStatus(Long recNo, String identifier, int active);
}
