package com.leoschulmann.roboquote.quoteservice.repositories;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.projections.QuoteWithoutSections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuoteRepo extends JpaRepository<Quote, Integer> {
    Optional<Quote> findDistinctById(Integer id);

    boolean existsByNumber(String serialNumber);

    List<Quote> findAllByNumber(String number);

    @Query("select q.created as createdDate, q.number as serialNumber, q.version as version, " +
            "q.customer as customer, q.dealer as dealer, q.finalPrice as finalPrice from Quote q")
    List<QuoteWithoutSections> getAllQuoteProjections();
}
