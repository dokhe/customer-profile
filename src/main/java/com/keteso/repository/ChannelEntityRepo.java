package com.keteso.repository;


import com.keteso.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelEntityRepo extends JpaRepository<ChannelEntity, Long> {
    Optional<ChannelEntity> findByChannelCode(String channelCode);
}
