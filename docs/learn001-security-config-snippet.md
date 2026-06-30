# SecurityConfig change

Adicionar `/api/v1/learning/**` no matcher autenticado:

```java
.requestMatchers(
        "/api/v1/auth/me",
        "/api/v1/parents/**",
        "/api/v1/learning/**",
        "/api/v1/dev/**"
).authenticated()
```
