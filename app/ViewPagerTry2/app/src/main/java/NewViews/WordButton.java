
package NewViews;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.R;

/**
 * A custom {@link androidx.appcompat.widget.AppCompatButton} that displays a word
 * and includes interactive buttons for marking the word and playing its audio.
 * These additional buttons are added dynamically to a {@link RelativeLayout}
 * that wraps the WordButton.
 */
public class WordButton extends androidx.appcompat.widget.AppCompatButton {
    private DBManager dbManager;
    private ImageButton imageButton = null; // Button to mark/unmark the word
    private ImageButton playAudioImgButton = null; // Button to play the word's audio
    private FinalWordProperties finalWordProperties = null; // Contains word details and user-specific info

    /**
     * Constructor for creating a WordButton.
     *
     * @param context             The Context the view is running in.
     * @param finalWordProperties The {@link FinalWordProperties} associated with this word.
     * @param activeDbManager     An instance of {@link DBManager} for database operations.
     */
    public WordButton(Context context, FinalWordProperties finalWordProperties, DBManager activeDbManager) {
        super(context);
        this.finalWordProperties = finalWordProperties;
        this.dbManager = activeDbManager;
    }

    /**
     * Overrides {@link #setVisibility(int)} to also control the visibility
     * of the associated mark and play audio buttons.
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (imageButton != null) {
            imageButton.setVisibility(visibility);
        }
        if (playAudioImgButton != null) {
            playAudioImgButton.setVisibility(visibility);
        }
    }

    /**
     * Sets the {@link DBManager} instance for this button and adds the interactive buttons.
     * This is useful if the DBManager is not available during initial construction.
     *
     * @param activeDbManager The {@link DBManager} instance.
     */
    public void setDbManager(DBManager activeDbManager) {
        this.dbManager = activeDbManager;
        addButtons();
    }

    /**
     * Called after the WordButton has been added to a layout. This ensures
     * that the parent {@link ViewGroup} is available for adding the overlay buttons.
     */
    public void afterAddingToLayout() {
        if (dbManager != null) {
            addButtons();
        }
    }

    /**
     * Adds the mark and play audio {@link ImageButton}s as overlays to the WordButton.
     * This involves creating a {@link RelativeLayout} to contain both the WordButton
     * and the overlay buttons, then replacing the WordButton in its original parent
     * with this new container.
     */
    private void addButtons() {
        RelativeLayout container = new RelativeLayout(getContext());

        // Ensure the parent is not null
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this); // Remove WordButton from its parent
        }

        container.addView(this); // Add WordButton to the container

        addMarked(container); // Add the marked button
        add_volume_img_display(container); // Add the volume button

        // Check if the parent is not null and add the new container at the same index
        if (parent != null) {
            int index = parent.indexOfChild(this);
            parent.addView(container, index);
        }
    }

    /**
     * Adds the play audio {@link ImageButton} to the container, positioned on the right
     * of the WordButton. It sets an {@link android.view.View.OnClickListener} to
     * trigger audio playback using {@link MakeViewPlayAudio}.
     *
     * @param container The {@link RelativeLayout} to which the button is added.
     */
    private void add_volume_img_display(RelativeLayout container) {
        playAudioImgButton = new ImageButton(getContext());
        playAudioImgButton.setElevation(20); // Make the image appear on top of the button
        playAudioImgButton.setImageResource(R.drawable.baseline_play_audio_img_24); // Set the play audio icon
        playAudioImgButton.setBackground(null); // Remove default background

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        container.addView(playAudioImgButton, params);
        playAudioImgButton.setOnClickListener(v -> MakeViewPlayAudio.playRecordingOfWord(this.getContext(), this.finalWordProperties.getWordProperties().getWord_id()));
    }

    /**
     * Adds the mark/unmark {@link ImageButton} to the container, positioned on the left
     * of the WordButton. It sets an {@link android.view.View.OnClickListener} to
     * update the word's marked status in the database and toggle the button's icon.
     *
     * @param container The {@link RelativeLayout} to which the button is added.
     */
    private void addMarked(RelativeLayout container) {
        boolean isMarked = finalWordProperties.getUserDetailsOnWords().isWordMark();

        imageButton = new ImageButton(getContext());
        imageButton.setElevation(20);
        imageButton.setImageResource(isMarked ? R.drawable.baseline_check_24 : R.drawable.baseline_add_to_marked_words_24);
        imageButton.setBackground(null);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        container.addView(imageButton, params);
        imageButton.setOnClickListener(v -> {
            boolean isCurrentlyMarked = finalWordProperties.getUserDetailsOnWords().isWordMark();
            finalWordProperties.getUserDetailsOnWords().setWordMark(!isCurrentlyMarked);
            dbManager.updateIsWordMarkedBasedOnWord(this.finalWordProperties.getWordProperties().getWord(), !isCurrentlyMarked);
            imageButton.setImageResource(isCurrentlyMarked ? R.drawable.baseline_add_to_marked_words_24 : R.drawable.baseline_check_24);
            new AlertDialog.Builder(this.getContext())
                    .setMessage(isCurrentlyMarked ? "Word removed from marked words." : "Word added to marked words!")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    /**
     * Returns the {@link FinalWordProperties} associated with this button.
     *
     * @return The {@link FinalWordProperties} instance.
     */
    public FinalWordProperties getFinalWordProperties() {
        return finalWordProperties;
    }
}
