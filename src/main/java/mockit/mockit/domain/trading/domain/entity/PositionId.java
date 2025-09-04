package mockit.mockit.domain.trading.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PositionId implements Serializable {
    private Long memberId;
    private String stockCode;
}
