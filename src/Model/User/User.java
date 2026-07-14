package src.Model.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import src.Enums.GenderType;
import src.Enums.SecurityQuestionType;


@JsonIgnoreProperties(ignoreUnknown = true)public class User {
    private String userName;
    private String nickName;
    private String password;
    private String email;
    private GenderType genderType;
    private UserProgress userProgress;
    private SecurityQuestionType securityQuestion;
    private String securityAnswer;

    public User(String userName, String nickName, String password, String email, GenderType genderType) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.genderType = genderType;
        this.userProgress = new UserProgress();
    }

    public UserProgress getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(UserProgress userProgress) {
        this.userProgress = userProgress;
    }

    public User() {
    }

    public SecurityQuestionType getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(SecurityQuestionType securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GenderType getGenderType() {
        return genderType;
    }


    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }
}
