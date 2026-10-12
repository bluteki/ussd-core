# USSD Core Library

Uma biblioteca Java reutilizável para desenvolvimento de aplicações USSD
(Unstructured Supplementary Service Data). Esta biblioteca fornece uma
base sólida para criar sistemas USSD compatíveis com múltiplos
operadores.

## 📦 Características

-   **Multi-Operador**: Suporte nativo para Movitel, Flares e TruRoute
-   **Gerenciamento de Sessões**: Persistência automática de sessões
    USSD
-   **Log de Transações**: Registro completo de todas as interações
-   **Arquitectura Modular**: Fácil extensão para novos operadores e
    funcionalidades
-   **Spring Boot Ready**: Integração completa com Spring Boot

## 🚀 Operadores Suportados
| Operador | Protocolo | Status |
|----------|-----------|--------|
| **Movitel** | SOAP/XML | ✅ Implementado |
| **Flares** | HTTP/Params | ✅ Implementado |
| **TruRoute** | HTTP/XML | ✅ Implementado |

## 📋 Pré-requisitos

-   Java 17 ou superior
-   Maven 3.6+
-   Spring Boot 3.2.0+
-   Base de dados (H2, MySQL, PostgreSQL, etc.)

## 🧩 Módulos (a partir da 1.1.0)

| Módulo | Para quê | Dependências |
|--------|----------|--------------|
| `ussd-core-protocol` | Ler e escrever os formatos das operadoras (Movitel, TruRoute, Flares) e normalizar códigos USSD | Só o JDK (+ `jakarta.xml.bind-api` para os DTOs antigos) |
| `ussd-core` | Base para aplicações Spring Boot: sessões JPA, `BaseUssdProcessorService`, `OperatorService` | `ussd-core-protocol`, Spring Boot, JPA |

As aplicações existentes continuam a depender só de `ussd-core`: os DTOs (`MovitelUssdRequest`, `TruRouteUssdRequest`, …) mantêm o mesmo pacote e chegam através de `ussd-core-protocol`.

Publicado no JitPack (repositório `https://jitpack.io`). Para quem só precisa dos protocolos (como o USSD Studio):

``` xml
<dependency>
    <groupId>com.github.bluteki.ussd-core</groupId>
    <artifactId>ussd-core-protocol</artifactId>
    <version>1.1.0</version>
</dependency>
```

Aplicações Spring Boot que usam as sessões JPA: `com.github.bluteki.ussd-core:ussd-core:1.1.0`.

Classes em `com.bluteki.ussd.protocol`:

- `MovitelProtocol` — `parse` (aceita `service_code` e `msg`, prefixos por declarar e `<br>` por fechar), `ack`, `menu` (XML válido, texto escapado, quebras de linha como `<br>`), `request`.
- `TruRouteProtocol` — `parseXml`, `fromParams` (GET), `response`, `parseResponse`.
- `FlaresProtocol` — `fromParams`, `response` (`FC`/`FB`), `parseResponse`.
- `ServiceCodes` — `normalize("*612*777#") == "612*777"` e `matches` com sub-códigos.

## 🔧 Instalação

### Instalação 1/2: Adicione isto ao pom.xml do seu projecto:
``` xml
<dependency>
    <groupId>com.bluteki</groupId>
    <artifactId>ussd-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Instalação 2/2: Executar via linha de comando

``` bash
cd seu_project
mvn install
```


## 🏗️ Estrutura do Projeto

``` text
ussd-core/
├── src/main/java/com/bluteki/ussd/core/
│   ├── model/
│   │   ├── UssdSession.java          # Entidade de sessão
│   │   └── UssdTransaction.java      # Entidade de transação
│   ├── repository/
│   │   ├── SessionRepository.java    # Repository de sessões
│   │   └── TransactionRepository.java # Repository de transações
│   ├── service/
│   │   ├── SessionService.java       # Serviço de gestão de sessões
│   │   ├── UssdProcessor.java        # Interface do processador
│   │   ├── BaseUssdProcessorService.java # Classe base abstrata
│   │   └── operator/
│   │       ├── OperatorService.java  # Interface de operador
│   │       ├── MovitelOperatorService.java
│   │       ├── FlaresOperatorService.java
│   │       └── TruRouteOperatorService.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── FlaresUssdRequest.java
│   │   │   ├── MovitelUssdRequest.java
│   │   │   └── TruRouteUssdRequest.java
│   │   ├── response/
│   │   │   ├── FlaresUssdResponse.java
│   │   │   ├── MovitelUssdResponse.java
│   │   │   └── TruRouteUssdResponse.java
│   │   ├── CommonUssdRequest.java    # Request comum
│   │   └── CommonUssdResponse.java   # Response comum
│   └── util/
│      └── HashMapConverter.java     # Conversor para Map
│  
└── pom.xml
```

## 🎯 Como Usar

### 1. Estenda a classe base

``` java
package com.seuprojeto.service.ussd;

