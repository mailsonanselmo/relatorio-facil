# Relatório Fácil (Spring Boot 3.5.5, Java 17)

# Autor: Mailson Aguiar Anselmo - mailson.anselmo@gmail.com

Aplicação que lê arquivos `.txt` contendo JSON com **titulo** e **sql**, executa(apenas select) no PostgreSQL,
gera um Excel (colunas *Título* e *Resultado*), salva no diretório configurado e envia por e-mail.
Agendada para rodar **todo dia 1 e 16 às 06:00 (America/Fortaleza)**.

## Entrada (scripts)
Coloque seus arquivos `.txt` em `./scripts` (ou diretório configurado). Formatos aceitos:
1. **Array JSON** único por arquivo:
```json
[
  {"titulo": "Total de usuários", "sql": "SELECT COUNT(*) AS total FROM usuarios"},
  {"titulo": "Último login", "sql": "SELECT MAX(last_login) FROM usuarios"}
]
```
2. **NDJSON** (um JSON por linha):
```json
{"titulo":"Total de pedidos","sql":"SELECT COUNT(*) FROM pedidos"}
{"titulo":"Valor médio","sql":"SELECT AVG(valor) FROM pedidos"}
```

> **Dica:** As consultas devem ser `SELECT`. O serviço pegará **apenas a primeira linha**;
> se houver múltiplas colunas, a linha é serializada em JSON.

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
