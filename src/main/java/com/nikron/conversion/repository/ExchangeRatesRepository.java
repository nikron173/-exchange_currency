package com.nikron.conversion.repository;

import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.DataBaseServiceImpl;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ExchangeRatesRepository implements ImpRepository<ExchangeRates> {

    private final Connection connection = new DataBaseServiceImpl().getDataBaseConnection();
    private final ExchangeRatesMapper mapper = new ExchangeRatesMapper();

    @Override
    public Optional<ExchangeRates> findById(long id) {
        String findById = "SELECT e.id, b.id, b.code, b.full_name, b.sign, t.id, t.code, t.full_name, t.sign, e.rate" +
                " FROM exchange_rates AS e\n" +
                " JOIN currency as b ON e.base_currency_id = b.id AND e.id = ?\n" +
                " JOIN currency as t ON e.target_currency_id = t.id";
        try (PreparedStatement ps = connection.prepareStatement(findById)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapper.resultSetToExchangeRates(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRates> findByCode(String code) {
        String findByCode = "SELECT e.id, (b.code || t.code) AS codex, e.rate FROM exchange_rates AS e\n" +
                " JOIN currency AS b ON e.base_currency_id = b.id\n" +
                " JOIN currency AS t ON e.target_currency_id = t.id\n" +
                " WHERE codex = ?";
        try (PreparedStatement ps = connection.prepareStatement(findByCode)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapper.resultSetToExchangeRates(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRates> findByCodeReverte(String code) {
        String findByCodeReverte = "SELECT e.id, (t.code || b.code) AS rev_codex, ROUND((1/e.rate), 6) AS rev_rate " +
                " FROM exchange_rates AS e\n" +
                " JOIN currency as b ON e.base_currency_id = b.id\n" +
                " JOIN currency as t ON e.target_currency_id = t.id\n" +
                " WHERE rev_codex = ?";
        try (PreparedStatement ps = connection.prepareStatement(findByCodeReverte)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapper.resultSetToExchangeRates(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<ExchangeRates>> findAll() {
        List<ExchangeRates> exchangeRates = new ArrayList<>();
        String findAll = "SELECT e.id, b.id, b.code, b.full_name, b.sign, t.id, t.code, t.full_name, t.sign, e.rate " +
                " FROM exchange_rates as e \n" +
                " JOIN currency as b ON e.base_currency_id = b.id \n" +
                " JOIN currency as t ON e.target_currency_id = t.id";
        try (PreparedStatement ps = connection.prepareStatement(findAll)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                exchangeRates.add(mapper.resultSetToExchangeRates(rs));
            }
            return exchangeRates.size() == 0 ? Optional.empty() : Optional.of(exchangeRates);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRates> save(ExchangeRates tObject) {
        String saveExchangeRates = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)" +
                " VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(saveExchangeRates, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, tObject.getBaseCurrency().getId());
            ps.setLong(2, tObject.getTargetCurrency().getId());
            ps.setBigDecimal(3, tObject.getRate());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                tObject.setId(rs.getLong(1));
            }
            return Optional.of(tObject);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRates> change(long id, ExchangeRates tObject) {
        findById(id).orElseThrow(() -> new NotFoundException("Не найдет обменник с id " + id ));
        String updateExchangeRates = "UPDATE exchange_rates SET rate = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateExchangeRates)) {
            ps.setBigDecimal(1, tObject.getRate());
            ps.setLong(2, id);
            ps.execute();
            return Optional.of(tObject);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Не найдет обменник с id " + id ));
        String updateExchangeRates = "DELETE FROM exchange_rates WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateExchangeRates)) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
