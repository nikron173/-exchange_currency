package com.nikron.conversion.dao;

import com.nikron.conversion.exceptions.MyException;
import com.nikron.conversion.model.Currency;
import com.nikron.conversion.model.ExchangeVolute;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class DataDB {
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_URL;
    private static String DB_DRIVER;

    static {
        try {
            Properties properties = new Properties();
            Path pathParameterFileDB = Path.of(Objects.requireNonNull(DataDB.class.getClassLoader().getResource("parameters_db.properties")).getPath());
            InputStream inputStream = Files.newInputStream(pathParameterFileDB);
            properties.load(inputStream);
            DB_USER = properties.getProperty("DB_USER");
            DB_URL = properties.getProperty("DB_URL");
            DB_PASSWORD = properties.getProperty("DB_PASSWORD");
            DB_DRIVER = properties.getProperty("DB_DRIVER");
            inputStream.close();
        } catch (IOException | NullPointerException e){
            try {
                throw new MyException("not found parameters db file.");
            } catch (MyException ex) {
                throw new RuntimeException(ex);
            }
        }

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            try {
                throw new MyException("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            } catch (MyException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static Connection connection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static List<com.nikron.conversion.model.Currency> getCurrencies() throws MyException {
        List<com.nikron.conversion.model.Currency> currencyList = new ArrayList<>();
        try (Connection connection = connection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM currencies");
            while (resultSet.next()){
                currencyList.add(new com.nikron.conversion.model.Currency(
                        resultSet.getInt("currencies_id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sing")));
            }
        } catch (SQLException e){
            throw new MyException("Not connect DB");
        }
        return currencyList;
    }

    public static com.nikron.conversion.model.Currency getCurrency(String code) throws MyException {
        return getCurrencies().stream().filter(x -> x.getCode().equals(code)).findFirst().orElse(null);
    }

    public static List<ExchangeVolute> getExchangeList() throws MyException{
        List<ExchangeVolute> exchangeVoluteList = new ArrayList<>();
        try (Connection connection = connection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT e.id, c.code as base, c2.code as target, rate FROM exchange_rates e\n" +
                    "join currencies c on c.currencies_id = e.base_currency_id\n" +
                    "join currencies c2 on c2.currencies_id = e.target_currency_id");
            while (resultSet.next()){
                exchangeVoluteList.add(new ExchangeVolute(resultSet.getInt("id"),
                       getCurrency(resultSet.getString("base")),
                        getCurrency(resultSet.getString("target")),
                        resultSet.getDouble("rate")));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return exchangeVoluteList;
    }

    public static ExchangeVolute getExchange(String valutes) throws MyException {
        List<String> concatValues = new ArrayList<>();
        List<ExchangeVolute> exchangeVoluteList = getExchangeList();
        for (ExchangeVolute exchangeVolute : exchangeVoluteList){
            concatValues.add(exchangeVolute.getBase().getCode() + exchangeVolute.getTarget().getCode());
        }
        for (int i = 0; i < concatValues.size(); i++) {
            if (valutes.equals(concatValues.get(i))){
                return exchangeVoluteList.get(i);
            }
        }
        return null;
    }

    public static void changeRate(Double rate, ExchangeVolute exchangeVolute) throws MyException {
        try (Connection connection = connection()){
            PreparedStatement pstmt = connection.prepareStatement("UPDATE exchange_rates SET rate=? WHERE id=?");
            pstmt.setDouble(1, rate);
            pstmt.setInt(2, exchangeVolute.getId());
            pstmt.executeUpdate();
        } catch (SQLException e){
            throw new MyException("not update operation");
        }
    }

    public static void addCurrency(Currency currency) throws MyException {
        try (Connection connection = connection()){
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO currencies(code, full_name, sing) values (?, ?, ?)");
            pstmt.setString(1, currency.getCode());
            pstmt.setString(2, currency.getFullName());
            pstmt.setString(3, currency.getSing());
            pstmt.execute();
        } catch (SQLException e){
            throw new MyException("operation insert not execute");
        }
    }
}
