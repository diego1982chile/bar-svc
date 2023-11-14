package cl.dsoto.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Created by root on 03-05-23.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BAR",
        indexes = {@Index(name = "time_frame_index",  columnList="time_frame", unique = false)},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"begin_time", "end_time", "time_frame"})}
)
public class BarEntity extends AbstractPersistableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    double open;
    double high;
    double low;
    double close;
    double volume;

    @Column(name = "begin_time", nullable = false)
    LocalDateTime beginTime;

    @Column(name = "end_time", nullable = false)
    LocalDateTime endTime;

    @Column(name = "time_frame", nullable = false)
    double timeFrame;

    @Override
    public Long getId() {
        return id;
    }
}
