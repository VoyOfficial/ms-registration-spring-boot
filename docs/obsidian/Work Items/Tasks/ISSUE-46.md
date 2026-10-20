---
type: workitem
id: ISSUE-46
status: done
date: 2026-10-19
tags: [task, openapi, swagger, mobile]
---

# ISSUE-46 - Completar documentação Swagger/OpenAPI (integração mobile)

Source: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/46

Related: [voy-app-react-native#80](https://github.com/VoyOfficial/voy-app-react-native/issues/80) — aprimorar `ApiDocumentationValidator` (mesmo gate de qualidade OpenAPI).

## Sessions

- [[2026-10-19-issue-46-openapi-swagger-docs]]

## Escopo

- Corrigir scan OpenAPI (`br.voy.application.controller`)
- Completar `@Schema` / `@Operation` / `@ApiResponses` para contrato mobile
- Mover e endurecer `ApiDocumentationValidator`
- Smoke test de `/v3/api-docs/v1`

## Delivery

- PR: https://github.com/VoyOfficial/ms-registration-spring-boot/pull/47 (merged)
- GitHub issue #46 still needs manual close (agent token lacks `issues:write`)
- Related voy-app-react-native#80 delivered in this backend; close manually if RN track agrees
