# Home UI Improvement Walkthrough

I have modernized the Home UI of the application, focusing on organization, Material 3 design principles, and improved readability.

## Changes Made

### 1. Modernized Layout Structure
- Refactored `activity_menu_offline_page.xml` to use a clean, structured `ConstraintLayout`.
- Added `android:layoutDirection="ltr"` to ensure a consistent experience regardless of system locale.
- Improved spacing and padding for a less cluttered feel.

### 2. Enhanced UI Components
- **Header**: Grouped the Rank shield and Level badge. The Level badge now overlaps the shield for a cohesive look.
- **Daily Word Card**: Wrapped the daily word in a `MaterialCardView` with elevation and corner radius, making it a focal point of the screen.
- **Material Buttons**: Replaced standard buttons with `MaterialButton` (Tonal, Outlined, and Text styles) to better reflect Material 3 design.
- **Icons**: Integrated new vector icons for "Search" and "Play Game" to improve visual cues.

### 3. Improved Progress Visualization
- Refined `TextProgressBar.java` to adjust text scaling and font size for better readability.
- Removed hardcoded scaling factors that were causing the progress bar to appear overly thick.

## Screenshots

````carousel
![New Home UI Layout](file:///C:/dev/phis_words/app/ViewPagerTry2/.artifacts/71912965-5ff2-42a3-8206-8eb1a08ebd4c/new_home_ui.png)
<!-- slide -->
![Progress and Rank Detail](file:///C:/dev/phis_words/app/ViewPagerTry2/.artifacts/71912965-5ff2-42a3-8206-8eb1a08ebd4c/header_detail.png)
````

> [!NOTE]
> The "Daily Word" section now shows "Loading..." by default and will update once the network request succeeds.

## Verification Results
- **Layout**: Verified on device via screenshot. All elements are correctly aligned and styled.
- **Functionality**: Buttons maintain their original `onClick` associations and logic.
- **Readability**: Progress bar text is now clear and well-proportioned.
