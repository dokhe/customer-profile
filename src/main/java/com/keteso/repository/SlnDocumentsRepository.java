package com.keteso.repository;

import com.keteso.entity.SlnDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlnDocumentsRepository extends JpaRepository<SlnDocuments, Long> {
}
