package com.example.wellness;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * spring.datasource.*를 H2 인메모리 DB로 덮어써서, 로컬/CI에 실제 MySQL이 없어도
 * 전체 스프링 컨텍스트(JPA 포함)가 뜨는지 확인할 수 있게 한다.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class WellnessChatBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
