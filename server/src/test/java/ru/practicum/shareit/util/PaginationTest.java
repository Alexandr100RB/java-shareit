package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.*;

class PaginationTest {

    @Test
    void constructor_ShouldThrow_WhenFromIsNegative() {
        assertThatThrownBy(() -> new Pagination(-1, 10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не может быть меньше нуля");
    }

    @Test
    void constructor_ShouldThrow_WhenSizeIsNegative() {
        assertThatThrownBy(() -> new Pagination(1, -10))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("не может быть меньше нуля");
    }

    @Test
    void constructor_ShouldThrow_WhenSizeIsZero() {
        assertThatThrownBy(() -> new Pagination(1, 0))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("должно быть больше нуля");
    }

    @Test
    void constructor_ShouldSetDefaults_WhenSizeIsNull_AndFromIsZero() {
        Pagination pagination = new Pagination(0, null);
        assertThat(pagination.getPageSize()).isEqualTo(1000);
        assertThat(pagination.getIndex()).isEqualTo(0);
        assertThat(pagination.getTotalPages()).isEqualTo(0);
    }

    @Test
    void constructor_ShouldCalculateTotalPages_Correctly() {
        Pagination pagination = new Pagination(2, 5);
        assertThat(pagination.getPageSize()).isEqualTo(2);
        assertThat(pagination.getIndex()).isEqualTo(1);
        assertThat(pagination.getTotalPages()).isGreaterThanOrEqualTo(1);
    }
}
