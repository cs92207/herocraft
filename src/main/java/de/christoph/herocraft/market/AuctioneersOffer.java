package de.christoph.herocraft.market;

public class AuctioneersOffer {

    private String offersName;
    private String offersUUID;
    private double offersPrice;

    public AuctioneersOffer(String offersName, String offersUUID, double offersPrice) {
        this.offersName = offersName;
        this.offersUUID = offersUUID;
        this.offersPrice = offersPrice;
    }

    public double getOffersPrice() {
        return offersPrice;
    }

    public String getOffersName() {
        return offersName;
    }

    public String getOffersUUID() {
        return offersUUID;
    }

}
