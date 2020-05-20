package ir.taxi1880.operatormanagement.model;

public class RewardsModel {

    private int score;
    private String comment;
    private String expireDate;
    private String expireTime;
    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getexpireTime() {
        return expireTime;
    }

    public void setexpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
