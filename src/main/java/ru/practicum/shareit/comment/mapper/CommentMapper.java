package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentCreateDto commentCreateDto);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "author.name", target = "authorName")
    CommentResponseDto toCommentResponseDto(Comment comment);
}
