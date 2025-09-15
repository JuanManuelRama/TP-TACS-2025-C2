# Frontend

Esta carpeta se corresponde al front del proyecto. Está desarollada en React.js, utilizando TypesScript y vite como build tool.

## Build & Run

### Dev mode

``` bash
npm run dev
```

### Prod (using serve)

Genera la carpeta /dist para ejecutar el proyecto. Contamos con una dependencia a serve para facilitar correrlo

``` bash
npm run build
serve dist
```

## Estilo

En vez de utilizar CSS para darle estilo, fuimos con Tailwind para poder centralizar todo en los .tsx sin tener que alternar con archivos .css

## 