package ru.yandex.practikum.dto;

import java.util.ArrayList;

// описание сущности которая будет передаваться
public class OrderRequest {

    ArrayList<String> ingredients;

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
