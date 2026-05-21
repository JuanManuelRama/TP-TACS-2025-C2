# Trabajo Práctico TACS 2025 C2

This project corresponds to the coursework for the second semester of TACS at UTN FRBA.

## Build & Run

We provide a `docker-compose.yaml` file to run the entire project inside containers. It can be executed with:

```bash
docker compose up --build
```

- [Backend](/backend/README.md)

## Tech stack

- Backend: Kotlin + Ktor
- Frontend: TypeScrypt + React + Vite
- Database: MongoDB
- Cache: Redis
- Deploy: AWS + Mongo Atlas

## Technical Details

- [Requirements specification](https://docs.google.com/document/d/e/2PACX-1vRKgz7eEA1fIByKMtXKxA6-Vs1rSst8cwUeTkMnZyYrDPkzkUECyK7WXqXWFSh5jwnxJMdanffdyWzB/pub)
- [Backend](/backend/README.md)
- [Persistence Strategy](persistence.md)
- [Deployment Architecture](deploy.md)
- [Cache Implementation](cache.md)
- [API usage](api-docs.md)
