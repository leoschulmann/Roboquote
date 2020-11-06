package com.leoschulmann.roboquote.quoteservice.repositories;

import com.leoschulmann.roboquote.quoteservice.entities.ItemPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPositionRepo extends JpaRepository<ItemPosition, Integer> {
}
