package com.keteso.repository;


import com.keteso.entity.SlnGender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SlnGenderRepository extends JpaRepository<SlnGender, Long> {
}
