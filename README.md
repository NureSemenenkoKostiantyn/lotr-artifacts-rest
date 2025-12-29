# LOTR Artifacts REST API

A Spring Boot application that exposes a REST API for managing **artifacts** and their **creators** from the Lord of the Rings universe. Artifacts reference creators through a many-to-one relationship and can be created, updated, listed with filters, exported to CSV, and bulk-imported from a JSON file.

## Getting started

1. **Start PostgreSQL:**
   ```bash
   docker compose up -d
   ```
2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Liquibase creates the schema on startup and seeds several creator records.

## Domain model

- **Creator**: `id`, `name`, `race`, `realm`
- **Artifact**: `id`, `name`, `creatorId` (required), `origin`, `tags`, `yearCreated`, `powerLevel`

## API endpoints

### Artifact endpoints (`/api/artifact`)

| Method & Path | Description |
| --- | --- |
| `POST /api/artifact` | Create an artifact (validates required fields). |
| `GET /api/artifact/{id}` | Get artifact details, including its creator. |
| `PUT /api/artifact/{id}` | Update an artifact by ID (validates input and creator existence). |
| `DELETE /api/artifact/{id}` | Delete an artifact by ID. |
| `POST /api/artifact/_list` | Paginated list with optional filters. Returns `{ "list": [...], "totalPages": n }`. |
| `POST /api/artifact/_report` | Generates a CSV report for all matches (not just one page) and returns it as a download. |
| `POST /api/artifact/upload` | Multipart upload (field `file`) with a JSON array of artifacts; returns counts of imported/failed records. |

**Create/Update request body example**

```json
{
  "name": "The One Ring",
  "creatorId": 1,
  "origin": "Mount Doom",
  "tags": "ring,shadow",
  "yearCreated": 1600,
  "powerLevel": 1000
}
```

**List & report request body example**

```json
{
  "creatorId": 2,
  "origin": "Eregion",
  "yearFrom": 1500,
  "yearTo": 2000,
  "powerFrom": 100,
  "powerTo": 5000,
  "page": 0,
  "size": 20
}
```

### Creator endpoints (`/api/creators`)

| Method & Path | Description |
| --- | --- |
| `GET /api/creators` | List all creators. |
| `POST /api/creators` | Create a creator (rejects duplicate names). |
| `GET /api/creators/{id}` | Get creator details by ID. |
| `PUT /api/creators/{id}` | Update a creator (enforces name uniqueness). |
| `DELETE /api/creators/{id}` | Delete a creator by ID. |

## Data import

- A sample JSON file ready for upload lives at `src/main/resources/artifacts-upload.json`.
- Upload it through `POST /api/artifact/upload` with multipart field `file`. The response reports how many artifacts were imported versus failed (e.g., due to missing creators).

## Database migrations

Liquibase runs automatically on startup:

1. Creates `creator` and `artifact` tables (with foreign key from artifact to creator).
2. Adds indexes and a unique constraint on creator names.
3. Seeds four creators to match the sample import file.

## Testing

Run the full integration test suite (requires Docker for Testcontainers):

```bash
./mvnw test
```
