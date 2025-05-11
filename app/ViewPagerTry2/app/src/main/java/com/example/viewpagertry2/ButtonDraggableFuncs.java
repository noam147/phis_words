package com.example.viewpagertry2;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import NewViews.LockableScrollView;
import NewViews.WordButton;
import files.XpPointsTracker;

public class ButtonDraggableFuncs
{

    private static final int JUST_RIGHT_CODE =OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS;
    private static final int JUST_LEFT_CODE = OperationsAndOtherUsefull.DO_KNOW_WORDS;
    private static final int RightMaxLocation = 400;
    private static final int LeftMaxLocation = -400;
    private float dX;
    private float startXLocation;
    private static boolean isBtnInProcses = false;
    //private static int lastAction =-1;
    private int lastAction =-1;//to each btn unicly
    //
    private boolean isDragging = false; // To track if dragging is in progress
    private static float initialTouchX;
    private static float initialTouchY;
    private static final int DRAG_THRESHOLD = 10; // Threshold for deciding drag vs scroll
    private Button[] m_listOfBtns = null;
    public ButtonDraggableFuncs()
    {

    }
    /*public ButtonDraggableFuncs(Button[] listOfBtns)
    {

        m_listOfBtns = listOfBtns;
        resetXOfAllButtons();
    }
    private void resetXOfAllButtons()
    {
        for (Button btn:m_listOfBtns) {
            btn.setX(startXLocation);
        }
    }*/
    //can make this static - need to see whats more efficient
    public void makeButtonDraggableOnXAxsis(WordButton btn, int currAction, DBManager dbManager, int[] buttonsAndId, LockableScrollView scrollViewOfButtons) {

        Context context= btn.getContext();
        String currentWord = btn.getFinalWordProperties().getWordProperties().getWord();
        btn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                ViewGroup parent;
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        // Initialize values for dragging
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        dX = view.getX() - event.getRawX();
                        startXLocation = view.getX();
                        lastAction = MotionEvent.ACTION_DOWN;

                        // Disable the scroll view during drag
                        scrollViewOfButtons.setScrollingEnabled(false);
                        isDragging = false; // Reset dragging state
                        // Let the system handle the long click event
                        return false; // Pass the event to the system for long click detection

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getRawX() - initialTouchX);
                        float deltaY = Math.abs(event.getRawY() - initialTouchY);

                        // Determine if the user is swiping horizontally
                        if (deltaX + 80 > deltaY && deltaX > DRAG_THRESHOLD) {
                            isDragging = true;

                            // Update the button's position during drag
                            float xCurr = event.getRawX() + dX;
                            view.setX(xCurr);

                            // Handle swipe actions (color change, etc.)
                            if (view.getX() - startXLocation > RightMaxLocation && currAction != JUST_LEFT_CODE) {
                                btn.setBackgroundColor(Color.GREEN);
                            } else if (view.getX() - startXLocation < LeftMaxLocation && currAction != JUST_RIGHT_CODE) {
                                btn.setBackgroundColor(Color.RED);
                            } else {
                                btn.setBackgroundColor(Color.WHITE);
                            }

                            lastAction = MotionEvent.ACTION_MOVE;
                            return true; // Consume the event to prevent other actions
                        }
                        else if (deltaY > deltaX && deltaY > DRAG_THRESHOLD) {
                            // Let the ScrollView handle vertical scrolling
                            btn.setBackgroundColor(Color.WHITE);
                            view.setX(startXLocation); // Reset button position
                            scrollViewOfButtons.setScrollingEnabled(true);
                            return false; // Pass the event for vertical scrolling
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        scrollViewOfButtons.setScrollingEnabled(true);

                        // Handle the button release after dragging
                        if (isDragging) {
                            if(btn.getFinalWordProperties().getUserDetailsOnWords().getAmountOfStars() == 0)
                            {
                                XpPointsTracker.addOrSubToProgressBar2(3,btn.getContext());
                                //XpPointsTracker.addOrSubToProgressBar(1,btn.getContext());
                            }
                            // Handle the swipe logic
                            if (view.getX() - startXLocation > RightMaxLocation && currAction != JUST_LEFT_CODE) {
                                dbManager.setWordAsKnowWord(currentWord);
                                int idOfRightBtn = buttonsAndId[2];
                                Button b2 = ((Activity) context).findViewById(idOfRightBtn);
                                changeBtnName(b2, true);

                                int idOfCurrentBtnToSubtract = buttonsAndId[currAction - 1];
                                Button b = ((Activity) context).findViewById(idOfCurrentBtnToSubtract);
                                changeBtnName(b, false);
                                makeAnimationOfButtonGoingRight(btn, true);
                            } else if (view.getX() - startXLocation < LeftMaxLocation && currAction != JUST_RIGHT_CODE) {
                                dbManager.setWordAsDoesNOTKnowWord(currentWord);
                                makeAnimationOfButtonGoingRight(btn, false);

                                int idOfCurrentBtn = buttonsAndId[currAction - 1];
                                Button b = ((Activity) context).findViewById(idOfCurrentBtn);
                                changeBtnName(b, false);
                                int idOfLeftBtn = buttonsAndId[0];
                                Button b2 = ((Activity) context).findViewById(idOfLeftBtn);
                                changeBtnName(b2, true);
                            } else {
                                view.setX(startXLocation); // Reset position if no swipe
                            }
                            isDragging = false; // Reset dragging state
                        } else {
                            view.setX(startXLocation); // Reset position if no drag
                        }
                        return true;

                    default:
                        return false;
                }
                return true;
            }
        });

// Adding the OnLongClickListener
        /*btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isDragging)
                {
                    return false;
                }
                // Code to handle the long press event
                Toast.makeText(v.getContext(), "Long press detected!", Toast.LENGTH_SHORT).show();
                dbManager.updateIsWordMarkedBasedOnWord(currentWord,true);
                return true; // Return true to indicate the event is handled
            }
        });*/

    }
    private static void changeBtnName(Button btn,boolean isPlus)
    {
        String finalString = "";
        String text = btn.getText().toString();
        int valueOfBtn = OperationsAndOtherUsefull.getIntInButtonText(btn);
        if(isPlus)
        {
            valueOfBtn++;
        }
        else {
            valueOfBtn--;
        }
        for(int i =0; i <text.length();i++)
        {
            if(text.charAt(i) >= '0' && text.charAt(i) <='9')
            {
                continue;
            }
            finalString+=text.charAt(i);
        }
        btn.setText(finalString+String.valueOf(valueOfBtn));
    }
    public static void makeAnimationOfButtonGoingRight(View btn, boolean isRight) {
        float endX = 1200;
        if (!isRight) {
            endX *= -1;
        }

        btn.animate()
                .x(endX)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {}
                })
                .start();
    }
}
