### What?

[O que foi feito - endpoints/UseCases/camadas afetadas]

### Why?

[Por que foi feito - contexto vindo da Issue]

### How?

[Como foi implementado - camadas domain/application/infrastructure afetadas, migrations Flyway se houver]

### Testing?

[Testes unit (domain.service) e integration (@WebMvcTest / @DataJpaTest) adicionados]

- [ ] Se este PR altera regra de negócio em `domain.*`: mutation testing revisado localmente (`./mvnw test-compile org.pitest:pitest-maven:mutationCoverage`, ver `.cursor/rules/validation-pyramid.mdc`)
- [ ] Se este PR altera integração Google Places (adapter, client, nearby, search, list photos): live IT com Google real (sem mock), evidência colada abaixo
  - Client: `RUN_PLACES_LIVE_IT=true ./scripts/run-places-live-it.sh`
  - HTTP smoke (API local com `ENABLE_MOCK_SERVICES=false`): `./scripts/run-places-live-it.sh --with-smoke`
  - Ou Render: `RUN_RENDER_IT=true ./mvnw -Dtest=RenderPlacesSmokeIT test`

### Screenshots (optional)

[Screenshots de Swagger/OpenAPI ou respostas de exemplo, se aplicável]

### Anything Else?

[Observações adicionais]
