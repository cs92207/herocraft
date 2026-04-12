package de.christoph.herocraft.onboarding;

public enum OnBoardingStep {

    CREATE_LAND(0),
    COMPLETED(1);

    private int id;

    OnBoardingStep(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static OnBoardingStep fromId(int id) {
        for (OnBoardingStep step : values()) {
            if (step.id == id) {
                return step;
            }
        }
        throw new IllegalArgumentException("Unknown OnBoardingStep id: " + id);
    }

}
