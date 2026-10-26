---
type: workitem
id: ISSUE-50
status: in-progress
date: 2026-10-26
tags: [task, deploy, mobile, recommendations]
---

# ISSUE-50 - Deploy remoto para integração com o app mobile

Source: https://github.com/VoyOfficial/ms-registration-spring-boot/issues/50

## Sessions

- [[2026-10-26-issue-50-remote-deploy-recommendation-seed]]
- [[2026-10-26-issue-50-remote-instance]]

## Escopo

- Publicar instância remota do `ms-registration` para o app mobile validar integração
- Seed Flyway de lugares recomendados (Gramado/Canela) para o endpoint de recommendations não voltar vazio
- Smoke de recommendations + OpenAPI no ambiente remoto

## Notes

- Seed (V21) shipped in PR #51. This session publishes the reachable instance + deploy docs.
- Tunnel URL is ephemeral; durable path is Render blueprint (`render.yaml`).
- Issue #49 was an accidental create/probe (`test` body); token cannot edit/close issues — close #49 manually
- Org Project board sync unavailable for this token (`projectsV2` empty / no `project` scope)
