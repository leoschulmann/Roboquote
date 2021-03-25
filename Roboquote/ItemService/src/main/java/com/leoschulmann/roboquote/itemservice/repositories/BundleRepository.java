package com.leoschulmann.roboquote.itemservice.repositories;

import com.leoschulmann.roboquote.itemservice.entities.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BundleRepository extends JpaRepository<Bundle, Integer> {
}
