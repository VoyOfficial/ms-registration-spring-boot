# 👨🏽‍💻 Voy Registration 👤

Este projeto trata-se de uma API Rest para realizar o cadastro de Usuários.

[![GitHub stars](https://img.shields.io/github/stars/VoyOfficial/ms-registration-spring-boot?color=7159)](https://github.com/VoyOfficial/ms-registration-spring-boot/stargazers)
![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven/3.8.1?color=7159)

## 🤔 O que é este projeto?

Ele é uma aplicação responsável pelo domínio de Usuários do Projeto Voy, logo, essa API disponibiliza uma série de
endpoints
para realizar o CRUD de Usuário.

## � Diretrizes de Desenvolvimento

Este projeto segue rigorosas práticas de engenharia de software:

- **🏛️ Clean Architecture**: Arquitetura em camadas (Domain, Application, Infrastructure)
- **✨ Clean Code**: Princípios de código limpo por Uncle Bob
- **🔄 TDD**: Test-Driven Development por Kent Beck
- **💎 Atomic Commits**: Commits atômicos por Martin Fowler

**📖 Documentação completa**: [.github/DEVELOPMENT_GUIDELINES.md](.github/DEVELOPMENT_GUIDELINES.md)

**🤖 Instruções GitHub Copilot**: [.github/copilot-instructions.md](.github/copilot-instructions.md)

### Quick Start para Desenvolvedores

Antes de começar a desenvolver, leia:
1. [Estrutura da Clean Architecture](.github/docs/ARCHITECTURE.md)
2. [Princípios Clean Code](.github/docs/CODE_CONVENTIONS.md)
3. [Guia TDD](.github/docs/TDD.md)
4. [Padrão de Commits](.github/docs/COMMITS.md)

## �🚀 Executando a aplicação 👾

### 📍 Local

Se você quiser fazer alguma modificação no código precisará ter instalado em sua máquina as seguintes ferramentas:

- [Java JDK 11+](https://www.oracle.com/br/java/technologies/javase-jdk11-downloads.html)
- [Maven 3.8.1](https://maven.apache.org/download.cgi)

### 🐳 Docker

Este projeto conta com um **Dockerfile**, com as instruções para realizar o build da aplicação e subir o banco de dados,
ao realizar a subida de dados, um banco chamado **voy** será criado, os schemas serão criadas automaticamente utilizando os 
scripts localizados no caminho /src/main/resources/db.

Os requisitos para isso são:

- [Docker](https://www.docker.com/products/docker-desktop) - Baixe de acordo com o seu SO
- [Docker-compose](https://docs.docker.com/compose/install/) - Se estiver utilizando Windows, o Docker desktop já possui
  o docker-compose instalado

## 🎲 Executando a API com o docker-compose

Com esse repositório já clonado em sua máquina e com todos os pré-requisitos atendidos.

1. Você deve ir até a raiz do projeto onde o arquivo **docker-compose.yml** está.
2. Deve abrir um terminal na raiz do projeto.
3. Agora certifique-se que o seu Docker já está em execução.
4. Execute o seguinte comando no terminal:

```bash
docker-compose up -d
```

5. Com isso sua aplicação já está em execução por padrão na porta local 8080

## 🔧 Variáveis de Ambiente da Aplicação

| ENV_VARS             | Descrição                                                                                          |
|----------------------|----------------------------------------------------------------------------------------------------|
| API_PORT             | Porta que a aplicação utilizará em sua execução. (Default: 8080)                                   |
| ENABLE_MOCK_SERVICES | Flag para habilitar os mocks de integração com serviços externos (google places). (Default: false) |
| PLACES_API_KEY       | Chave de API do Google Places.                                                                     |
| DATABASE_HOST        | Host do banco de dados utilizado. (Default: localhost ; Postgres)                                  |
| DATABASE_PORT        | Porta do banco de dados utilizado. (Default: 5432 ; Postgres)                                      |
| DATABASE_DB          | Banco de dados utilizado dentro. (Default: voy)                                                    |
| DATABASE_USER        | Usuário do banco de dados. (Default: postgres)                                                     |
| DATABASE_PASSWORD    | Senha do banco de dados. (Default: password)                                                       |
| DATABASE_SCHEMA      | Esquema do banco de dados. (Default: registration)                                                 |

## 📝Fazendo requisições - Insomnia

Essa aplicação tem um workspace com todas as requisições disponíveis configurado no aplicativo **Insomnia**, clicando no
botão abaixo você pode
baixar o workspace de requests utilizados nesse projeto.

[![Run in Insomnia}](https://insomnia.rest/images/run.svg)](https://insomnia.rest/run/?label=Voy&uri=https%3A%2F%2Fgist.githubusercontent.com%2Fmatheuscarv69%2F2acaa18a9e235c0e6ae21c49985aa138%2Fraw%2Fdb1a225839db36c04d827d8e550cd1f523932f2d%2FCollection)

Além disso a mesma foi documentada usando o Swagger, por meio dele você também pode ter acesso as requisições e aos
modelos de dados recebidos e enviados pela aplicação.
Com a aplicação sendo executada, você pode acessar a página do Swagger por meio da url abaixo.

```bash
http://localhost:8080/api/registration/swagger-ui/index.html#/
```

## 📝Spring Actuator

Esse projeto conta com o Spring Actuator implementado, isso possibilita verificar informações dele durante sua execução,
tais como saúde, env vars e muito mais.

Essas informações podem ser acessadas por meio do seguinte endpoint:

```
http://localhost:2022/actuator/portal-admin
```

## 🚀 Tecnologias 👩‍🚀

As seguintes tecnologias foram utilizadas no desenvolvimento do projeto.

- Java 11
- Spring Boot 2.7.5
    - Web
    - Validation
    - Devtools
    - Data JPA
    - Postgres
    - Profiles
- Hibernate-types-52
- H2-Database
- Lombok
- OpenAPI

- [Spring Boot Admin](https://codecentric.github.io/spring-boot-admin/current/)

## 👨🏻‍💻 Autor

<br>
<a href="https://github.com/matheuscarv69">
 <img style="border-radius: 35%;" src="https://avatars1.githubusercontent.com/u/55814214?s=460&u=ffb1e928527a55f53df6e0d323c2fd7ba92fe0c3&v=4" width="100px;" alt=""/>
 <br />
 <sub><b>Matheus Carvalho</b></sub></a> <a href="https://github.com/matheuscarv69" title="Matheus Carvalho">🚀</a>

Feito por Matheus Carvalho, entre em contato!✌🏻

 <p align="left">
    <a href="mailto:matheus9126@gmail.com" alt="Gmail" target="_blank">
      <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white&link=mailto:matheus9126@gmail.com"/></a>
    <a href="https://www.linkedin.com/in/matheus-carvalho69/" alt="Linkedin" target="_blank">
        <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white&link=https://www.linkedin.com/in/matheus-carvalho69/"/></a>  
    <a href="https://www.instagram.com/_mmcarvalho/" alt="Instagram" target="_blank">
      <img src="https://img.shields.io/badge/Instagram-E4405F?style=for-the-badge&logo=instagram&logoColor=white&link=https://www.instagram.com/_mmcarvalho/"/></a>  
  </p>
