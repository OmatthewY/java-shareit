package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;

public class CommentMapper {

    public static Comment toComment(CommentCreateDto commentCreateDto) {
        if (commentCreateDto == null) {
            return null;
        }
        return Comment.builder()
                .text(commentCreateDto.getText())
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem() != null ? comment.getItem().getId() : null)
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .build();
    }
}
