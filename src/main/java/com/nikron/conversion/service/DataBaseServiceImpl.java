package com.nikron.conversion.service;

import com.nikron.conversion.exception.DataBaseException;
import jakarta.servlet.http.HttpServletResponse;
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
            throw new DataBaseException("Драйвер не найден", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
            SQLiteConfig config = new SQLiteConfig();
            config.setEncoding(SQLiteConfig.Encoding.UTF8);
            config.enforceForeignKeys(true);
        try {
            Connection connection = DriverManager.getConnection(getConnectionUrl(), config.toProperties());
            connection.setAutoCommit(true);
            return connection;
        } catch (SQLException e){
            throw new DataBaseException("База данных не доступна", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String getConnectionUrl() {
        URL url = getClass().getClassLoader().getResource("db/test.db");
        if (Objects.nonNull(url)) {
            try {
                return String.format("jdbc:sqlite:" + url.toURI());
            } catch (URISyntaxException e) {
                throw new DataBaseException("Ошибка синтаксиса строки подключения к БД",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        throw new DataBaseException("Строка подключения URI содержит null.",
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
