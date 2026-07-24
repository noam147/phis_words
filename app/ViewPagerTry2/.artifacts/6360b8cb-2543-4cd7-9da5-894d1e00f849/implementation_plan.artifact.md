# Implementation Plan - Fix Write Minigame UI

The goal is to modernize the UI of the "Write" minigame (`WordQuestionsPageWriteAnswer`) to match the Material 3 design established in the main menu.

## Proposed Changes

### UI Redesign
#### [MODIFY] [activity_word_questions_page_write_answer.xml](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/res/layout/activity_word_questions_page_write_answer.xml)
- **Header**: Add a progress bar (using `TextProgressBar` if possible) and a close button at the top.
- **Question Area**: Use a `MaterialCardView` to prominently display the word that needs to be translated.
- **Input Area**: Replace the bare `TextInputEditText` with a `TextInputLayout` + `TextInputEditText` combo for a more modern look and better error handling.
- **Action Buttons**: Standardize "Send", "Continue", and "Override" buttons using `MaterialButton` with appropriate styles (Tonal, Outlined, etc.).
- **Spacing**: Use standard 8dp/16dp/24dp margins and padding.

### Logic Refinement
#### [MODIFY] [WordQuestionsPageWriteAnswer.java](file:///C:/dev/phis_words/app/ViewPagerTry2/app/src/main/java/ExercisesPages/WordQuestionsPageWriteAnswer.java)
- **View Binding**: Update view IDs and types to match the new layout.
- **Feedback**: Use `TextInputLayout` helper text or error states to show the correct answer instead of a hidden `EditText`.
- **Typo Fixes**: Correct "sulotion" to "solution" in hints and variable names.
- **Visual Feedback**: Smoothly change button colors or card backgrounds to indicate correct/wrong answers.

## Verification Plan

### Automated Tests
- Build the project to ensure no layout or compilation errors.
- (Optional) Run UI tests if available.

### Manual Verification
- Deploy to device/emulator.
- Navigate to the "Write" game from the main menu.
- Verify that the layout is centered, responsive, and matches the app's overall theme.
- Test the game flow:
    - Entering a correct answer.
    - Entering a wrong answer.
    - Using the "Override" button.
    - Continuing to the next question.
    - Exiting the game.
