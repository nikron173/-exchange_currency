package com.nikron.conversion.service;

import com.nikron.conversion.exception.DataBaseException;
import org.sqlite.SQLiteConfig;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DataBaseServiceImpl implements DataBaseService {
    @Override
    public Connection getDataBaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new DataBaseException("Драйвер не найден");
        }
            SQLiteConfig config = new SQLiteConfig();
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.enforceForeignKeys(true);
        try {
            Connection connection = DriverManager.getConnection(getConnectionUrl(), config.toProperties());
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e){
            throw new DataBaseException("База данных не доступна");
        }
    }

    private String getConnectionUrl() {
        URL url = getClass().getClassLoader().getResource("db/test.db");
        if (Objects.nonNull(url)) {
            try {
                return String.format("jdbc:sqlite:" + url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Ошибка синтаксиса URI");
            }
        }
        throw new RuntimeException("Строка подключения URI содержит null.");
    }
}
