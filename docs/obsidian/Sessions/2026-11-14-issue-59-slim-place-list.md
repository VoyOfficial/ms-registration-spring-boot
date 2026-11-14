---
type: session
date: 2026-11-14
workitems: [ISSUE-59]
tags: [session, bugfix, place]
status: closed
parent_workitem: [[ISSUE-59]]
parent_session: [[2026-11-14-issue-59-place-banner-and-search-filter]]
---

# Session: Issue 59 — lista de places sem a galeria

## Objetivo

A lista devolvia o base64 de cada foto em `photos` alem do banner. O payload ficava grande.

## Links

- **Work Item**: [[ISSUE-59]]
- **Sessions Anteriores**: [[2026-11-14-issue-59-place-banner-and-search-filter]]
- **Modulos**: [[Modules/place]]

## Completado

- [x] `GetNearbyPlacesService` e `SearchPlacesService` devolvem so a foto principal
- [x] A galeria continua gravada e segue em `PlaceDetailsResponse`
- [x] `PlaceResponse` de lista omite `photos`
- [x] `GetNearbyPlacesServiceIntegrationTest` cobre local persistido e local do Google com galeria grande

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `ListPlacePhotos.java` | escolhe a foto principal |
| `GetNearbyPlacesService.java` | resultado da lista fica com uma foto |
| `SearchPlacesService.java` | mesmo corte na busca |
| `PlaceResponse.java` | lista nao serializa a galeria |
