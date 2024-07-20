package xyz.dzmoore.examplews;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestResource {
    private Long id;
    private String name;

    @Getter(lombok.AccessLevel.PRIVATE)
    private String password;
}