import com.bluteki.ussd.core.service.BaseUssdProcessorService;
import com.bluteki.ussd.core.service.SessionService;
import com.bluteki.ussd.core.service.operator.OperatorService;
import com.seuprojeto.service.ussd.menu.MenuHandlerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeuUssdProcessorService extends BaseUssdProcessorService {
    private final MenuHandlerFactory menuHandlerFactory;

    public MeuUssdProcessorService(
            SessionService sessionService,
            List<OperatorService> operatorServices,
            MenuHandlerFactory menuHandlerFactory) {
        super(sessionService, operatorServices);
        this.menuHandlerFactory = menuHandlerFactory;
    }

    @Override
    protected CommonUssdResponse processRequest(CommonUssdRequest request, boolean isInitial) {
        // Implemente a lógica específica do seu projeto
        // Use menuHandlerFactory para gerenciar os menus
    }
}
```

### 2. Crie seus Menu Handlers

``` java
package com.seuprojeto.service.ussd.menu.impl;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;
import com.seuprojeto.service.ussd.menu.MenuHandler;
import org.springframework.stereotype.Component;

@Component
public class MainMenuHandler implements MenuHandler {
    
    @Override
    public CommonUssdResponse handle(CommonUssdRequest request, CommonUssdResponse response) {
        String menuText = "Menu Principal\n\n1. Opcao 1\n2. Opcao 2";
        response.setMessage(menuText);
        response.setNextMenu("MAIN_MENU");
        return response;
    }
}
```

### 3. Configure o MenuHandlerFactory

``` java
package com.seuprojeto.service.ussd.menu;

import com.seuprojeto.service.ussd.menu.impl.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MenuHandlerFactory {
    private final Map<String, MenuHandler> menuHandlers;

    public MenuHandlerFactory(
            MainMenuHandler mainMenuHandler,
            WelcomeMenuHandler welcomeMenuHandler) {
        this.menuHandlers = new HashMap<>();
        this.menuHandlers.put("MAIN_MENU", mainMenuHandler);
        this.menuHandlers.put("WELCOME_MENU", welcomeMenuHandler);
    }

    public MenuHandler getHandler(String menuType) {
        return menuHandlers.get(menuType);
    }
}
```

### 4. Crie os controllers para cada operador

``` java
package com.seuprojeto.controller.ussd;

import com.bluteki.ussd.core.dto.CommonUssdRequest;
import com.bluteki.ussd.core.dto.CommonUssdResponse;
import com.bluteki.ussd.core.dto.request.TruRouteUssdRequest;
import com.bluteki.ussd.core.dto.response.TruRouteUssdResponse;
import com.seuprojeto.service.ussd.UssdProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ussd/meu-projeto/truroute")
public class TruRouteController {
    private final UssdProcessorService ussdProcessor;

    @GetMapping(produces = "application/xml")
    public TruRouteUssdResponse handleGetRequest(
            @RequestParam String msisdn,
            @RequestParam String sessionid,
            @RequestParam String type,
            @RequestParam String msg) {
        // Implementação do endpoint
    }
}
```

## ⚙️ Configuração

Exemplo `application.yml`:

``` yaml
spring:
  application:
    name: meu-projeto-ussd
  datasource:
    url: jdbc:h2:mem:ussddb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

server:
  port: 8080

logging:
  level:
    com.seuprojeto: DEBUG
    com.bluteki.ussd.core: DEBUG
```

## 🔌 Endpoints

**TruRoute (GET)**

``` text
GET /api/ussd/meu-projeto/truroute?msisdn=258841234567&sessionid=12345&type=1&msg=*123#
```

**Movitel (SOAP/XML)**

``` text
POST /api/ussd/meu-projeto/movitel
Content-Type: text/xml
```

**Flares (HTTP Params)**

``` text
GET /api/ussd/meu-projeto/flares?msisdn=258841234567&sessionid=12345&type=1&msg=*123#
```

## 📊 Modelo de Dados

**UssdSession**

-   sessionId: ID único da sessão
-   msisdn: Número do telefone
-   currentMenu: Menu atual
-   operator: Operador (MOVITEL, FLARES, TRUROUTE)
-   isActive: Sessão activa
-   additionalParams: Parâmetros adicionais

**UssdTransaction**

-   input: Input do usuário
-   response: Resposta do sistema
-   timestamp: Data/hora da transação
-   requestType: Tipo de requisição

## 🛠️ Desenvolvimento

Para contribuir:

-   Faça fork do projeto
-   Crie uma branch para sua feature:
    `git checkout -b feature/nova-feature`
-   Commit suas mudanças: `git commit -am 'Adiciona nova feature'`
-   Push para a branch: `git push origin feature/nova-feature`
-   Abra um Pull Request

### Para adicionar novo operador:

-   Implemente `OperatorService`
-   Adicione no `@ComponentScan`
-   Configure no `UssdConfig`

## 📝 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE
para detalhes.

## 🤝 Suporte

Para dúvidas e suporte:

-   Abra uma issue no GitHub
-   Email: support@bluteki.com


Desenvolvido por Bluteki LDA
