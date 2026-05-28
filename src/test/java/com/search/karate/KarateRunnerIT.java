package com.search.karate;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Karate E2E runner – boots the full Spring context on a fixed port, then runs
 * all feature files under src/test/resources/karate.
 * <p>
 * Run with: mvn failsafe:integration-test
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=8090"})
@ActiveProfiles("build")
class KarateRunnerIT {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }
}
