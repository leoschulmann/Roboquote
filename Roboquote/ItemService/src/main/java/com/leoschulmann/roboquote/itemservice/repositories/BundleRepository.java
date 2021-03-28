package com.leoschulmann.roboquote.itemservice.repositories;

import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import com.leoschulmann.roboquote.itemservice.entities.projections.BundleWithoutPositions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BundleRepository extends JpaRepository<Bundle, Integer> {

    @Query("select b.id as id, b.nameRus as nameRus, b.nameEng as nameEng from Bundle b")
    List<BundleWithoutPositions> getAllBundlesNamesAndIds();
}
