package src.Model.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import src.Enums.*;
import src.Model.News.News;
import src.Model.News.NewsManager;


@JsonIgnoreProperties(ignoreUnknown = true)public class User {
    private String userName;
    private String nickName;
    private String password;
    private String email;
    private NewsManager newsManager;
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
        this.newsManager = new NewsManager();
        unlockChapter(ChapterType.ANCIENT_EGYPT);
    }

    void unlockPlant(PlantType plantType){
        userProgress.unlockPlant(plantType);
        newsManager.addNews(new News("plant " + plantType.name() + " unlocked!"));
    }
    void unlockZombie(ZombieType zombieType){
        userProgress.unlockZombie(zombieType);
        newsManager.addNews(new News("zombie " + zombieType.name() + " unlocked!"));
    }
     void unlockChapter(ChapterType chapterType){
        userProgress.unlockChapter(chapterType);
        newsManager.addNews(new News("chapter " + chapterType.getName() + " unlocked!"));
        newsManager.addNews(new News("level 1 of " + chapterType.getName() + " unlocked!"));
    }
     void unlockLevel(int level, ChapterType chapterType){
        userProgress.unlockLevel(level, chapterType);
        newsManager.addNews(new News("level " + level + " of " + chapterType.getName() + " unlocked!"));
    }

    public UserProgress getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(UserProgress userProgress) {
        this.userProgress = userProgress;
    }

    public User() {
    }

    public NewsManager getNewsManager() {
        return newsManager;
    }

    public SecurityQuestionType getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(SecurityQuestionType securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    public String getUserName() {
        return userName;
    }

     void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

     void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

     void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

     void setEmail(String email) {
        this.email = email;
    }

    public GenderType getGenderType() {
        return genderType;
    }


    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }
}
