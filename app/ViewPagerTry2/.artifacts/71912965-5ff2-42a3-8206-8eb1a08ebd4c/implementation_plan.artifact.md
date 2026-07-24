# Home UI Improvement Plan

This plan aims to modernize and organize the Home UI (`MenuOfflinePage`) of the application. The current UI is disorganized, with scattered buttons and inconsistent styling.

## Proposed Changes

### [Layout Redesign]

#### [MODIFY] [activity_menu_offline_page.xml](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/res/layout/activity_menu_offline_page.xml)
- **Root**: `ConstraintLayout` with proper padding and structured constraints.
- **Header Section**:
    - `progressRankImageView` and `currentLevelTextView` grouped for a badge-like look.
    - `progressBar` (using custom `TextProgressBar`) centered vertically with the rank.
    - `imageButton5` (Settings) moved to the top-right corner.
- **Daily Word**:
    - Wrapped in a `MaterialCardView` with `16dp` corner radius and elevation.
    - Title "Daily Word" added for context.
    - Large, bold text for the word itself.
- **Action Buttons**:
    - **Primary Group**: "Play Game" (with icon) and "Write Game" in a horizontal row.
    - **Secondary Group**: "Search Words" (full width, with icon), "Sort Words" and "Marked" (half width each).
    - Use `MaterialButton` for all actions.
- **Bottom Bar**:
    - Currency icons aligned horizontally in the center.
    - "Reset Points" button at the very bottom as a text button.

### [UI Components]

#### [NEW] [ic_search.xml](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/res/drawable/ic_search.xml)
- Standard search icon for the "Search Words" button.

#### [NEW] [ic_gamepad.xml](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/res/drawable/ic_gamepad.xml)
- Gamepad icon for the "Play Game" button.

## Verification Plan

### Manual Verification
- Deploy the app and verify the new layout on the device.
- Check that all buttons still function correctly (navigate to their respective pages).
- Verify the "Daily Word" still updates correctly.
- Ensure the progress bar and rank image are correctly displayed.
- Check that the level number is correctly positioned over the rank shield.
