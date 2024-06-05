public class TourPlanner {
    String To;
    double travelTime;
    double baseScore;

    public TourPlanner(){};

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(double travelTime) {
        this.travelTime = travelTime;
    }

    public double getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(double baseScore) {
        this.baseScore = baseScore;
    }
}
