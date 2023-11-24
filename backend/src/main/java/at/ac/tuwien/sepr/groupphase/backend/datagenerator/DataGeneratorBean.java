package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

/**
 * This component is only created, if the profile {@code generateData} is active
 * You can activate this profile by adding {@code -Dspring.profiles.active=generateData} to your maven command line
 */
@Component
@Profile("generateData")
public class DataGeneratorBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final DataSource dataSource;

  /**
   * Executed once when the component is instantiated. Inserts some dummy data.
   *
   * @param dataSource a DataSource object
   */
  public DataGeneratorBean(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Generates Test Date which is defined in resources/sql/insertData.sql
   *
   * @throws SQLException if there is a wrong SQL Statement defined
   */
  @PostConstruct
  public void generateData() throws SQLException {
    LOGGER.info("Generating data…");
    try (var connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/insertData.sql"));
      LOGGER.info("Finished generating data without error.");
    }
  }

  /**
   * Clears Test Date which was created via generateData()
   *
   * @throws SQLException if there is a wrong SQL Statement defined
   */
  public void clearData() throws SQLException {
    LOGGER.info("Clearing data…");
    try (var connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/deleteData.sql"));
      LOGGER.info("Finished clearing data without error");
    }
  }
}
