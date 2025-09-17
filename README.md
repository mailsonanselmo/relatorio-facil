# 📊 Relatório Fácil  

Aplicação que lê arquivos `.txt` contendo JSON com **titulo** e **sql**, executa (**apenas SELECT**) no PostgreSQL,  
gera um Excel (colunas *Título* e *Resultado*), salva no diretório configurado e envia por e-mail.  

🔔 Agendada para rodar **todo dia 1 e 16 às 06:00 (America/Fortaleza)**.

![Java](https://img.shields.io/badge/Java-17-red)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/Postgres-9.4%2B-blue)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 👨‍💻 Autor
**Mailson Aguiar Anselmo**  
📧 [mailson.anselmo@gmail.com](mailto:mailson.anselmo@gmail.com)

---

## 📚 Sumário
- [🔎 Visão Geral](#-visão-geral)
- [🛠 Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [📂 Entrada (scripts)](#-entrada-scripts)
- [▶️ Execução manual](#️-execução-manual)
- [⚙️ Configuração](#️-configuração)
- [🐳 Docker](#-docker)
- [🔒 Segurança](#-segurança)

---

## 🔎 Visão Geral
O **Relatório Fácil** permite automatizar consultas SQL em bancos **PostgreSQL**, gerar relatórios em **Excel** e enviá-los por e-mail, sem necessidade de intervenção manual.  

Ideal para rotinas quinzenais ou mensais de **BI, auditoria ou acompanhamento de métricas**.

---

## 🛠 Tecnologias Utilizadas
- ☕ **Java 17**
- 🌱 **Spring Boot 3.5.5**
- 📦 **Maven**
- 🐘 **PostgreSQL**
- 📑 **Apache POI** (geração de arquivos Excel)
- 📧 **Spring Mail** (envio de e-mails)
- 🐳 **Docker** (containerização)

---

## 📂 Entrada (scripts)
Coloque seus arquivos `.txt` em `./scripts` (ou diretório configurado).  
Formatos aceitos:

### 1️⃣ Array JSON (único por arquivo)
```json
[
  {"titulo": "Total de usuários", "sql": "SELECT COUNT(*) AS total FROM usuarios"},
  {"titulo": "Último login", "sql": "SELECT MAX(last_login) FROM usuarios"}
]
```

## Execução manual
`POST http://localhost:8080/api/relatorios/gerar` gera e envia imediatamente. Lembre do lapiço de datas pois está configurado para extrair dados quinzenais.

## Configuração
Edite `src/main/resources/application.yml`:
- `spring.datasource.*`: conexão PostgreSQL
- `spring.mail.*`: SMTP para envio
- `relatorio.scripts-dir`: diretório dos `.txt`
- `relatorio.output-dir`: onde salvar os Excel

## Docker
### Dockerfile
- Multi-stage (build + runtime)
- Charset UTF-8 por padrão

### docker-compose.yml (Windows)
Mapeie `C:\relatorios` (host) para `/app/output` (container) para salvar no **C:** se for windows.

## Segurança
- Apenas `SELECT` é permitido.
- Sem parâmetros dinâmicos; se precisar, use `views` ou `functions` protegidas.
