package mk.ukim.finki.petshop.model.enumerations;

public enum AnimalCategory {
    CAT("Cats"),
    DOG("Dogs"),
    BIRDS("Birds"),
    REPTILE("Reptiles"),
    SMALL_ANIMAL("Small Animals"),
    AQUATIC_ANIMAL("Aquatic Animals");
    private final String displayValue;
    private AnimalCategory(String displayValue) {
        this.displayValue = displayValue;
    }
    public String getDisplayValue() {
        return displayValue;
    }
}
