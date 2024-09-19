package ru.practicum.shareit.item.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.model.Comment;

//@Mapper
//public interface CommentMapper {
//
//    @Mapping(target = "authorName", source = "author.name")
//    CommentDto toCommentDto(Comment comment);
//
//    Comment toComment(CommentDtoLittle commentDto);
//}

public class CommentMapper {

    public static CommentDto toCommentDto (Comment comment) {
        return new CommentDto(
          comment.getId(),
          comment.getText(),
          comment.getItem(),
          comment.getAuthor(),
          comment.getCreated()
        );
    }

    public static Comment toComment(CommentDtoLittle commentDto) {
        return new Comment(
          commentDto.getText(),
          commentDto.getItemId(),
          commentDto.getAuthorId()
        );
    }
}
