package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDtoWithResponses {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoForRequest> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class ItemDtoForRequest {
        private Long id;
        @NotBlank(groups = {Create.class})
        private String name;
        @NotBlank(groups = {Create.class})
        private String description;
        private Boolean available;
        private Long requestId;
    }
}




