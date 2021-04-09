package com.cgi.iec61850serversimulator.datarepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.iec61850serversimulator.datamodel.RelayEntity;

public interface RelayRepository extends JpaRepository<RelayEntity, Long> {
    List<RelayEntity> findByIndexNumber(int indexNumber);

    boolean existsByIndexNumber(int indexNumber);
}
