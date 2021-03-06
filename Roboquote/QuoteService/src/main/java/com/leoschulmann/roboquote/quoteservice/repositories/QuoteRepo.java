package com.leoschulmann.roboquote.quoteservice.repositories;

import com.leoschulmann.roboquote.quoteservice.entities.Quote;
import com.leoschulmann.roboquote.quoteservice.entities.projections.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface QuoteRepo extends JpaRepository<Quote, Integer> {
    Optional<Quote> findDistinctById(Integer id);

    boolean existsByNumber(String serialNumber);

    List<Quote> findAllByNumber(String number);

    @Query("select q.id as id, q.created as createdDate, q.createdTimestamp as createdDateTime," +
            " q.number as serialNumber, q.version as version, q.customer as customer, q.dealer as dealer, " +
            "q.finalPrice as finalPrice, q.comment as comment, q.cancelled as cancelled from Quote q")
    List<QuoteWithoutSections> getAllQuoteProjections();

    @Query("select q.id as id, q.created as createdDate, q.createdTimestamp as createdDateTime," +
            " q.number as serialNumber, q.version as version, q.customer as customer, q.dealer as dealer, " +
            "q.finalPrice as finalPrice, q.comment as comment, q.cancelled as cancelled from Quote q " +
            "where q.cancelled = false or q.cancelled is null")
    List<QuoteWithoutSections> getAllUncancelledQuoteProjections();

    @Query("select q.number as serialNumber, q.version as version from Quote q where q.id = :id")
    QuoteSerialAndVersion getSerialAndVersionForId(@Param("id") int id);

    @Transactional
    @Modifying
    @Query("update Quote q set q.comment = :comment where q.id = :id")
    void addComment(int id, String comment);

    @Transactional
    @Modifying
    @Query("update Quote q set q.cancelled = :action where q.id = :id")
    void setCancelled(int id, boolean action);

    @Query("select distinct q.installation as installation from Quote q")
    List<InstallationProjection> getDistinctInstallations();

    @Query("select distinct q.paymentTerms as payment from Quote q")
    List<PaymentProjection> getDistinctPayments();

    @Query("select distinct q.shippingTerms as shipping from Quote q")
    List<ShippingProjection> getDistinctShipping();

    @Query("select distinct q.warranty as warranty from Quote q")
    List<WarrantyProjection> getDistinctWarranties();

    @Query("select distinct q.dealer as dealer from Quote q")
    List<DealerProjection> getDistinctDealers();

    @Query("select distinct q.customer as customer from Quote q")
    List<CustomerProjection> getDistinctCustomers();


    @Query("select qs.quote.created.id as id, qs.quote.created.created as createdDate, " +
            "qs.quote.created.createdTimestamp as createdDateTime, qs.quote.created.number as serialNumber, " +
            "qs.quote.created.version as version, qs.quote.created.customer as customer, " +
            "qs.quote.created.dealer as dealer, qs.quote.created.finalPrice as finalPrice, " +
            "qs.quote.created.comment as comment, qs.quote.created.cancelled as cancelled " +
            "from ItemPosition ip inner join ip.section qs where ip.itemId = :id")
    List<QuoteWithoutSections> getAllQuoteProjectionsForItemId(int id);

}
