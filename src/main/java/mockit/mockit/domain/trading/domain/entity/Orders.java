package mockit.mockit.domain.trading.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mockit.mockit.domain.model.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Orders")
@Getter
@Setter
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String stockCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderSide side;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderType type;

    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal qty;

    @Column(nullable = false)
    private BigDecimal filledQty;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String clientOrderId;

    private LocalDateTime filledAt;

    public enum OrderSide { BUY, SELL }
    public enum OrderType { MARKET, LIMIT }
    public enum OrderStatus { OPEN, PARTIAL, FILLED, CANCELED }
}
