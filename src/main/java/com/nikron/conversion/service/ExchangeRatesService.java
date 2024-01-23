package com.nikron.conversion.service;

import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.repository.ExchangeRatesRepository;

import java.util.List;
import java.util.Optional;

public class ExchangeRatesService {

    private final ExchangeRatesRepository repository = new ExchangeRatesRepository();

    public Optional<ExchangeRates> findById(long id) {
        return repository.findById(id);
    }

    public Optional<ExchangeRates> findByCode(String code) {
        //Optional<ExchangeRates> exchangeRates = repository.
        return null;
    }

    public Optional<List<ExchangeRates>> findAll() {
        return repository.findAll();
    }

    public Optional<ExchangeRates> change(long id, ExchangeRates exchangeRates) {
        return repository.change(id, exchangeRates);
    }

    public void delete(long id) {
        repository.delete(id);
    }

    public ExchangeRates addExchangeRates(ExchangeRates exchangeRates) {
        return repository.save(exchangeRates).get();
    }
}
