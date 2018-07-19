package es.uca.allergio.backend.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Ingredient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToMany(mappedBy = "relatedIngredients")
    private List<Allergy> relatedAllergies;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "originalIngredient")
    private List<IngredientRowData> ingredientRowsData;

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

    public List<Allergy> getRelatedAllergies() {
        return relatedAllergies;
    }

    public void setRelatedAllergies(List<Allergy> relatedAllergies) {
        this.relatedAllergies = relatedAllergies;
    }

    public List<IngredientRowData> getIngredientRowsData() {
        return ingredientRowsData;
    }

    public void setIngredientRowsData(List<IngredientRowData> ingredientRowsData) {
        this.ingredientRowsData = ingredientRowsData;
    }
}
