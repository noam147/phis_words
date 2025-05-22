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

/**
 * This class provides functionality to make buttons draggable horizontally
 * and handle actions based on the swipe direction. It is specifically designed
 * for {@link WordButton}s within a learning application to categorize words
 * as known or unknown.
 */
public class ButtonDraggableFuncs {

    private static final int JUST_RIGHT_CODE = OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS;
    private static final int JUST_LEFT_CODE = OperationsAndOtherUsefull.DO_KNOW_WORDS;
    private static final int RightMaxLocation = 400;
    private static final int LeftMaxLocation = -400;
    private float dX;
    private float startXLocation;
    private static boolean isBtnInProcses = false;
    private int lastAction = -1;
    private boolean isDragging = false;
    private static float initialTouchX;
    private static float initialTouchY;
    private static final int DRAG_THRESHOLD = 10;
    private Button[] m_listOfBtns = null;

    /**
     * Default constructor for the {@code ButtonDraggableFuncs} class.
     */
    public ButtonDraggableFuncs() {
        // Empty constructor
    }

    /**
     * Makes a given {@link WordButton} draggable along the X-axis. When dragged
     * beyond certain thresholds, it triggers actions to categorize the associated
     * word as known or unknown in the database.
     *
     * @param btn               The {@link WordButton} to make draggable.
     * @param currAction        The current action code associated with the button's category
     * (e.g., {@link #JUST_RIGHT_CODE} or {@link #JUST_LEFT_CODE}).
     * @param dbManager         An instance of {@link DBManager} to interact with the database.
     * @param buttonsAndId      An array of integers representing the IDs of relevant buttons.
     * It is expected to contain IDs related to the "know word" and
     * "does not know word" categories, as well as the ID of the
     * current button's category. The order is important and assumed
     * to be [ID_LEFT_BUTTON, ID_CURRENT_BUTTON, ID_RIGHT_BUTTON].
     * @param scrollViewOfButtons The {@link LockableScrollView} that contains the button.
     * This is used to disable scrolling during the drag operation.
     */
    public void makeButtonDraggableOnXAxsis(WordButton btn, int currAction, DBManager dbManager, int[] buttonsAndId, LockableScrollView scrollViewOfButtons) {
        Context context = btn.getContext();
        String currentWord = btn.getFinalWordProperties().getWordProperties().getWord();
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        dX = view.getX() - event.getRawX();
                        startXLocation = view.getX();
                        lastAction = MotionEvent.ACTION_DOWN;
                        scrollViewOfButtons.setScrollingEnabled(false);
                        isDragging = false;
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getRawX() - initialTouchX);
                        float deltaY = Math.abs(event.getRawY() - initialTouchY);
                        if (deltaX + 80 > deltaY && deltaX > DRAG_THRESHOLD) {
                            isDragging = true;
                            float xCurr = event.getRawX() + dX;
                            view.setX(xCurr);
                            if (view.getX() - startXLocation > RightMaxLocation && currAction != JUST_LEFT_CODE) {
                                btn.setBackgroundColor(Color.GREEN);
                            } else if (view.getX() - startXLocation < LeftMaxLocation && currAction != JUST_RIGHT_CODE) {
                                btn.setBackgroundColor(Color.RED);
                            } else {
                                btn.setBackgroundColor(Color.WHITE);
                            }
                            lastAction = MotionEvent.ACTION_MOVE;
                            return true;
                        } else if (deltaY > deltaX && deltaY > DRAG_THRESHOLD) {
                            btn.setBackgroundColor(Color.WHITE);
                            view.setX(startXLocation);
                            scrollViewOfButtons.setScrollingEnabled(true);
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        scrollViewOfButtons.setScrollingEnabled(true);
                        if (isDragging) {
                            if (btn.getFinalWordProperties().getUserDetailsOnWords().getAmountOfStars() == 0) {
                                XpPointsTracker.addOrSubToProgressBar2(3, btn.getContext());
                            }
                            if (view.getX() - startXLocation > RightMaxLocation && currAction != JUST_LEFT_CODE) {
                                dbManager.setWordAsKnowWord(currentWord);
                                Button rightBtn = ((Activity) context).findViewById(buttonsAndId[2]);
                                changeBtnName(rightBtn, true);
                                Button currentBtnSubtract = ((Activity) context).findViewById(buttonsAndId[currAction - 1]);
                                changeBtnName(currentBtnSubtract, false);
                                makeAnimationOfButtonGoingRight(btn, true);
                            } else if (view.getX() - startXLocation < LeftMaxLocation && currAction != JUST_RIGHT_CODE) {
                                dbManager.setWordAsDoesNOTKnowWord(currentWord);
                                makeAnimationOfButtonGoingRight(btn, false);
                                Button currentBtn = ((Activity) context).findViewById(buttonsAndId[currAction - 1]);
                                changeBtnName(currentBtn, false);
                                Button leftBtn = ((Activity) context).findViewById(buttonsAndId[0]);
                                changeBtnName(leftBtn, true);
                            } else {
                                view.setX(startXLocation);
                            }
                            isDragging = false;
                        } else {
                            view.setX(startXLocation);
                        }
                        return true;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    /**
     * Changes the text of a given {@link Button} by either incrementing or
     * decrementing the numerical value found within its text. The non-numerical
     * parts of the text are preserved.
     *
     * @param btn     The {@link Button} whose text needs to be updated.
     * @param isPlus  A boolean indicating whether to increment ({@code true})
     * or decrement ({@code false}) the numerical value.
     */
    private static void changeBtnName(Button btn, boolean isPlus) {
        String finalString = "";
        String text = btn.getText().toString();
        int valueOfBtn = OperationsAndOtherUsefull.getIntInButtonText(btn);
        if (isPlus) {
            valueOfBtn++;
        } else {
            valueOfBtn--;
        }
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= '0' && text.charAt(i) <= '9') {
                continue;
            }
            finalString += text.charAt(i);
        }
        btn.setText(finalString + String.valueOf(valueOfBtn));
    }

    /**
     * Animates a given {@link View} (typically a button) moving off-screen to the
     * right or left, and then sets its visibility to {@link View#GONE} after the
     * animation completes.
     *
     * @param btn     The {@link View} to animate.
     * @param isRight A boolean indicating the direction of the animation.
     * {@code true} for moving to the right, {@code false} for moving to the left.
     */
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
                    public void onAnimationStart(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btn.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {
                    }
                })
                .start();
    }
}
