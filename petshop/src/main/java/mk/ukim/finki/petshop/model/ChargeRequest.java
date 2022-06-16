package mk.ukim.finki.petshop.model;

import lombok.Data;

@Data
public class ChargeRequest {
    public enum Currency {
        MKD, EUR, USD
    }
    private String description;
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String stripeToken;
}
