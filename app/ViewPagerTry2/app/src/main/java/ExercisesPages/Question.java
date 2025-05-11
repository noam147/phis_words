package ExercisesPages;

import com.example.viewpagertry2.FinalWordProperties;

public class Question extends FinalWordProperties
{
    private String[] answers;
//question

    public Question(String[] answers, FinalWordProperties finalWordProperties) {
        super(finalWordProperties);
        this.answers = answers;
    }

    public String[] getAnswers() {
        return answers;
    }

    //public FinalWordProperties getWordToAsked() {
        //return finalWordProperties;
    //}
}
