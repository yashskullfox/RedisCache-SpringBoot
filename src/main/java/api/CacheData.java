package api;

import javax.validation.constraints.NotNull;

public class CacheData {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    private String type;
    @NotNull private Long value;
}
