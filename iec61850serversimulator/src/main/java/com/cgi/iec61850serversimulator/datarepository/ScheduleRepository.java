package com.cgi.iec61850serversimulator.datarepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.iec61850serversimulator.datamodel.RelayEntity;

public interface ScheduleRepository extends JpaRepository<RelayEntity, Long> {
    List<RelayEntity> findByIndexNumber(int index_number);

    boolean existsByIndexNumber(int index_number);
}
