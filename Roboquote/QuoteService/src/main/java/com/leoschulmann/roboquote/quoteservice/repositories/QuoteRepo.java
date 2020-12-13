package com.leoschulmann.roboquote.quoteservice.repositories;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepo extends JpaRepository<Quote, Integer> {
    Optional<Quote> findDistinctById(Integer id);

    boolean existsByNumber(String serialNumber);

    List<Quote> findAllByNumber(String number);
}
