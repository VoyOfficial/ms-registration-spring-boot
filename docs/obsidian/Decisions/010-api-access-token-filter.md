---
title: API access token via servlet filter
date: 2026-11-17
status: accepted
---

# ADR 010 — API access token via servlet filter

## Context

Precisamos bloquear consumo anônimo da API pública no Render sem introduzir login de usuário.

## Decision

Usar um `OncePerRequestFilter` com segredo compartilhado (`API_ACCESS_TOKEN`) no header `Authorization: Bearer`, ligado por `REQUIRE_API_TOKEN`. Não adicionar Spring Security nesta etapa.

## Consequences

- Clientes (mobile/BFF) precisam conhecer o token.
- Token embutido no app não é perfeito contra engenharia reversa; ainda assim corta scraping casual da URL pública.
- Health/Swagger/api-docs ficam públicos de propósito.
