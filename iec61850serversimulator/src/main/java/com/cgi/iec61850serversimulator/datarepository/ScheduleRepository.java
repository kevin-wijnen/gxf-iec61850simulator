package com.cgi.iec61850serversimulator.datarepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.iec61850serversimulator.datamodel.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findByIndexNumberAndRelayId(int indexNumber, long relayId);

    boolean existsByIndexNumberAndRelayId(int indexNumber, long relayId);
}
