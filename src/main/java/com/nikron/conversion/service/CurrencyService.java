package com.nikron.conversion.service;

import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.repository.CurrencyRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyRepository currencyRepository = CurrencyRepository.getInstanceRepository();

    private static final CurrencyService INSTANCE_SERVICE = new CurrencyService();

    private CurrencyService() {
    }

    public static CurrencyService getInstanceService() {
        return INSTANCE_SERVICE;
    }

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Currency findById(long id) {
        Optional<Currency> currency = currencyRepository.findById(id);
        if (currency.isPresent()) {
            return currency.get();
        }
        throw new NotFoundException("Валюта с id " + id + " не найдена",
                HttpServletResponse.SC_NOT_FOUND);
    }

    public Currency save(Currency currency) {
        return currencyRepository.save(currency).get();
    }

    public void delete(long id) {
        currencyRepository.delete(id);
    }

    public void delete(String code) {
        currencyRepository.delete(code);
    }

    public Currency update(long id, Currency currency) {
        return currencyRepository.update(id, currency).get();
    }

    public Currency findByCode(String code) {
        Optional<Currency> currency = currencyRepository.findByCode(code);
        if (currency.isPresent()){
            return currency.get();
        }
        throw new NotFoundException("Валюта с кодом " + code + " не найдена",
                HttpServletResponse.SC_NOT_FOUND);
    }
}
