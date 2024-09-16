package com.keteso.repository;


import com.keteso.entity.OtpHistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpHistEntityRepo extends JpaRepository<OtpHistEntity, Long> {
    List<OtpHistEntity> findByChannelIdAndIdentifier(Long channelId, String identifier);
}
