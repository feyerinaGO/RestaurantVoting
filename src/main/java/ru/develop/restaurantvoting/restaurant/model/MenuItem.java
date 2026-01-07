package ru.develop.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.develop.restaurantvoting.common.model.NamedEntity;
import ru.develop.restaurantvoting.validation.NoHtml;

import java.time.LocalDate;

@Entity
@Table(name = "menu_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"restaurant_id", "menu_date", "description"}, name = "menu_items_unique_restaurant_date_description_idx")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"restaurant"})
public class MenuItem extends NamedEntity {

    @Column(name = "menu_date", nullable = false)
    @NotNull
    private LocalDate menuDate;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 128)
    @NoHtml
    private String description;

    @Column(name = "price", nullable = false)
    @NotNull
    @Positive
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonIgnore
    private Restaurant restaurant;

    public MenuItem(Integer id, String name, LocalDate menuDate, String description, Integer price) {
        super(id, name);
        this.menuDate = menuDate;
        this.description = description;
        this.price = price;
    }

    @Schema(hidden = true)
    public Restaurant getRestaurant() {
        return restaurant;
    }
}