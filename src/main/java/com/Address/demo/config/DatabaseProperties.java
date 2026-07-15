package com.Address.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "db")
@Data
public class DatabaseProperties {

    private Map<String, String> databases;
    private String defaultDb;

    public Map<String, String> getDatabases() {
        return databases;
    }

    public void setDatabases(Map<String, String> databases) {
        this.databases = databases;
    }

    public String getDefaultDb() {
        return defaultDb;
    }

    public void setDefaultDb(String defaultDb) {
        this.defaultDb = defaultDb;
    }

    // Get actual DB name from logical name
    public String getActualDbName() {
        if (defaultDb == null || databases == null) return null;
        return databases.get(defaultDb);
    }
}

