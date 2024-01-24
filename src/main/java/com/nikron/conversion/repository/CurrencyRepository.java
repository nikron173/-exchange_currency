package com.nikron.conversion.repository;

import com.nikron.conversion.exception.DuplicateException;
import com.nikron.conversion.exception.NotFoundException;
import com.nikron.conversion.mapper.CurrencyMapper;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.service.DataBaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sqlite.SQLiteErrorCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CurrencyRepository implements ImpRepository<Long, Currency> {

    private final Connection connection = new DataBaseServiceImpl().getDataBaseConnection();
    private final CurrencyMapper mapper = new CurrencyMapper();

    @Override
    public Optional<Currency> findById(Long id) {
        String findById = "SELECT id, code, full_name, sign FROM currency WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(findById)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapper.resultSetToCurrency(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<Currency>> findAll() {
        List<Currency> currencies = new ArrayList<>();
        String findAll = "SELECT id, code, full_name, sign FROM currency";
        try (PreparedStatement ps = connection.prepareStatement(findAll)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                currencies.add(mapper.resultSetToCurrency(rs));
            }
            return currencies.size() == 0 ? Optional.empty() : Optional.of(currencies);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> save(Currency tObject) {
        String saveCurrency = "INSERT INTO currency(code, full_name, sign) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(saveCurrency, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tObject.getCode());
            ps.setString(2, tObject.getFullName());
            ps.setString(3, tObject.getSign());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            tObject.setId(rs.getLong(1));
            return Optional.of(tObject);
        } catch (SQLException e) {
            if (SQLiteErrorCode.SQLITE_CONSTRAINT.code == e.getErrorCode())
                throw new DuplicateException("Code " + tObject.getCode() + " уже существует.", 400);
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> change(Long id, Currency tObject) {
        findById(id).orElseThrow(() -> new NotFoundException("Объект с id " + id + " не найден!", 404));
        String updateCurrency = "UPDATE currency SET code=?, full_name=?, sign=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(updateCurrency)) {
            ps.setString(1, tObject.getCode());
            ps.setString(2, tObject.getFullName());
            ps.setString(3, tObject.getSign());
            ps.setLong(4, id);
            ps.execute();
            tObject.setId(id);
            return Optional.of(tObject);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Объект с id " + id + " не найден!", 404));
        String deleteCurrency = "DELETE FROM currency WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(deleteCurrency)) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String code) {
        findByCode(code).orElseThrow(() -> new NotFoundException("Объект с code " + code + " не найден!", 404));
        String deleteCurrency = "DELETE FROM currency WHERE code=?";
        try (PreparedStatement ps = connection.prepareStatement(deleteCurrency)) {
            ps.setString(1, code);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        String findByCode = "SELECT id, code, full_name, sign FROM currency WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(findByCode)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return Optional.of(mapper.resultSetToCurrency(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
