# SERVER MOCK | PROJETO EVENT CALENDAR

Este é um mock para requisitar api simulando
a api de feriados do https://brasilapi.com.br/

## Requirements

- Ter o node instalado na máquina.

## Scripts para rodar

### Iniciar o projeto

Para iniciar o projeto, rode o comando abaixo:

```bash
$ npm install
```

### Script de alimentação

O scrtipt de alimentação faz um loop entre os anos 1900 a 2199 realizando um fetch na api do BrasiAPI (https://brasilapi.com.br/api/feriados/v1/{ano}) e ao final alimenta o db.json que é o arquivo mock que simula a api.

Este script funciona apenas fora da rede da CAIXA.

Para rodar o script execute o comando abaixo na raiz do projeto:

```bash
$ node init
```

### Script do Server

Este script serve para simular uma api mock externa localmente.

É acessada pelo endereço http://localhost:3000

Para rodar o script execute o comando abaixo na raiz do projeto:

```bash
$ node init
```

As rotas disponíveis seguem o padrão abaixo, onde **{ano}** se substitui pelo ano desejado entre 1900 a 2199:

```browser
http://localhost:3000/api/feriados/v1/{ano}
```

----