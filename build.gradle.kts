plugins {
    java
    id("org.springframework.boot") version "3.1.7"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.clover"
version = "0.0.1-SNAPSHOT"
val queryDslVersion = "5.0.0"


java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // mysql
    runtimeOnly("com.mysql:mysql-connector-j")

    //redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // query-dsl
    implementation("com.querydsl:querydsl-jpa:${queryDslVersion}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // jwt
    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // jwt test
    testCompileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    testRuntimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    testRuntimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // h2
    compileOnly("com.h2database:h2:2.2.220")
    testImplementation("com.h2database:h2:2.2.220")

    // aws
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

    // email
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // rabbit
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // reactor-netty * 필수 외부 STOMP 사용*
    implementation("org.springframework.boot:spring-boot-starter-reactor-netty")

    // rabbit jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.1")

    //jackson2json LocalDateTime handling
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}
