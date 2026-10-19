# BookNest Architecture

BookNest is a Kotlin Multiplatform application with shared Compose UI on Android and iOS. The foundation is local-first: it displays a Room-backed library and a vertical reader backed by replaceable content and progress contracts. Home integrates a public catalog feed with persistent offline snapshots. Explore browses the public taxonomy and paginated catalog. Authentication, orders, synchronization, import, add, and edit flows remain outside this foundation.

## Module Graph

| Module | Responsibility | Allowed project dependencies |
| --- | --- | --- |
| `:core:domain` | Typed `Result` and application errors | None |
| `:core:network` | Shared Ktor engines, JSON, API origin, 90-second timeouts and typed errors | Core domain |
| `:core:database` | Room database, entities, DAOs, builders, schemas | `:core:domain` only when required |
| `:core:presentation` | `UiText`, event collection, error-to-UI mapping | `:core:domain` |
| `:core:design-system` | Theme and reusable adaptive UI | None |
| `:core:settings` | App and reader preference contracts with Android/iOS persistence | None |
| `:feature:home:domain` | Home feed models and remote/local/repository contracts | Core domain |
| `:feature:home:data` | Ktor home request, DTO mappings, Room snapshot, offline repository | Home domain, core database/domain/network |
| `:feature:home:presentation` | Home storefront MVI, remote images, local search/filter, adaptive feed, Home Navigation 3 key | Home domain, core domain/presentation/design-system |
| `:feature:explore:domain` | Taxonomy, catalog/query/page models and repository/source contracts | Core domain |
| `:feature:explore:data` | Public Ktor requests, DTO validation, Room snapshots and offline pagination | Explore domain, core database/domain/network |
| `:feature:explore:presentation` | API browsing MVI, root search, server subcategory/sort/pagination and Navigation 3 keys | Explore domain, core domain/presentation/design-system |
| `:feature:library:domain` | Book model and local data contract | `:core:domain` |
| `:feature:library:data` | Room data source and entity mappings | Library domain, core database/domain |
| `:feature:library:presentation` | MVI contracts, ViewModels, screens, navigation keys | Library domain, core presentation/design-system/domain |
| `:feature:reader:domain` | Readable content and stable reading-progress contracts | Core domain |
| `:feature:reader:data` | Bundled demo content and Room progress persistence | Reader domain, core database/domain |
| `:feature:reader:presentation` | Vertical reader MVI, appearance controls, and Reader Navigation 3 key | Reader domain, core settings/presentation/design-system/domain |
| `:feature:settings:presentation` | Theme settings MVI and adaptive settings UI | Core settings/presentation/design-system |
| `:shared` | App shell, top-level Navigation 3 back stack, adaptive scene strategy, app theme state, Koin composition | Core and all feature modules |
| `:androidApp` | Android launcher only | `:shared` |

Forbidden directions are presentation to data, core to feature, and feature to feature. Platform implementations live in `androidMain` and `iosMain` while keeping the same package structure.

## Presentation and Navigation

Each screen uses an immutable State/Action/Event contract and a Root/Screen composable split. A ViewModel may emit a navigation event, but only the `:shared` composition root mutates the back stack. Home observes `HomeRepository` and refreshes once per ViewModel creation or explicit refresh. Banner/category/popular-book UI comes from the API; Continue Reading remains a bundled demo and Premium remains a static promotion. Search and price/rental filters apply only to loaded popular books. Category browsing and book/order actions show coming-soon messages; Explore displays the public category browser. Home More and category See all open Explore; other Home API categories retain coming-soon behavior.

Navigation uses serializable, user-owned Navigation 3 keys. `BookNestNavigation` starts at `HomeKey` and owns `rememberNavBackStack`, `NavDisplay`, and `rememberListDetailSceneStrategy`. Compact and medium windows use bottom navigation; expanded windows use a navigation rail. The Library destination preserves compact one-pane and expanded list-detail behavior. Navigation 2, `NavController`, and `NavHost` are prohibited by `architectureCheck`.

`SettingsKey` is a secondary Navigation 3 destination opened from the fixed Home app bar. It does not replace the Profile destination. The Home tab remains selected while Settings is open, Back returns to Home, and selecting another top-level destination replaces the secondary stack.

`ReaderKey(bookId)` is a secondary destination opened from Home Continue Reading or Library Book Detail. Reader screens hide the top-level bottom bar and navigation rail, retain their source destination on the user-owned back stack, and load content by identifier through `BookContentDataSource`.

`NavDisplay` stays in a stable full-window container. `BookNestChromeSceneStrategy` wraps each calculated scene with its own app chrome and safe-area treatment, including the complete adaptive Library scene. Chrome derives from entry metadata, not the current global stack top, so an outgoing Reader retains its fullscreen viewport while Home returns. Scene keys include the underlying scene type and key to preserve Navigation 3 transitions.

The design system owns BookNest semantic color schemes, Noto Sans Myanmar typography, spacing, shapes, touch-target sizes, and reusable branded controls. Home uses a single-column mobile feed below 600dp, a three-column book grid from 600dp, and expanded navigation plus a six-column book grid from 840dp.

Theme preference defaults to Light, supports Light/Dark/System, and is persisted through `ThemeLocalDataSource` using SharedPreferences on Android and NSUserDefaults on iOS. The app composition root observes this state so a setting change updates every destination without rebuilding navigation state.

## Home Feed

