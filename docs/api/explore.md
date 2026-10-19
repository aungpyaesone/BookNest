# Explore public API integration

Contract: the supplied FIGMA_API_AUDIT.md (reviewed 2026-10-17). The app uses only public taxonomy/catalog endpoints; protected Recently Viewed and view recording are excluded.

- Origin: `https://booknest-api-3jl3.onrender.com`.
- Taxonomy: `GET /api/v1/screens/explore`; retain all IDs as Long, display roots and derive child chips from parent IDs.
- Catalog: `GET /api/v1/screens/catalog?categoryId=<id>&sort=<popular|recent|title|price>&page=<n>&pageSize=20`.
- Parse `items[].book`, `items[].authors`, nullable rating/cover, demo flag and API total. All authors are retained. Currency formatting uses the ISO currency code through core presentation.
- Validate HTTP status before decoding. Status errors (including Problem Details and non-JSON bodies), timeout, parsing and cache-write failures map to localized resource messages. Cancellation propagates.
- Shared Ktor clients use 90-second request/socket timeouts and platform OkHttp/Darwin engines. Relative HTTPS artwork uses the same resolver and Coil SVG/disk cache as Home.

Room 4 migration adds taxonomy and catalog query snapshots without altering earlier tables. The query key includes category ID, sort and page size. One upsert atomically replaces the fetched-page snapshot; refresh starts with page 1 and append extends it. Failures preserve cached data, while valid empty responses replace it. Book IDs are deduplicated for rendering. No cache expiry is imposed.

Each screen observes persisted data and refreshes on creation. Query changes cancel old observation/request generations, reset scroll, and refresh page 1. Append errors stop automatic loading until explicit Retry. Serialized Navigation 3 category strings encode backend numeric IDs; older slugs resolve against taxonomy.

Tests use recorded public response fixtures, Ktor MockEngine, in-memory DAO/repository fakes, ViewModel race tests, and Android migration/persistence/UI tests. Device test compilation is part of verification; execution requires an Android test target.
