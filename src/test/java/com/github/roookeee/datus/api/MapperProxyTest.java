package com.github.roookeee.datus.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperProxyTest {

    private static class Category {
        private String id;
        private Category subCategory;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Category getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(Category subCategory) {
            this.subCategory = subCategory;
        }
    }

    @Test
    public void shouldProxyCorrectlyForRecursiveDataStructures() {
        //given
        Category subCategory = new Category();
        subCategory.setId("sub");
        Category category = new Category();
        category.setId("top");
        category.setSubCategory(subCategory);

        MapperProxy<Category, Category> proxy = new MapperProxy<>();
        Mapper<Category, Category> idMapper = Datus.forTypes(Category.class, Category.class)
                .mutable(Category::new)
                .from(Category::getId).into(Category::setId)
                .from(Category::getSubCategory).nullsafe().map(proxy::convert)
                    .into(Category::setSubCategory)
                .build();
        proxy.setMapper(idMapper);

        //when
        Category result = proxy.convert(category);

        assertThat(result.getId()).isEqualTo("top");
        assertThat(result.getSubCategory().getId()).isEqualTo("sub");
        assertThat(result.getSubCategory().getSubCategory()).isNull();
    }
}