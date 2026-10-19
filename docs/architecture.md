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
| `:feature:profile:presentation` | Sample Profile dashboard and edit form MVI | Core presentation/design-system |
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

The design system owns BookNest semantic color schemes, Noto Sans Myanmar typography, spacing, shapes, touch-target sizes, and reusable branded controls. Home uses a single-column mobile feed below 600dp. Tablet portrait windows use four Popular Books columns; landscape retains three columns in medium layouts and six in expanded layouts, with expanded navigation from 840dp.

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

`CategoryBooksKey(categoryId)` is a secondary scene with `BOOK_NEST_HIDE_NAVIGATION` metadata: bottom navigation and the expanded rail are hidden, safe drawing insets remain applied, and Back restores the category browser with its search and lazy-grid scroll state. Reader keeps its separate edge-to-edge scene behavior. The app shell derives chrome per scene during push/pop transitions. Both Explore screens use four phone columns, switching to three at font scale 1.3 and above. From a 600dp viewport, column counts use the capped, padded content width with 8dp gaps and minimum category/book widths of 96dp/112dp scaled for large text. Grid rows use 12dp gaps. Compact text slots align titles, authors and prices; demo badges overlay covers. Book cover fallback, category icon mapping, and the search field are shared with Home through the design system. `BookNestSearchField` uses a 48dp minimum height with content-driven growth at large font scales; Home and Library supply trailing filter actions; Explore category search omits the filter action. Explore content centers within 1200dp with 16dp compact/32dp tablet horizontal padding. Missing covers use a title placeholder. API banner images are optional; bundled hero artwork and a localized generic subtitle provide fallback.

## Library Dashboard

`LibraryOverviewKey` is the Library tab root and renders a presentation-only sample dashboard. It uses existing logo, search, cover/Coil, buttons, typography, theme and app chrome. Reading Now, sample summary counts, collection cards and Recently Added match the supplied reference; collection artwork is bundled. Search matches sample titles/authors, tabs filter sample status flags, and the sort sheet keeps draft changes until Apply. Reset restores the draft default; dismiss discards unapplied edits. Summary counts and collections remain independent of search.

Sample book, Continue Reading, collection, overflow and deferred See all actions show coming-soon messages. Summary tiles select local tabs; Reading Now, My Books and Collections See all open dedicated Library destinations. Samples never enter Room or grant readable content. Notification remains Home-only. The dashboard centers in 1200dp with 16dp compact/32dp tablet margins, lazy scrolling and horizontal rows whose cards grow for larger text.

The design system owns shared compact platform sans-serif typography with native script fallback. Library scopes it to the dashboard and sort sheet; Home and Explore reuse the compact section/card/metadata styles. Home retains its horizontal phone feed and existing adaptive book column counts, with 12dp/16dp phone/tablet section gaps. Shared selection chips use primary/onPrimary selected colors and 6dp corners in Library and category detail. The Library Continue Reading action uses a content-width cream compact button with a 32dp minimum visual height inside a separate 48dp minimum touch target; larger text can grow both. Shared logo sizing is optional and retains its defaults for Home. The shared search field keeps a 48dp minimum with content-driven height. Library summary cards share one intrinsic row height, retain equal content alignment at larger font scales, and scroll horizontally when minimum card widths cannot fit. Design-system outline vectors supply the bookshelf navigation icon and dashboard summary/filter icons.

The original Room-backed `LibraryListKey`, adaptive `BookDetailKey` list-detail scenes, and Reader remain registered. Home's existing local-library callback still opens `LibraryListKey`; the bottom navigation and rail open the new overview. Overview has no list-pane metadata, so expanded windows show a full dashboard instead of an unused detail placeholder.

## Data and Dependency Injection

`BookLocalDataSource` is defined in library domain and exposes observable typed results. `RoomBookDataSource` is its local implementation. Reader content uses `BookContentDataSource`; the development implementation provides an original Burmese demo book and can later be replaced by an EPUB/PDF implementation without changing presentation. `ReadingProgressLocalDataSource` stores stable chapter and paragraph identifiers in Room. Room schema files are version-controlled; versions increase sequentially and destructive migration is not allowed.

Koin definitions are grouped by layer: `coreNetworkModule`, `coreDatabaseModule`, `coreSettingsModule`, feature presentation/data modules, and `appPresentationModule`. `appModules` is assembled in `:shared`; Android and iOS initialize it at their platform entry points.

## Enforcement

- `architectureCheck` validates project dependencies, domain imports, and the Navigation 3-only policy.
- `codingStandardsCheck` runs ktlint, Detekt naming checks, and resource-name validation.
- `verifyBookNest` runs architecture and coding checks, tests, the Android debug build, and iOS simulator compilation.
- CI publishes `verify-booknest`; repository branch protection should require it before merge.

## Profile Dashboard

`:feature:profile:presentation` owns presentation-only sample identity, summary counts, premium promotion, activity and account/support UI. It reuses shared compact typography, logo, buttons and outline icons. No API, authentication, persistence or library dependency is introduced. The header notification remains Home-only. Profile uses a lazy single-column phone dashboard, two-column activity/account panels from 840dp, a 1200dp content cap and 16dp/32dp phone/tablet margins. Summary cells scroll when minimum widths cannot fit; rows grow with text.

