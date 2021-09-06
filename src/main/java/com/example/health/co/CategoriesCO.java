package com.example.health.co;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriesCO {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("categories")
    private List<CategoryResponseCO> categories;

    public CategoriesCO(Long total, List<CategoryResponseCO> categories) {
        this.total = total;
        this.categories = categories;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<CategoryResponseCO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponseCO> categories) {
        this.categories = categories;
    }
}
