package wojtekfr.highscoretracker.util;

public class RequestReview {
    boolean shouldRequestReview = false;

    public RequestReview() {

    }

    public boolean isShouldRequestReview() {
        return shouldRequestReview;
    }

    public void setShouldRequestReview(boolean shouldRequestReview) {
        this.shouldRequestReview = shouldRequestReview;
    }
}
