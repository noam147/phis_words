# Add Audio Button to Search Results

This plan outlines the steps to add audio playback buttons to individual words in the search results UI. We will utilize the existing functionality in the `WordButton` custom view.

## User Review Required

> [!NOTE]
> By enabling the extra features of `WordButton` in the search results, words will also show their "marked" status (add to favorites) in addition to the audio button. This provides a consistent experience with other word lists in the app.

## Proposed Changes

### Logic

#### [MODIFY] [SeacrhWordInDbActivity.java](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/java/OfflineActivities/SeacrhWordInDbActivity.java)
- In the `createButtons` method, call `btn.afterAddingToLayout()` after each word button is added to the layout. This triggers the dynamic addition of the audio and marked status icons.

### Bug Fixes & Stability

#### [MODIFY] [WordButton.java](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/java/NewViews/WordButton.java)
- **Fix Index Bug**: Update `addButtons()` to capture the view's index in its parent *before* removing it. This ensures the newly created container (containing the word and its icons) is re-inserted at the correct position.
- **Add Null Checks**: Update `setVisibility()` to check if `imageButton` and `playAudioImgButton` are null before attempting to change their visibility. This prevents crashes if `setVisibility` is called before the extra buttons are initialized.

## Verification Plan

### Manual Verification
1. Open the "Search Words" screen from the main menu.
2. Type a search query to display results.
3. Verify that each word result now has an audio icon (on the right) and a "marked" icon (on the left).
4. Tap the audio icon and confirm the word's pronunciation plays.
5. Tap the marked icon and verify it updates the word's status in the database.
6. Verify that clicking the word button itself still takes you to the word's location in the units (existing behavior).
