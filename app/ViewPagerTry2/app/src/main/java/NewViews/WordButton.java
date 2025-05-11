package NewViews;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.R;

public class WordButton extends androidx.appcompat.widget.AppCompatButton {
    private DBManager dbManager;
    private ImageButton imageButton = null;
    private ImageButton playAudioImgButton = null;
    private FinalWordProperties finalWordProperties = null;
    public WordButton(Context context,FinalWordProperties finalWordProperties,DBManager activeDbManager) {
        super(context);
        this.finalWordProperties = finalWordProperties;
        this.dbManager = activeDbManager;
    }
    //maybe onclick - get to sorted in right unit and category?
    @Override
    public void setVisibility(int visibility)
    {
        super.setVisibility(visibility);
        imageButton.setVisibility(visibility);
        playAudioImgButton.setVisibility(visibility);
        if(visibility == GONE || visibility == INVISIBLE)
        {

        }
    }
    public void setDbManager(DBManager activeDbManager)
    {
        this.dbManager = activeDbManager;
        addButtons();
    }
    public void afterAddingToLayout()
    {
        if(dbManager != null)
        {
            addButtons();
            //addbuttons2();
        }
    }
    private void addbuttons2()
    {
        //this sets the img on top - but the click function can not be achived:(
        this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_play_audio_img_24, 0, 0, 0);
    }
    private void addButtons() {
        RelativeLayout container = new RelativeLayout(getContext());


        // Make sure the parent is not null
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            //do this once!
            parent.removeView(this); // Remove WordButton from its parent
        }

        container.addView(this); // Add WordButton

        addMarked(container); // Add marked button
        add_volume_img_display(container); // Add volume button

        // Check if the parent is not null and add the new container at the same index
        if (parent != null) {
            int index = parent.indexOfChild(this);
            parent.addView(container, index);
        }
    }
    private void add_volume_img_display(RelativeLayout container)
    {
        playAudioImgButton = new ImageButton(getContext());
        playAudioImgButton.setElevation(20);//make img appear on top of the button
        playAudioImgButton.setImageResource(R.drawable.baseline_play_audio_img_24); // Replace with your drawable
        playAudioImgButton.setBackground(null); // Remove default background if needed
        // Create LayoutParams for positioning the image button
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,  // Width of the image button
                ViewGroup.LayoutParams.WRAP_CONTENT   // Height of the image button
        );

        // Center the image button on the WordButton
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //params.addRule(RelativeLayout.RIGHT_OF, this.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);



        // Add the ImageButton to the container with specified layout parameters
        container.addView(playAudioImgButton, params);
        playAudioImgButton.setOnClickListener(v -> MakeViewPlayAudio.playRecordingOfWord(this.getContext(),this.finalWordProperties.getWordProperties().getWord_id()));

        // Set click listener for the ImageButton
    }
    private void addMarked(RelativeLayout container) {
        boolean isMarked = finalWordProperties.getUserDetailsOnWords().isWordMark();
            //this.setVisibility(GONE);
            // Create a new ImageButton
        imageButton = new ImageButton(getContext());
        imageButton.setElevation(20);
        if (!isMarked) {
            // Set the image for the ImageButton
            imageButton.setImageResource(R.drawable.baseline_add_to_marked_words_24); // Replace with your drawable
        }
        else {imageButton.setImageResource(R.drawable.baseline_check_24);}

        imageButton.setBackground(null); // Remove default background if needed
            // Create LayoutParams for positioning the image button
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,  // Width of the image button
                    ViewGroup.LayoutParams.WRAP_CONTENT   // Height of the image button
            );

            // Center the image button on the WordButton
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);



            // Add the ImageButton to the container with specified layout parameters

            container.addView(imageButton, params);
            // Set click listener for the ImageButton
            imageButton.setOnClickListener(v -> {
                boolean IS_MARKED_IN_LISTER = finalWordProperties.getUserDetailsOnWords().isWordMark();
                if(!IS_MARKED_IN_LISTER)
                {
                    finalWordProperties.getUserDetailsOnWords().setWordMark(true);
                    dbManager.updateIsWordMarkedBasedOnWord(this.finalWordProperties.getWordProperties().getWord(),true);
                    imageButton.setImageResource(R.drawable.baseline_check_24); // Replace with your drawable
                    Toast.makeText(getContext(), "Word being added to marked words!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    finalWordProperties.getUserDetailsOnWords().setWordMark(false);
                    dbManager.updateIsWordMarkedBasedOnWord(this.finalWordProperties.getWordProperties().getWord(),false);
                    imageButton.setImageResource(R.drawable.baseline_add_to_marked_words_24); // Replace with your drawable
                    Toast.makeText(getContext(), "Word being removed from marked words.", Toast.LENGTH_SHORT).show();
                }
            });

        }



    public FinalWordProperties getFinalWordProperties()
    {return finalWordProperties;}
}
