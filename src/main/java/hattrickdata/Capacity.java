package hattrickdata;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Capacity {

    private int terraces;
    private int basic;
    private int roof;
    private int vip;
    private int total;
}
