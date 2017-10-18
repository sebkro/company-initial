package io.rscale.training.company;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class DataSourceConfig extends AbstractCloudConfig {

    private static final Logger logger = Logger.getLogger(DataSourceConfig.class);

    public DataSourceConfig() {
        logger.info(this.getClass() + " loaded");
    }

    @Bean
    public DataSource dataSource() throws Exception {
        DataSource dataSource = cloud().getSingletonServiceConnector(DataSource.class, null);
        if ( !isMySQL(dataSource)) {
            logger.error("MySQL required when running cloud profile.");
            throw new UnsatisfiedDependencyException("javax.sql.DataSource", "javax.sql.DataSource", "javax.sql.DataSource", "MySQL required when running cloud profile.");
        }
        return dataSource;
    }
    
    private boolean isMySQL(DataSource dataSource) {
    	String dbName = null;
    	Connection connection = null;
    	try {
			connection = dataSource.getConnection();
			dbName = connection.getMetaData().getDatabaseProductName();
			logger.info("DB Name is: " + dbName);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} 
    	return "MySQL".equals(dbName);
    }

}
