# BookNest Coding Standards

Kotlin official style is the baseline. These project rules make architecture and intent visible in names.

## Packages and Modules

| Item | Rule | Example |
| --- | --- | --- |
| Kotlin package | Lowercase, no underscores | `com.nps.booknest.feature.library.data` |
| Gradle module | Lowercase layer segments | `:feature:library:presentation` |
| Multiword module | Kebab-case | `:core:design-system` |
| Package structure | Match module and layer | `feature.library.presentation.list` |

The Android launcher keeps the Kotlin Multiplatform template name `:androidApp`; it is the only camelCase module exception. Do not put source-set details such as `common`, `kmp`, or `impl` in packages. Platform code belongs in platform source sets without changing its package structure.

## Types and Files

| Purpose | Pattern | Example |
| --- | --- | --- |
| Domain model | Singular noun | `Book` |
| Data-source interface | `<Model><Local/Remote>DataSource` | `BookLocalDataSource` |
| Implementation | Technology or behavior prefix | `RoomBookDataSource` |
| Room entity / DAO | `<Model>Entity` / `<Model>Dao` | `BookEntity`, `BookDao` |
| Database | `<App>Database` | `BookNestDatabase` |
| ViewModel | `<Screen>ViewModel` | `LibraryListViewModel` |
| MVI contract | `<Screen>State/Action/Event` | `LibraryListState` |
| Navigation key | `<Destination>Key` | `BookDetailKey` |
| Koin property | `<layer>Module` | `libraryDataModule` |
| Test | `<Subject>Test` | `LibraryListViewModelTest` |

Use the primary type or a cohesive responsibility as the file name. Prefer `BookMappings.kt` and `ResultExtensions.kt`; never use generic `Extensions.kt`, `Utils.kt`, or `Helpers.kt`. Do not prefix interfaces with `I`, suffix implementations with `Impl`, or use vague role names such as `Manager`, `Helper`, `Utils`, `Common`, and `Base`. Write acronyms as words: `bookId`, `coverUrl`, `apiClient`, `BookDto`, and `BookDao`.

## Functions and Properties

- Use `camelCase` for functions and properties, `PascalCase` for types and composables, and `UPPER_SNAKE_CASE` for constants.
- Prefix booleans with `is`, `has`, `can`, or `should`.
- Use plural nouns for collections: `books`, `selectedBookIds`.
- Use `getBook()` for one-shot reads and `observeBooks()` for streams.
- Name commands verb-first: `saveBook()`, `deleteBook()`, `refreshLibrary()`.
- Name mappers `toBook()` and `toEntity()`.
- Use `OnBookClick` for actions, `onBookClick` for callbacks, and `NavigateToBookDetail` for events.
- Avoid vague verbs such as `handle`, `process`, and `execute` unless the enclosing type makes the exact behavior explicit.

## Compose

Screen APIs follow `LibraryListRoot`, `LibraryListScreen`, and private `LibraryListPreview`. Reusable components use UI nouns such as `BookListItem`, `EmptyLibraryContent`, and `BookDetailPane`. Callback parameters use `on<Action>`. Place `Modifier` after required data and before optional UI parameters. UI strings, colors, and reusable dimensions come from resources or the design system.

## Resources and Database

Compose resource names are feature-prefixed `snake_case`, for example `library_list_title`, `book_detail_author`, and `error_database_unknown`. Icons use `ic_<meaning>`. Database tables and columns use plural/snake_case names such as `books`, `book_id`, and `cover_url`. Migrations use names such as `Migration1To2`.

## Tests

Test files and classes match the subject. Test functions describe behavior, condition, and result:

```kotlin
@Test
fun `observeBooks emits empty list when database is empty`() = runTest { }
```

Use `FakeBookLocalDataSource` for fakes and `bookFixture()` for fixtures. Do not create generic `TestUtils` containers.

## Exceptions

Broad suppressions and new lint baselines are forbidden. A necessary exception must be narrowly scoped, placed next to the declaration, and explain why the rule cannot reasonably apply.
