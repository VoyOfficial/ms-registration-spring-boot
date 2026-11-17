# Checks — API access token

## Coverage

| Claim | Proof |
| --- | --- |
| Sem Bearer em `/v1/**` com require on → 401 | `./mvnw test -Dtest=ApiAccessTokenFilterTest` |
| Bearer errado → 401 | mesmo teste |
| Bearer correto → request segue | mesmo teste |
| OpenAPI docs sem token → 200 | mesmo teste + `OpenApiDocsSmokeTest` |
| Require on sem token configurado → 503 | `./mvnw test -Dtest=ApiAccessTokenMisconfiguredTest` |
| Suite existente sem token (require off) | `./mvnw test -Dtest=PlaceControllerTest,OpenApiDocsSmokeTest` |

## Handoff

Após merge: definir `API_ACCESS_TOKEN` no Render; atualizar `voy-app-react-native` para enviar `Authorization: Bearer …`.
