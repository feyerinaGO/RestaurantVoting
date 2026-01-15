package ru.develop.restaurantvoting.common.to;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.develop.restaurantvoting.common.validation.NoHtml;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NamedTo extends BaseTo {
    @NotBlank
    @Size(min = 2, max = 64)
    @NoHtml
    protected String name;

    public NamedTo(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }
}