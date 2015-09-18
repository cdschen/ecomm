package com.sooeez.ecomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sooeez.ecomm.domain.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
