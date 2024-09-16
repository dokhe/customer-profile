package com.keteso.utils;

import com.keteso.enums.Status;

import java.util.EnumSet;
import java.util.Set;

public class Constants {
    public static final Set<Status> PROHIBITED_LINK_STATUS_SET = EnumSet.of(Status.INACTIVE);
    public static final Set<Status> PROHIBITED_ACTOR_STATUS_SET = EnumSet.of(Status.DELETE, Status.SUSPEND);
    public static final Set<Status> PROHIBITED_DISTRIBUTOR_STATUS_SET = EnumSet.of(Status.DELETE, Status.SUSPEND, Status.FREEZE);
    public static final Set<Status> PROHIBITED_SUBSCRIBER_STATUS_SET = EnumSet.of(Status.DELETE, Status.SUSPEND, Status.OPTOUT);

    public static final Set<Status> NON_ALLOWABLE_SET = EnumSet.of(Status.DELETE);
    public static final Set<Integer> NON_ALLOWABLE_STATUS = Set.of(Status.DELETE.getValue());
    public static final Set<Integer> PROHIBITED_LINK_STATUS = Set.of(Status.INACTIVE.getValue());
    public static final Set<Integer> PROHIBITED_ACTOR_STATUS = Set.of(Status.DELETE.getValue(), Status.SUSPEND.getValue());
    public static final Set<Integer> PROHIBITED_DISTRIBUTOR_STATUS = Set.of(Status.DELETE.getValue(), Status.SUSPEND.getValue(), Status.FREEZE.getValue());
    public static final Set<Integer> PROHIBITED_SUBSCRIBER_STATUS = Set.of(Status.DELETE.getValue(), Status.SUSPEND.getValue(), Status.OPTOUT.getValue());
}

