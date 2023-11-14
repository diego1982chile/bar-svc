package cl.dsoto.trading.repositories;


import cl.dsoto.trading.entities.BarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by root on 13-10-22.
 */
public interface BarRepository extends JpaRepository<BarEntity, Long> {

    BarEntity findById(long id);

    @Query("SELECT b FROM BarEntity b where b.timeFrame = :timeFrame and b.beginTime >= :start and b.endTime <= :end order by b.endTime")
    List<BarEntity> findBarsBy(@Param("timeFrame") long timeFrame, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT b FROM BarEntity b where b.timeFrame = :timeFrame and b.beginTime = :start and b.endTime = :end")
    BarEntity findBarBy(@Param("timeFrame") long timeFrame, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
