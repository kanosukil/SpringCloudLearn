package learn.elasticsearch.entity;

/**
 * @author VHBin
 * @date 2022/7/27-17:54
 */

public class PersonDTO {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value='" + value;
    }

    public PersonDTO(String value) {
        this.value = value;
    }

    public PersonDTO() {
    }
}
