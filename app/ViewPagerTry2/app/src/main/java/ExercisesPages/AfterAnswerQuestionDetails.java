package ExercisesPages;

import com.example.viewpagertry2.FinalWordProperties;

import java.io.Serializable;

public class AfterAnswerQuestionDetails implements Serializable {
    private FinalWordProperties questionDetails;
    private boolean isUserRight;

    public AfterAnswerQuestionDetails(FinalWordProperties questionDetails, boolean isUserRight) {
        this.questionDetails = questionDetails;
        this.isUserRight = isUserRight;
    }

    public FinalWordProperties getQuestionDetails() {
        return questionDetails;
    }

    public boolean getIsUserRight() {
        return isUserRight;
    }
    //private int[] userAnswers;
    //private float[] timeForAnswer;//not must

}
