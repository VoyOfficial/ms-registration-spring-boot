# Current work

Canonical snapshot for agents and humans. Commit this file with session notes.

## Active

- API access token em `/v1/**` (`REQUIRE_API_TOKEN` + `API_ACCESS_TOKEN`). Session: [[2026-11-17-api-access-token]]. PR em aberto.

## Blocked

- none

## Recently Completed

- Search by name stays inside 5 km of the given coordinates, including `Mais avaliados`. Session: [[2026-11-15-place-search-radius]].
- Live Google IT + DoD Places (PR #63): gate com Google real e teto de foto tambem no path de search. Session: [[2026-11-14-places-live-it-dod]].
- Foto da lista usa um teto so: 150KB armazenados, 200 mil caracteres em base64. Session: [[2026-11-14-list-photo-size-limit]].
- Lista nearby em Gramado quebrava e a busca de pizza levava cerca de 2 minutos. Session: [[2026-11-14-nearby-list-and-search-speed]].
- Busca de places deixava o POST preso baixando a galeria inteira. Session: [[2026-11-14-place-search-hang]].
- [[ISSUE-59]] — Banner base64 no carrossel e lista sem a galeria. Sessions: [[2026-11-14-issue-59-place-banner-and-search-filter]], [[2026-11-14-issue-59-slim-place-list]].
- [[ISSUE-54]] — Busca de locais. PR #56 merged into `develop`. Optional `/speckit.verify` in a non-author session and closing GitHub issue #54 remain outside this work.
- [[ISSUE-50]] — Deploy remoto para integração mobile (tunnel + Render blueprint). Session: [[2026-10-26-issue-50-remote-instance]]. GitHub issue closed 2026-10-27.
- [[ISSUE-46]] — OpenAPI/Swagger for mobile + ApiDocumentationValidator (voy-app-react-native#80). PR #47 merged. Session: [[2026-10-19-issue-46-openapi-swagger-docs]]
- Workflow lean no harness (contexto always-on -67%, Spec Lean, gates jscpd/DoD, Reuse-First). Session: [[2026-10-19-workflow-lean-harness]]
- [[ISSUE-43]] — Sponsor table eligibility (`registration.sponsor`), PR #44. Session: [[2026-10-19-issue-43-sponsor-eligibility-table]]
- Merged PR #42 — sponsored place registry (issue #27)
- Merged PR #39 (`create-get-place-from-db` → `develop`) — nearby from DB, recommendations, review fixes
