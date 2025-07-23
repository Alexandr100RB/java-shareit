package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}