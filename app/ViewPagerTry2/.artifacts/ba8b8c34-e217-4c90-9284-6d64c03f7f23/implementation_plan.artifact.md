# implementation_plan.artifact.md - Modernize Search Words UI

Modernize the "Search Words" page using Material 3, `RecyclerView`, and shared components to match the new "Sort Words" experience.

## User Review Required

> [!IMPORTANT]
> - The search results will now be displayed in the same Material 3 card format as the "Sort Words" page.
> - The manual button generation will be replaced by a `RecyclerView` for better performance and consistency.

## Proposed Changes

### [Layouts]

#### [MODIFY] [activity_seacrh_word_in_db.xml](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/res/layout/activity_seacrh_word_in_db.xml)
- Reconstruct the layout using `CoordinatorLayout`.
- Add `AppBarLayout` with `MaterialToolbar`.
- Integrate a `TextInputLayout` with a `TextInputEditText` for searching within the header area.
- Add a `RecyclerView` for displaying search results.

### [Logic]

#### [MODIFY] [SeacrhWordInDbActivity.java](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/java/OfflineActivities/SeacrhWordInDbActivity.java)
- Refactor to use `WordSortAdapter` (or a more generic version). Since `WordSortAdapter` already handles the card display, audio, and marking, it's perfect for reuse.
- Update search logic to update the `RecyclerView` adapter instead of clearing/adding views to a `LinearLayout`.
- Implement a "Load More" mechanism that integrates cleanly with the `RecyclerView` (e.g., a button at the end of the list or automatic pagination).

### [Refactoring]

#### [MODIFY] [WordSortAdapter.java](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/java/OfflineActivities/WordSortAdapter.java)
- Add a listener interface to handle clicks on the word cards, as "Search" needs to navigate to the sorting page when a word is clicked.

## Verification Plan

### Manual Verification
- Open the "Search Words" page.
- Verify the Material 3 design matches the "Sort Words" page.
- Type a word in the search box and verify results appear in the new card format.
- Click a word card and verify it navigates to the "Sort Words" page for that word's unit.
- Verify audio playback and word marking still work on the search result cards.
