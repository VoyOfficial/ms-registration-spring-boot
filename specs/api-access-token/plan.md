# API access token (BFF / client gate)

## Problem

O serviço no Render (`ms-registration`) está público: qualquer cliente que conheça a URL pode chamar `/v1/**` e consumir créditos da Google Places API. Não há camada de autenticação no BFF/API hoje.

## Flow

1. Client (app mobile ou BFF) envia `Authorization: Bearer <API_ACCESS_TOKEN>` em toda chamada a `/v1/**`.
2. `ApiAccessTokenFilter` (servlet filter) intercepta requests sob `/v1/**`.
3. Se `voy.security.require-api-token=true`:
   - Sem header / Bearer inválido / token diferente → **401** + `StandardError`.
   - Token configurado ausente (misconfig) → **503** + `StandardError`.
   - Token válido (comparação constant-time) → segue o pipeline normal.
4. Se `require-api-token=false` (testes / local sem segredo) → filter não bloqueia.
5. Paths públicos permanecem abertos: actuator health, swagger-ui, `v3/api-docs`.

## Impact

**Muda**

- `/v1/**` exige Bearer token quando a flag está ligada (Render).
- OpenAPI documenta o security scheme Bearer.
- Render blueprint / docs pedem `API_ACCESS_TOKEN` + `REQUIRE_API_TOKEN=true`.
- App/BFF precisa enviar o header (handoff mobile).

**Não muda**

- Contratos de body/query dos endpoints.
- Google Places key handling.
- Auth de usuário final (login/JWT de usuário) — fora de escopo; isto é gate de serviço.

## Relations

| Peça | Reuso vs criação |
| --- | --- |
| `StandardError` | **Reuso** |
| `ExceptionHandlerAdvice` | **Não** — filter responde antes do DispatcherServlet |
| Spring Security starter | **Não** — YAGNI; filter basta |
| `OpenApiConfig` | **Editar** — security scheme Bearer |
| `application.yaml` / test yaml / `render.yaml` | **Editar** |
| `ApiAccessTokenFilter` + config | **Criar** (`infrastructure.config.security`) |

## Surface

- Header: `Authorization: Bearer <token>`
- Env: `API_ACCESS_TOKEN`, `REQUIRE_API_TOKEN` (default `false` no yaml; `true` no Render)
- Config: `voy.security.api-access-token`, `voy.security.require-api-token`
- 401 message key: `error.security.api.access.unauthorized`
- 503 message key: `error.security.api.access.misconfigured`

## Landing

- `src/main/java/br/voy/infrastructure/config/security/`
- `src/test/java/br/voy/infrastructure/config/security/`
- `src/main/resources/application.yaml`, `messages.properties`
- `src/test/resources/application.yaml`
- `render.yaml`, `docs/remote-deploy.md`, `README.md`
- `OpenApiConfig.java` (+ smoke assertion opcional)

## Criteria

1. Com `REQUIRE_API_TOKEN=true` e token configurado, request sem Bearer em `/v1/places` → 401.
2. Com Bearer correto → request segue (não 401).
3. Com Bearer errado → 401.
4. Actuator health / swagger / api-docs não exigem token.
5. Suite de testes existente permanece verde com `require-api-token=false` no yaml de teste.
6. Render blueprint declara `API_ACCESS_TOKEN` e `REQUIRE_API_TOKEN=true`.
