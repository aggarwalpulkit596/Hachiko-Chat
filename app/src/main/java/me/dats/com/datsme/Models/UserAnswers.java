package me.dats.com.datsme.Models;

public class UserAnswers {
    private long time;
    private String sender;
    private String answer;
    private boolean appoval;
    private boolean seen;
    private String question;
    private String privacy;

    public UserAnswers(long time, String sender, String answer, boolean appoval, boolean seen, String question, String privacy) {
        this.time = time;
        this.sender = sender;
        this.answer = answer;
        this.appoval = appoval;
        this.seen = seen;
        this.question = question;
        this.privacy = privacy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAppoval() {
        return appoval;
    }

    public void setAppoval(boolean appoval) {
        this.appoval = appoval;
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