The existing `ProfileKey` now renders `ProfileRoot`. `EditProfileKey` is a secondary Navigation 3 destination with hidden bottom bar/rail and safe insets; Back restores dashboard scroll. The edit form is capped at 600dp, scrolls with the keyboard, keeps Email read-only and limits Bio to 100 characters. Saveable-state and ViewModel-store Navigation 3 entry decorators retain each destination independently and clear popped ViewModels. Draft Name/Bio live only in the edit ViewModel and are discarded on pop; Save and camera show coming-soon without updating profile data. Initials provide a bundled, network-independent avatar fallback. `ProfileSettingsKey` reuses Settings while preserving Profile selection and Back origin; existing `SettingsKey` retains Home behavior. Personal Information opens Edit Profile; other deferred actions show coming-soon.

## Library See All Screens

`ReadingNowKey`, `MyBooksKey` and `CollectionsKey` are secondary Navigation 3 destinations in the existing Library presentation module. Scene metadata hides bottom navigation and the rail while retaining safe drawing insets. Back restores the dashboard entry, including its search/tab/sort and lazy scroll state. Original local-library list/detail and Reader routes remain independent.

All Library sample-book screens use the same six presentation-only fixtures. Original four-book status/progress values remain intact; finished Dune and downloaded The Midnight Library extend the sample. Dashboard summary counts remain promotional sample totals. Browse counts and status chips derive from actual samples; displayed counts reflect search and applied filters. Reading Now includes only reading samples and sorts by last-read order, title or progress. My Books combines title/author search, category and status, with recent-added/title sorting. Reading status takes display priority over finished, saved and downloaded; these underlying flags remain independently filterable.

Collections contains six UI-only fixtures with local name search, ownership/favorite filtering and recent-updated/title sorting. Filter sheets edit drafts: Apply commits, Reset resets only the draft and dismiss discards it. Toolbar Search scrolls to and focuses the shared field. Cards, Continue/Read/View, collection creation and overflow remain coming-soon without writing data or granting content access. Bundled artwork is reused, with semantic-color outline illustration fallbacks where no image exists.

Browse toolbars remain fixed while heroes, search/chips/counts and content scroll lazily. Content caps at 1200dp with 16dp/32dp phone/tablet margins. Book lists use one phone column and up to two tablet columns when each has 384dp times the font scale on tablets; collections use two normal phone columns, one large-text phone column and adaptive tablet columns. Compact shared typography, 48dp touch targets and content-driven card heights preserve large-text access. No API, database or module dependency changes are introduced.

My Books uses a dedicated compact card: a 64dp cover, category pill and top-aligned action/overflow header. Reading actions use primary/onPrimary; Read/View use a borderless secondary-container tone. Progress and chapter span the body below the complete header instead of sharing its action column. The body has a minimum cover height so progress/status footers align with the cover bottom while long text grows the card. Cards narrower than 320dp or font scale 1.3 and above move the action to its own row while keeping overflow at the top. Buttons retain a 32dp minimum visual height inside a 48dp touch target; text can grow the layout. Reading Now and dashboard card styles retain their separate existing rendering.

## Home All Books

Home All pushes the serializable `AllBooksKey` with Home scene metadata and hidden bottom navigation/rail. Other Home categories push the existing `CategoryBooksKey` using backend Long IDs encoded as decimal strings; Back returns to the retained Home entry. More and category See all retain Explore routing. All Books lives in Library presentation, reusing the sample books and shared browse MVI/filter/sort state without a feature-to-feature dependency. Its initial grid has three phone columns (two at font scale 1.3+) and adaptive tablet columns at a minimum 168dp times font scale. List mode reuses My Books rows; switching layouts retains filters and resets results scroll. Hero total comes from the full sample set, while results count follows combined filters. Status chips omit counts. Samples remain separate from public category API results; no persistence, API, or entitlement changes are introduced.

Shared Android device tests depend on Home domain to provide an isolated Home repository for real navigation tests; runtime module dependencies remain unchanged. The architecture dependency allowlist includes this test-only edge.

## Tablet Library Sizing

Library overview and browse screens opt into tablet typography from a 600dp available window width; narrow split-screen retains compact sizing. System font scale stays intact. Shared selection chips and compact buttons accept an optional text style, preserving defaults for other features. Tablet Library uses 24sp titles, 16sp sections, 14sp book titles and 12–13sp metadata, larger covers/artwork, and a 112dp minimum summary height. Book rows require 384dp times font scale for two columns; All Books uses a 168dp times font-scale grid minimum. Collections retains its existing 220dp tablet minimum to avoid shrinking existing artwork while labels grow. Home Popular Books uses four columns for portrait windows with both dimensions at least 600dp; compact and landscape rules remain unchanged. All Books cover/menu separation and 48dp touch targets remain intact.

## Tablet Typography Across Screens

Home, Explore, Profile and Edit Profile select the shared tablet compact text preset at window widths of at least 600dp. Existing theme-based Home/Explore content, Settings, original local Library list/detail and navigation labels opt into a scoped 20% font-size and line-height increase. Shared search, modal controls and status text inherit their screen theme; compact chips/buttons receive an explicit adaptive text style. Screen wrappers do not enclose navigation scenes globally. Existing Library overview/browse tablet sizing is applied once, and Reader retains its original theme and reading preferences. Phone and narrow split-screen typography remains unchanged; system font scale is preserved.
