---
type: session
date: 2026-11-14
tags: [session, bugfix, place]
status: closed
---

# Session: lista nearby e busca lenta

## Objetivo

`GET /v1/places` em Gramado respondia 500 quando ja havia locais no banco. A busca de pizza levava cerca de 2 minutos e devolvia fotos de ate 2MB.

## Completado

- [x] A lista escolhe a foto principal sem a subquery que falhava ao carregar a pagina
- [x] Fotos grandes demais ficam de fora da lista
- [x] Nearby e text search baixam a foto em no maximo 400px
- [x] `RenderPlacesSmokeIT` cobre o servico no Render quando `RUN_RENDER_IT=true`

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `RelationalPlaceRepository.java` | uma foto pequena por local |
| `PlacePhotoJpaRepository.java` | consulta sem subquery |
| `PlacesApiClient.java` | teto de 400px na lista |
| `RenderPlacesSmokeIT.java` | checagem ao vivo do Render |
