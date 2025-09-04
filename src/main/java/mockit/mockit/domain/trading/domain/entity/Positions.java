package mockit.mockit.domain.trading.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Positions")
@Getter
@Setter
@IdClass(PositionId.class)
public class Positions {

    @Id
    private Long memberId;

    @Id
    private String stockCode;

    @Column(nullable = false)
    private BigDecimal qty;

    @Column(nullable = false)
    private BigDecimal avgPrice;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
