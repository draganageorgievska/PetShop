package mk.ukim.finki.petshop.model.enumerations;

public enum ProductCategory {
    FOOD("Food"),
    HYGIENE("Hygiene"),
    TOYS("Toys"),
    SUPPLIES("Supplies");
    private final String displayValue;
    private ProductCategory(String displayValue) {
        this.displayValue = displayValue;
    }
    public String getDisplayValue() {
        return displayValue;
    }
}
