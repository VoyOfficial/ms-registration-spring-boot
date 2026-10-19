---
type: session
date: 2026-10-19
workitems: [ISSUE-46]
tags: [session, openapi, swagger]
status: closed
parent_workitem: [[ISSUE-46]]
parent_session: none
---

# Session: Issue #46 OpenAPI / Swagger docs for mobile

## Work Item

- Primary: [[ISSUE-46]]
- Related: VoyOfficial/voy-app-react-native#80 (Swagger quality validator)

## Contexto

O grupo `v1` do springdoc apontava para `by.voy...` (typo). DTOs de Place sem `@Schema`. `ApiDocumentationValidator` no domain com pacote placeholder `com.seuprojeto.dtos`. Issue #80 pedia o mesmo gate com documentação mais rica.

## O que foi feito

- `OpenApiConfig`: package scan `br.voy.application.controller`, `pathsToMatch(/v1/**)`, Info atualizado
- `@Schema` em request/response DTOs + `StandardError` / `Interval` / `PlacePhoto`
- `PlaceController` recommendations: schema real `NearbyPlacesResponse`; erros `DefaultResponse` (alinhado ao `ExceptionHandlerAdvice`)
- `UserController` GET: response 404 documentado
- `ApiDocumentationValidator` movido para `infrastructure.config.openapi` e endurecido (DTO class/fields, Operation summary, ApiResponses content, Parameter descriptions)
- Removida exclusão ArchUnit do validator no domain
- Testes: `ApiDocumentationValidatorTest`, `OpenApiDocsSmokeTest`

## Decisoes tecnicas

- Manter o validator como gate de startup (BeanFactoryPostProcessor), alinhado à #80, em vez de só smoke test
- Issue #80 do app mobile cabe neste PR: o validator vive neste backend

## Arquivos principais

- `src/main/java/br/voy/infrastructure/config/openapi/OpenApiConfig.java`
- `src/main/java/br/voy/infrastructure/config/openapi/ApiDocumentationValidator.java`
- `src/main/java/br/voy/application/controller/*`
- `src/main/java/br/voy/application/controller/request/*`
- `src/main/java/br/voy/application/controller/response/*`
- `src/test/java/br/voy/infrastructure/config/openapi/*`

## Review fix pass (`/code-reviewer`)

- Restored endpoint-accurate Places OpenAPI i18n keys (nearby vs details)
- Removed OpenAPI `@Schema` from domain `Interval`/`PlacePhoto`; added `IntervalResponse`/`PlacePhotoResponse`
- Deleted single-caller `controller.openapi` meta-annotations; restored idiomatic mappings
- Validator uses direct swagger imports again; negative unit tests + stronger smoke assertions
- Recommendations 400/404/500 OpenAPI schemas switched to `DefaultResponse` (runtime envelope)
- Smoke asserts `DefaultResponse` on recommendations errors; `PlaceDetailsResponseTest` covers photo API-key strip
- PR: https://github.com/VoyOfficial/ms-registration-spring-boot/pull/47

## Proximos passos

- [x] Abrir PR contra `develop` com `Closes #46`
- [x] Referenciar voy-app-react-native#80 no PR
- [x] Fix High: recommendations error schema matches runtime `DefaultResponse`
