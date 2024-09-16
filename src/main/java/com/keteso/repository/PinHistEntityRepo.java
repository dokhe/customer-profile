package com.keteso.repository;


import com.keteso.entity.PinHistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PinHistEntityRepo extends JpaRepository<PinHistEntity, Long> {
    List<PinHistEntity> findByChannelIdAndIdentifier(Long channelId, String identifier);
}
