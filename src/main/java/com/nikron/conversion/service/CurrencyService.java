package com.nikron.conversion.service;

import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.repository.CurrencyRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyRepository repository = CurrencyRepository.getInstanceRepository();

    private static final CurrencyService INSTANCE_SERVICE = new CurrencyService();

    private CurrencyService() {
    }

    public static CurrencyService getInstanceService() {
        return INSTANCE_SERVICE;
    }

    public List<Currency> findAll() {
        Optional<List<Currency>> currencies = repository.findAll();
        if (currencies.isPresent()){
            return currencies.get();
        }
        return new ArrayList<>();
    }

    public Currency findById(long id) {
        Optional<Currency> currency = repository.findById(id);
        if (currency.isPresent()) {
            return currency.get();
        }
        throw new NotFoundException("Валюта с id " + id + " не найдена",
                HttpServletResponse.SC_NOT_FOUND);
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

    public Currency change(long id, Currency currency) {
        return repository.change(id, currency).get();
    }

    public Currency findByCode(String code) {
        Optional<Currency> currency = repository.findByCode(code);
        if (currency.isPresent()){
            return currency.get();
        }
        throw new NotFoundException("Валюта с кодом " + code + " не найдена",
                HttpServletResponse.SC_NOT_FOUND);
    }
}
