package db;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.jdbc.JdbcDsl.jdbcFeeder;
import static ru.tinkoff.load.javaapi.JdbcDsl.DB;
import static ru.tinkoff.load.javaapi.JdbcDsl.jdbc;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.Simulation;

import ru.tinkoff.load.javaapi.check.simpleCheckType;
import static ru.tinkoff.load.javaapi.JdbcDsl.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import java.util.Map;
import ru.tinkoff.load.javaapi.actions.QueryActionBuilder;
import ru.tinkoff.load.javaapi.protocol.JdbcProtocolBuilder;
import scala.Predef;

public class UUIDvsStringIndexSimulation extends Simulation {

  private final String dbUrl = "jdbc:postgresql://localhost:5432/adi";
  private final String dbUsername = "postgres";
  private final String dbPassword = "";

  private final FeederBuilder<Object> stringEntityJdbcFeeder =
      jdbcFeeder(dbUrl, dbUsername, dbPassword, "SELECT id FROM string_entity")
          //.transform((key, value) -> UUID.fromString((String)value))
          .random();

  private final FeederBuilder<Object> uuidEntityJdbcFeeder =
      jdbcFeeder(dbUrl, dbUsername, dbPassword, "SELECT id FROM uuid_entity")
          .random();

  public static JdbcProtocolBuilder dataBase =
      DB().url("jdbc:postgresql://localhost:5432/adi")
          .username("postgres")
          .password("")
          .maximumPoolSize(200)
          .protocolBuilder();

  public static QueryActionBuilder selectStringTest() {

    return jdbc("SELECT STRING TEST")
        .queryP("SELECT name FROM string_entity WHERE id = {id}")
        .params(Map.of("id", "#{id}"))
        .check(simpleCheck(simpleCheckType.NonEmpty));
  }

  public static QueryActionBuilder selectUUIDTest() {

    return jdbc("SELECT UUID TEST")
        .queryP("SELECT name FROM uuid_entity WHERE id = uuid({id})")
        .params(Map.of("id", "#{id}"))
        .check(simpleCheck(simpleCheckType.NonEmpty));
  }

  public ScenarioBuilder stringScn = scenario("String JDBC scenario")
      .feed(stringEntityJdbcFeeder)
      .exec(selectStringTest());

  public ScenarioBuilder uuidScn = scenario("UUID JDBC scenario")
      .feed(uuidEntityJdbcFeeder)
      .exec(selectUUIDTest());

  {
    setUp(
        stringScn.injectOpen(rampUsers(2000).during(60)),
        uuidScn.injectOpen(rampUsers(2000).during(60))
    ).protocols(dataBase);
    //setUp(scn.injectOpen(rampUsers(1000).during(60))).protocols(dataBase);
  }
}
