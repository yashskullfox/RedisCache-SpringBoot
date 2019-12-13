package api;

import javax.validation.constraints.NotNull;

public class PatchData {
    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @NotNull
    private Long value;
}
