public class CustomPair {
    Integer key;
    Integer value;

    CustomPair(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    CustomPair values() {
        return new CustomPair(key, value);
    }

    public Integer getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CustomPair) {
            CustomPair other = (CustomPair) o;
            if (other.getKey() == key && other.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "{" + key + ", " + value + "}";
    }
}
