package ru.develop.restaurantvoting.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.develop.restaurantvoting.common.model.NamedEntity;
import ru.develop.restaurantvoting.common.validation.NoHtml;

import java.util.List;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends NamedEntity {

    @Column(name = "address")
    @NotBlank
    @Size(max = 128)
    @NoHtml
    private String address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OrderBy("menuDate DESC")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<MenuItem> menuItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Vote> votes;

    public Restaurant(Integer id, String name, String address) {
        super(id, name);
        this.address = address;
    }

    @Override
    public String toString() {
        return "Restaurant:" + id + '[' + name + ']';
    }
}