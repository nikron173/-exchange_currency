package com.nikron.conversion.service;

import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository repository = new CurrencyRepository();

    public List<Currency> findAll() {
        return repository.findAll().get();
    }

    public Currency findById(long id) {
        Optional<Currency> currency = repository.findById(id);
        if (currency.isPresent()) {
            return currency.get();
        }
        throw new NotFoundException("Currency id " + id + " не найден.");
    }

    public Currency save(Currency currency) {
        return repository.save(currency).get();
    }

    public void delete(long id) {
        repository.delete(id);
    }

    public void delete(String code) {
        repository.delete(code);
    }

    public Currency change(long id, Currency currency){
        return repository.change(id, currency).get();
    }

    public Currency findByCode(String code) {
        return repository.findByCode(code).get();
    }
}
