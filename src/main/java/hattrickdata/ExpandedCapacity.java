package hattrickdata;

import core.util.HODateTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExpandedCapacity extends Capacity {

    private HODateTime expansionDate;
}
