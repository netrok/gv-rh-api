# GV RH API (Spring Boot 3.5 · Java 21 · PostgreSQL · JWT)

API de Recursos Humanos para **Granvia**. Incluye autenticación JWT (HS256), Swagger/OpenAPI, JPA/Hibernate, Flyway y mejores prácticas de seguridad (stateless, CORS).

## 🚀 Stack
- **Java 21**, **Spring Boot 3.5.7**
- **Spring Security** (OAuth2 Resource Server · JWT)
- **PostgreSQL** + **Spring Data JPA** + **Hibernate**
- **Flyway** (migraciones)
- **springdoc-openapi** (Swagger UI)
- **Jackson JSR-310** (fechas)
- Build: **Maven**

---

## 📦 Requisitos
- Java 21
- Maven 3.9+
- PostgreSQL 14+ (DB: `gv_rh`)
- Variables/archivo `application.yml` configurado

---

## ⚙️ Configuración (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/gv_rh
    username: postgres
    password: riesenhammer
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
    properties:
      "[hibernate.format_sql]": true
      "[hibernate.jdbc.time_zone]": UTC
  flyway:
    enabled: true
    locations: classpath:db/migration
  jackson:
    serialization:
      write-dates-as-timestamps: false
  web:
    resources:
      add-mappings: false

app:
  jwt:
    # Clave HS256 en Base64 (>= 32 bytes). Cambiar en producción.
    secret: VGhyZWUtZG9ncy1ndWFyZC10aGUgZ28tYm94LXN1cGVyLXN1cGVyLXNlY3JldC0zMgo=
    issuer: gv-rh-api
    access-minutes: 60
    refresh-minutes: 43200

springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui/index.html
    display-request-duration: true
    persist-authorization: true

# 1) Compilar
mvn clean package -DskipTests

# 2) Ejecutar (perfil default)
mvn spring-boot:run
# o
java -jar target/gv-rh-api-0.0.1-SNAPSHOT.jar

{
  "username": "admin",
  "password": "Admin123*"
}
