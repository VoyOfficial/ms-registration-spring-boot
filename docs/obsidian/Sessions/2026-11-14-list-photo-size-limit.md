---
type: session
date: 2026-11-14
tags: [session, bugfix, place]
status: closed
parent_session: [[2026-11-14-nearby-list-and-search-speed]]
---

# Session: teto unico para a foto da lista

## Contexto

A revisao do PR #63 apontou que o filtro SQL media bytes e o filtro Java media caracteres base64, os dois com o numero 150000. A lista tambem lia a foto como `Object[]`.

## O que foi feito

- A consulta devolve `PlacePhotoRef`.
- O teto de bytes armazenados continua 150000.
- O teto da string base64 passou a ser 200000, o tamanho maximo que 150000 bytes produzem.
- O teste de nearby cobre a foto no teto e a foto um byte acima, que sai da resposta sem 500.

## Decisoes tecnicas

- Nao baixar o teto SQL para 112500. Isso esconderia banners legados que ja estao em base64 e cabem em 150KB.
- O segundo check permanece como rede de seguranca se a consulta deixar passar um blob grande.

## Arquivos modificados

- `src/main/java/br/voy/infrastructure/repository/jpa/PlacePhotoRef.java`
- `src/main/java/br/voy/infrastructure/repository/jpa/PlacePhotoJpaRepository.java`
- `src/main/java/br/voy/infrastructure/repository/relational/RelationalPlaceRepository.java`
- `src/test/java/br/voy/application/controller/PlaceNearbyPersistenceIntegrationTest.java`

## Proximos passos

- Nenhum. O comportamento fica coberto no `PlaceNearbyPersistenceIntegrationTest`.
