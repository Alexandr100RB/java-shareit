package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotEmpty
    @NotBlank
    private String text;
    @JsonIgnore
    private Item item;
    private String authorName;
    private LocalDateTime created;
}