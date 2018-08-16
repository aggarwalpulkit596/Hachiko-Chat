package me.dats.com.datsme.Models;

public class UserAnswers {
    private long time;
    private String Sender;
    private String Answer;
    private boolean aprroval;
    private boolean seen;
    private String question;
    private String privacy;

//    public UserAnswers(long time, String Sender, String Answer, boolean aprroval, boolean seen, String question, String privacy) {
//        this.time = time;
//        this.Sender = Sender;
//        this.Answer = Answer;
//        this.aprroval = aprroval;
//        this.seen = seen;
//        this.question = question;
//        this.privacy = privacy;
//    }

    public long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        this.Sender = sender;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        this.Answer = answer;
    }

    public boolean isAprroval() {
        return aprroval;
    }

    public void setAprroval(boolean aprroval) {
        this.aprroval = aprroval;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
