package com.nikron.conversion.service;

import com.nikron.conversion.dto.ExchangeRatesDto;
import com.nikron.conversion.dto.ExchangeRequestDto;
import com.nikron.conversion.dto.ExchangeResponseDto;
import com.nikron.conversion.exception.BadRequestException;
import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.repository.CurrencyRepository;
import com.nikron.conversion.repository.ExchangeRatesRepository;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesService {

    private final ExchangeRatesRepository exchangeRatesRepository = ExchangeRatesRepository.getInstanceRepository();
    private final CurrencyRepository currencyRepository = CurrencyRepository.getInstanceRepository();

    private static final ExchangeRatesService INSTANCE_SERVICE = new ExchangeRatesService();

    private ExchangeRatesService() {
    }

    public static ExchangeRatesService getInstanceService() {
        return INSTANCE_SERVICE;
    }

    public ExchangeRates findById(long id) {
        Optional<ExchangeRates> exchangeRates = exchangeRatesRepository.findById(id);
        if (exchangeRates.isPresent()) {
            return exchangeRates.get();
        }
        throw new NotFoundException("Не найден обменник валют с id " + id,
                HttpServletResponse.SC_NOT_FOUND);
    }

    public ExchangeRates findByCode(String code) {
        Optional<ExchangeRates> exchangeRates = exchangeRatesRepository.findByCode(code);
        if (exchangeRates.isPresent()) {
            return exchangeRates.get();
        }
        exchangeRates = exchangeRatesRepository.findByCodeRevert(code);
        if (exchangeRates.isPresent()) {
            return exchangeRates.get();
        }
        throw new NotFoundException("Не найден обменник валют с кодом " + code,
                HttpServletResponse.SC_NOT_FOUND);
    }

    public List<ExchangeRates> findAll() {
        return exchangeRatesRepository.findAll();
    }

    public ExchangeRates update(Long id, BigDecimal rate) {
        Optional<ExchangeRates> optionalExchangeRates = exchangeRatesRepository.findById(id);
        if (optionalExchangeRates.isEmpty()) {
            throw new NotFoundException("Не найден обменник валют с id " + id, HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRates exchangeRates = optionalExchangeRates.get();
        exchangeRates.setRate(rate);
        return exchangeRatesRepository.update(id, exchangeRates).get();
    }

    public ExchangeRates update(String code, BigDecimal rate) {
        Optional<ExchangeRates> optionalExchangeRates = exchangeRatesRepository.findByCode(code);
        if (optionalExchangeRates.isEmpty()) {
            throw new NotFoundException("Не найден обменник валют с id " + code,
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRates exchangeRates = optionalExchangeRates.get();
        exchangeRates.setRate(rate);
        return exchangeRatesRepository.update(exchangeRates.getId(), exchangeRates).get();
    }

    public void delete(Long id) {
        exchangeRatesRepository.delete(id);
    }

    public ExchangeRates addExchangeRates(ExchangeRatesDto dto) {
        //return repository.save(exchangeRates).get();
        Optional<Currency> baseCurrency = currencyRepository.findByCode(dto.getBaseCurrencyCode());
        Optional<Currency> targetCurrency = currencyRepository.findByCode(dto.getTargetCurrencyCode());
        if (baseCurrency.isEmpty()) {
            throw new BadRequestException("Не найден обменник валют с кодом " + dto.getBaseCurrencyCode(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        if (targetCurrency.isEmpty()) {
            throw new BadRequestException("Не найден обменник валют с кодом " + dto.getTargetCurrencyCode(),
                    HttpServletResponse.SC_BAD_REQUEST);
        }
        ExchangeRates exchangeRates = new ExchangeRates();
        exchangeRates.setBaseCurrency(baseCurrency.get());
        exchangeRates.setTargetCurrency(targetCurrency.get());
        exchangeRates.setRate(dto.getRate());
        return exchangeRatesRepository.save(exchangeRates).get();
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto dto) {
        ExchangeRates exchangeRates = findByCode(dto.getBaseCurrencyCode() + dto.getTargetCurrencyCode());
        ExchangeResponseDto dtoResponse = new ExchangeResponseDto();
        dtoResponse.setBaseCurrency(exchangeRates.getBaseCurrency());
        dtoResponse.setTargetCurrency(exchangeRates.getTargetCurrency());
        dtoResponse.setRate(exchangeRates.getRate());
        dtoResponse.setAmount(dto.getAmount());
        dtoResponse.setConvertedAmount(exchangeRates.getRate().multiply(dto.getAmount()));
        return dtoResponse;
    }
}
