public class Landmark {
    String name;
    double visitorLoad;
    double personalInterest;

    public Landmark() { }

    public String getName() {
        return name;
    }

    public double getPersonalInterest() {
        return personalInterest;
    }

    public void setPersonalInterest(double personalInterest) {
        this.personalInterest = personalInterest;
    }

    public double getVisitorLoad() {
        return visitorLoad;
    }

    public void setVisitorLoad(double visitorLoad) {
        this.visitorLoad = visitorLoad;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Landmark(String name, double baseScore, double travelTime, double visitorLoad, double personalInterest) {
        this.name = name;
        this.visitorLoad = visitorLoad;
        this.personalInterest = personalInterest;
    }

}
