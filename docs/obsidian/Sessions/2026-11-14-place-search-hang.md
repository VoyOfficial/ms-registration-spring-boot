---
type: session
date: 2026-11-14
tags: [session, bugfix, place]
status: closed
---

# Session: busca de places presa no download das fotos

## Objetivo

`POST /v1/places/search` nao respondia. O Text Search baixava todas as fotos de cada resultado, e o download HTTP nao tinha timeout.

## Completado

- [x] Lista (nearby e text search) baixa so a primeira foto
- [x] Download de foto e cliente Google tem timeout

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `GooglePlacesAdapter.java` | uma foto por lugar na lista |
| `PlacesApiClient.java` | timeout de rede |
