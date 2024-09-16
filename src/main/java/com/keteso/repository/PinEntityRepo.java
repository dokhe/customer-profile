package com.keteso.repository;


import com.keteso.entity.PinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PinEntityRepo extends JpaRepository<PinEntity, Long> {
    Optional<PinEntity> findByChannelIdAndIdentifier(Long channelId, String identifier);
}
