package com.keteso.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "service.statuses")
public class EntityStatusProperties
{
    private int inactive;
    private int active;
    private int delete;
    private int freeze;
    private int unfreeze;
    private int suspend;
    private int unsuspend;
    private int optout;
}
