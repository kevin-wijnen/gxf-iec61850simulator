package com.cgi.iec61850serversimulator.datarepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.iec61850serversimulator.datamodel.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findByIndexNumber(int index_number);

    boolean existsByIndexNumber(int index_number);
}
