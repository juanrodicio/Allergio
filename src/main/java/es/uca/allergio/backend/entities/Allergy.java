package es.uca.allergio.backend.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Allergy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "allergies")
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "allergy_ing",
    joinColumns = @JoinColumn(name = "allergy_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "ingredient_id", referencedColumnName = "id"))
    private List<Ingredient> relatedIngredients;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Ingredient> getRelatedIngredients() {
        return relatedIngredients;
    }

    public void setRelatedIngredients(List<Ingredient> relatedIngredients) {
        this.relatedIngredients = relatedIngredients;
    }
}
