package com.example.cafebackend.repository;

import com.example.cafebackend.model.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("select b from Bill b where b.uuid=:fileName")
    Optional<Bill> findByUUID(String fileName);
}
