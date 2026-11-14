---
type: session
date: 2026-11-14
workitems: [ISSUE-59]
tags: [session, bugfix, place, search]
status: closed
parent_workitem: [[ISSUE-59]]
parent_session: [[2026-11-09-issue-54-place-search-verify-fixes]]
---

# Session: Issue 59 — banner do carrossel e filtro da busca

## Objetivo

Corrigir `photo` nulo no carrossel de places e o body de `POST /v1/places/search` que chegava vazio no service.

## Links

- **Work Item**: [[ISSUE-59]]
- **Sessions Anteriores**: [[2026-11-09-issue-54-place-search-verify-fixes]]
- **Modulos**: [[Modules/place]]
- **Decisoes**: [[Decisions/009-search-reuses-place-response]]

## Completado

- [x] `PlaceResponse.photo` usa o base64 da foto principal (URL com `key=` continua omitida)
- [x] Listas carregam so a foto principal, sem a galeria
- [x] `PlacePhotoModel.toDomain` reencoda bytes; linhas antigas que gravaram o texto base64 continuam legiveis
- [x] `SearchPlacesRequest` recebe `types` e `ordination` do JSON
- [x] Testes de place persistido, place do Google, e desserializacao do body no controller

## Decisoes

- Banner fica em `photo` como base64 cru. O contrato de URL nao muda.
- Foto principal basta na lista. A galeria inteira nao e hidratada.
- Bytes novos sao a imagem decodificada. Leitura aceita tambem o formato antigo (`base64.getBytes()`), que e o que `savePlace` gravou ate aqui.

## Arquivos Modificados

| Arquivo | Mudanca |
| --- | --- |
| `PlaceResponse.java` | `photo` a partir do base64 principal |
| `SearchPlacesRequest.java` | setter para o Jackson preencher o body |
| `PlacePhotoModel.java` | bytes da imagem voltam como base64 |
| `RelationalPlaceRepository.java` | lista anexa so a foto principal; save decodifica o base64 |
| `PlacePhotoJpaRepository.java` | consultas da foto principal e da primeira foto |
| `PlaceModel.java` | `toListDomain` aceita a foto principal |

## Proximos Passos

- Nenhum no codigo. Revisao do PR contra `develop`.
