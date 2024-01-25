package com.nikron.conversion.repository;

import com.nikron.conversion.exception.DuplicateException;
import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.mapper.ExchangeRatesMapper;
import com.nikron.conversion.model.ExchangeRates;
import com.nikron.conversion.service.DataBaseServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteErrorCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesRepository implements ImpRepository<Long, ExchangeRates> {

    private final Connection connection = new DataBaseServiceImpl().getDataBaseConnection();
    private final ExchangeRatesMapper mapper = ExchangeRatesMapper.getInstanceMapper();
    private static final ExchangeRatesRepository INSTANCE_REPOSITORY = new ExchangeRatesRepository();

    private ExchangeRatesRepository() {
    }

    public static ExchangeRatesRepository getInstanceRepository(){
        return INSTANCE_REPOSITORY;
    }

    @Override
    public Optional<ExchangeRates> findById(Long id) {
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
        String findByCode = "select e.id, b.id, b.code, b.full_name, b.sign, t.id, t.code, t.full_name, t.sign, e.rate" +
                " from exchange_rates as e\n" +
                " JOIN currency as b ON e.base_currency_id = b.id\n" +
                " JOIN currency as t ON e.target_currency_id = t.id and (b.code || t.code) = ?";
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

    public Optional<ExchangeRates> findByCodeRevert(String code) {
        String findByCodeReverte = "select e.id, t.id, t.code, t.full_name, t.sign, b.id, b.code, b.full_name, b.sign," +
                " round((1/e.rate), 6) as rev_rate from exchange_rates as e\n" +
                " JOIN currency as b ON e.base_currency_id = b.id\n" +
                " JOIN currency as t ON e.target_currency_id = t.id and (t.code || b.code) = ?";
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
        try (PreparedStatement ps = connection.prepareStatement(findAll)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
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
            if (rs.next()) {
                tObject.setId(rs.getLong(1));
            }
            return Optional.of(tObject);
        } catch (SQLException e) {
            if (SQLiteErrorCode.SQLITE_CONSTRAINT.code == e.getErrorCode())
                throw new DuplicateException("Такой обменник уже существует.",
                        HttpServletResponse.SC_NOT_FOUND);
            throw new RuntimeException(e);
        }
    }

    public Optional<ExchangeRates> change(Long id, ExchangeRates exchangeRates) {
        String updateExchangeRates = "UPDATE exchange_rates SET rate = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateExchangeRates)) {
            ps.setBigDecimal(1, exchangeRates.getRate());
            ps.setLong(2, id);
            ps.executeUpdate();
            return findById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Не найдет обменник с id " + id,
                HttpServletResponse.SC_NOT_FOUND));
        String updateExchangeRates = "DELETE FROM exchange_rates WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateExchangeRates)) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
