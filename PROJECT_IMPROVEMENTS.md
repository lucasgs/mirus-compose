# Mirus Android Project Review

## What is already solid

- Clear separation between `data`, `domain`, `presentation`, and `ui`
- Good use of Jetpack Compose, Hilt, Room, Retrofit, and Coroutines
- Offline-oriented approach with Room-backed reads
- Decent unit test coverage around repositories and use cases

## Recommended technical improvements

### Progress summary
- [x] 1. Modernize the build setup
- [x] 2. Fix search flow behavior
- [x] 3. Reduce duplicate collectors in detail screen
- [ ] 4. Improve Room sync efficiency
- [ ] 5. Tighten architecture boundaries
- [ ] 6. Improve UI scalability
- [ ] 7. Refresh navigation approach
- [ ] 8. Strengthen resilience and observability
- [ ] 9. Improve test coverage breadth
- [ ] 10. Repository hygiene

### 1. Modernize the build setup ✅ Done
**Why:** The project was on older Android tooling (`AGP 7.4.2`, Kotlin `1.8.10`, Gradle `7.5`, Compose `1.3.x`) and failed to build on newer JDKs.

**Done:**
- Upgraded Gradle wrapper to `8.10.2`
- Upgraded Android/Kotlin tooling to a modern stack
- Moved dependency and plugin versions into `gradle/libs.versions.toml`
- Replaced root `ext` version management with version catalog aliases
- Updated app dependencies and Java target to `17`
- Fixed the navigation compile issue caused during the upgrade

**Validation:**
- `./gradlew help` ✅
- `./gradlew testDebugUnitTest` ✅

**Files touched:**
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/libs.versions.toml`
- `build.gradle`
- `app/build.gradle`
- `app/src/main/java/com/dendron/mirus/ui/navigation/NavigationGraph.kt`

### 2. Fix search flow behavior
**Why:** Search currently launches a new collection every time `searchMovie()` is called, with no debounce or cancellation.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/presentation/movie_search/MovieSearchViewModel.kt`

**Checklist:**
- [x] Drive search from query changes with `debounce`, `distinctUntilChanged`, and `flatMapLatest`
- [x] Keep a single `StateFlow` screen state
- [x] Add empty-query handling
- [x] Add recent-search support

### 3. Reduce duplicate collectors in detail screen
**Why:** Favorite and genre observers are re-launched in ways that can create repeated collectors over time.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/presentation/movie_detail/MovieDetailViewModel.kt`

**Checklist:**
- [x] Collect each source once in `init`
- [x] Avoid nested `viewModelScope.launch { flow.launchIn(viewModelScope) }`
- [x] Update favorite state optimistically after toggle, or expose a single combined UI state

### 4. Improve Room sync efficiency
**Why:** Movie sync writes one movie at a time inside repeated transactions.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/data/repository/MovieRepositoryImp.kt`
- `app/src/main/java/com/dendron/mirus/data/local/MovieDao.kt`

**Checklist:**
- [ ] Batch insert movies and section rows in one transaction per feed
- [ ] Clear stale mapping rows before re-inserting feed content
- [ ] Mark DAO write methods as `suspend`
- [ ] Consider `upsert` APIs where possible

### 5. Tighten architecture boundaries
**Why:** The app is layered, but still lives in a single module and some APIs are awkward.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/domain/repository/MovieRepository.kt`
- entire `app/src/main/java/com/dendron/mirus/...` tree

**Checklist:**
- [ ] Split into modules like `app`, `core`, `domain`, `data`, `presentation/design-system`
- [ ] Remove unnecessary `suspend` from repository methods returning `Flow`
- [ ] Standardize on a single result/error model across use cases

### 6. Improve UI scalability
**Why:** The movie list screen uses a vertically scrolling `Column`, which is less scalable for larger datasets.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/ui/movie_list/MovieListScreen.kt`

**Checklist:**
- [ ] Use `LazyColumn` / `LazyRow`
- [ ] Add stable keys for lists
- [ ] Consider Paging 3 for larger catalogs and search results

### 7. Refresh navigation approach
**Why:** Navigation depends on Accompanist animation, which is largely superseded by official navigation improvements.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/ui/navigation/NavigationGraph.kt`
- `app/src/main/java/com/dendron/mirus/presentation/MainActivity.kt`

**Checklist:**
- [ ] Migrate toward current Navigation Compose APIs
- [ ] Consider type-safe routes
- [ ] Clean up small code smells like the stray `Scaffold` expression in `MainActivity.kt`

### 8. Strengthen resilience and observability
**Why:** Sync and network handling are basic and do not expose much retry/state detail.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/domain/use_case/SyncMoviesUseCase.kt`
- `app/src/main/java/com/dendron/mirus/di/AppModule.kt`

**Checklist:**
- [ ] Add logging/interceptors for debug builds only
- [ ] Add retry/backoff rules for transient network failures
- [ ] Consider WorkManager for background refresh
- [ ] Surface "last updated" and offline status in the UI

### 9. Improve test coverage breadth
**Why:** There are many unit tests, but almost no instrumentation/UI coverage.

**Evidence:**
- `app/src/test/java/...`
- `app/src/androidTest/java/com/dendron/mirus/ExampleInstrumentedTest.kt`

**Checklist:**
- [ ] Add Compose UI tests for list, search, favorites, and detail screens
- [ ] Add DAO tests for Room relations and sync behavior
- [ ] Add screenshot tests for important states

### 10. Repository hygiene
**Why:** The repo contains macOS metadata files.

**Evidence:**
- `app/src/main/java/com/dendron/mirus/.DS_Store`
- `app/src/main/java/com/dendron/mirus/data/.DS_Store`
- `app/src/main/java/com/dendron/mirus/data/remote/.DS_Store`

**Checklist:**
- [ ] Remove `.DS_Store` files
- [ ] Ignore them in `.gitignore`

## Nice-to-have product features

### High value
- **Watchlist** separate from favorites
- **Filters** by genre, year, rating, and sort order
- **Infinite scrolling / pagination** for discovery and search
- **Movie trailers** and cast/crew details
- **Share movie** deep links
- **Pull-to-refresh** on major feeds

### Great UX additions
- **Recent searches** and search suggestions
- **Dynamic theming** toggle and explicit dark mode setting
- **Tablet-first dual-pane detail layout** instead of only a drawer-style adaptation
- **Skeleton loading states** instead of only centered progress indicators
- **Empty-state illustrations/messages** for favorites and search

### Offline-first upgrades
- **Cached detail pages** with last sync timestamp
- **Bookmarks/watch history**
- **Background sync** when connectivity returns

## Suggested priority order

1. ✅ Upgrade build tooling so the project builds reliably on current JDKs
2. Refactor search and detail view model flow handling
3. Batch and clean up Room sync logic
4. Add UI/instrumentation tests
5. Add pagination, filters, and richer movie details

## Quick verdict

This is a strong foundation for a movie discovery app, especially for a single-module sample-style project. The biggest opportunity is to modernize the build/tooling and tighten reactive/state management. After that, the best product wins would be pagination, better search UX, richer detail pages, and more explicit offline behavior.
