package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static ItemInfoDto.CommentDto toCommentDto(Comment comment) {
        return new ItemInfoDto.CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toComment(ItemInfoDto.CommentDto commentDto, User author, Item item, LocalDateTime created) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                created);
    }
}
