package com.github.roookeee.datus.api;


import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class MapperProxyTest {

    private static class Category {
        private String id;
        private Category subScategory;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Category getSubScategory() {
            return subScategory;
        }

        public void setSubScategory(Category subScategory) {
            this.subScategory = subScategory;
        }
    }

    @Test
    public void shouldProxyCorrectlyForRecursiveDataStructures() {
        //given
        Category subCategory = new Category();
        subCategory.setId("sub");
        Category category = new Category();
        category.setId("top");
        category.setSubScategory(subCategory);

        MapperProxy<Category, Category> proxy = new MapperProxy<>();
        Mapper<Category, Category> idMapper = Datus.forTypes(Category.class, Category.class)
                .mutable(Category::new)
                .from(Category::getId).into(Category::setId)
                .from(Category::getSubScategory)
                    .given(Objects::nonNull, proxy::convert).orElseNull()
                    .into(Category::setSubScategory)
                .build();
        proxy.setMapper(idMapper);

        //when
        Category result = proxy.convert(category);

        assertThat(result.getId()).isEqualTo("top");
        assertThat(result.getSubScategory().getId()).isEqualTo("sub");
        assertThat(result.getSubScategory().getSubScategory()).isNull();
    }
}