`GET https://booknest-api-3jl3.onrender.com/api/v1/home` is public. `KtorHomeDataSource` decodes and validates the full response in one request with a 90-second timeout and cancellation preservation. IDs remain Long in domain/DTOs; presentation uses string keys compatible with the existing UI actions. Relative image URLs resolve against the API origin. Prices use platform currency formatters and the returned ISO currency code.

`OfflineFirstHomeRepository` coordinates remote and local sources. Room version 3 adds `home_feeds`, a singleton JSON snapshot with no expiry; migration 2 to 3 preserves books and reading progress. Room is the feed's source of truth: only valid, successfully saved responses replace the snapshot, including genuinely empty feeds. Corrupt snapshot decoding emits an error without stopping observation of later repairs. Refresh/network/write failures preserve the existing snapshot. The UI exposes loading, refresh, cached-data, error and retry states.

The app configures Coil with Ktor and SVG decoding. Previously downloaded artwork uses Coil's disk cache; uncached images show placeholders offline. Demo catalog cards remain marked as demo and never grant reading access.

## Explore Catalog

`GET /api/v1/screens/explore` supplies the full taxonomy. Main searches only loaded root names (`parentCategoryId == null`); children become detail chips. Public `GET /api/v1/screens/catalog` receives category ID, sort (`popular`, `recent`, `title`, `price`), page and pageSize=20. All selects the parent ID; a child selects its own ID. Counts use API `total`. Cards preserve Long IDs, all authors, decimal prices, nullable covers/ratings and demo flags. Shared core presentation currency formatting and the shared Coil SVG/disk-cache configuration also serve Home. No authorization header or protected Recently Viewed request is added. Runtime sample categories, More and Recently Viewed are removed; preview/test fixtures remain.

`OfflineFirstExploreRepository` uses Room as the source of truth. Version 4 adds `explore_taxonomy` and `catalog_snapshots` through non-destructive migration 3 to 4, preserving Home snapshots, library and reading progress. Catalog keys include category ID, sort and page size; each JSON snapshot includes fetched pages and pagination metadata. Refresh replaces all pages with page 1 in one upsert, append saves the next page in one upsert, and displayed book IDs are deduplicated. Valid empty responses replace snapshots; failed network, validation or writes preserve old data. Corrupt JSON does not terminate observation, so a successful refresh repairs it. Cached snapshots have no expiry.

ViewModels observe cache immediately and request an initial refresh. Per-query request coordination prevents duplicates. Sort/subcategory changes cancel the old generation, reset scroll and start at page 1; stale results cannot mutate the active UI. Near-end loading appends while more pages remain. Append failures retain cards and expose an explicit Retry footer, blocking automatic repeated retries. Initial and cached refresh failures expose localized errors and Retry. Category route transport stays a serialized string: numeric backend IDs are encoded as decimal strings; restored legacy slugs resolve through taxonomy or show unavailable.

Explore initial loading and uncached errors are centered in the available content area, below the toolbar/search. Detail initially shows its hero/chips/count after the first catalog snapshot. A separate screen-lifetime `hasShownCatalog` flag preserves the hero and controls across subsequent query changes; query-specific `hasLoaded` controls books and count. While an uncached query loads or fails, only its results section shows centered progress or Retry, and count remains hidden with the sort position reserved. Cached query results display immediately. Cached data stays visible during background refresh without a spinner. Retry appears only for an error and is disabled during the request; successful and empty responses reserve no status row. Pagination progress is centered horizontally in the footer.

Detail has no search or filter sheet. Book clicks retain coming-soon behavior. The Home header is the single notification entry point; Explore main has neither a back button nor a notification action.

`CategoryBooksKey(categoryId)` is a secondary scene with `BOOK_NEST_HIDE_NAVIGATION` metadata: bottom navigation and the expanded rail are hidden, safe drawing insets remain applied, and Back restores the category browser with its search and lazy-grid scroll state. Reader keeps its separate edge-to-edge scene behavior. The app shell derives chrome per scene during push/pop transitions. Both screens use lazy three-column phone grids and expand columns to maintain at least 120dp cards on tablets. Book cover fallback, category icon mapping, and the search field are shared with Home through the design system. `BookNestSearchField` uses a 48dp minimum height with content-driven growth at large font scales; Home alone supplies its trailing filter action. Explore content centers within 1200dp with 16dp compact/32dp tablet horizontal padding. Missing covers use a title placeholder. API banner images are optional; bundled hero artwork and a localized generic subtitle provide fallback.

## Data and Dependency Injection

`BookLocalDataSource` is defined in library domain and exposes observable typed results. `RoomBookDataSource` is its local implementation. Reader content uses `BookContentDataSource`; the development implementation provides an original Burmese demo book and can later be replaced by an EPUB/PDF implementation without changing presentation. `ReadingProgressLocalDataSource` stores stable chapter and paragraph identifiers in Room. Room schema files are version-controlled; versions increase sequentially and destructive migration is not allowed.

Koin definitions are grouped by layer: `coreNetworkModule`, `coreDatabaseModule`, `coreSettingsModule`, feature presentation/data modules, and `appPresentationModule`. `appModules` is assembled in `:shared`; Android and iOS initialize it at their platform entry points.

## Enforcement

- `architectureCheck` validates project dependencies, domain imports, and the Navigation 3-only policy.
- `codingStandardsCheck` runs ktlint, Detekt naming checks, and resource-name validation.
- `verifyBookNest` runs architecture and coding checks, tests, the Android debug build, and iOS simulator compilation.
- CI publishes `verify-booknest`; repository branch protection should require it before merge.
