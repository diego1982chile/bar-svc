package cl.dsoto.trading.services;

import cl.dsoto.trading.daos.StockMarketDAO;
import cl.dsoto.trading.entities.BarEntity;
import cl.dsoto.trading.mappers.BarsMapper;
import cl.dsoto.trading.model.TimeFrame;
import cl.dsoto.trading.repositories.BarRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.util.Pair;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.TimeSeries;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by root on 13-10-22.
 */
@RequestScoped
@Log4j
public class BarService {

    @PersistenceContext
    private EntityManager entityManager;
    private BarRepository barRepository;

    @Inject
    private BarsMapper barsMapper;

    @Inject
    StockMarketDAO stockMarketDAO;

    @PostConstruct
    private void init() {
        // Instantiate Spring Data factory
        RepositoryFactorySupport factory = new JpaRepositoryFactory(entityManager);
        // Get an implemetation of PersonRepository from factory
        this.barRepository = factory.getRepository(BarRepository.class);
    }


    @Transactional
    public List<Bar> getBars(TimeFrame timeFrame, ZonedDateTime start, ZonedDateTime end) throws Exception {

        List<BarEntity> bars = barRepository.findBarsBy(timeFrame.getId(), start.toLocalDateTime(), end.toLocalDateTime());
        TimeSeries data;
        ZonedDateTime start_ = start;
        ZonedDateTime end_ = end;


        Optional<Pair<ZonedDateTime, ZonedDateTime>> range =  containtsBars(bars, start, end);

        if (bars.isEmpty() || range.isPresent()) {
            if (range.isPresent()) {
                start_ = range.get().getFirst();
                end_ = range.get().getSecond();
            }
            data = stockMarketDAO.getHistoricalPrice(timeFrame, start_.toLocalDate(), end_.toLocalDate());
            for (Bar bar : data.getBarData()) {
                BarEntity barEntity = barRepository.findBarBy(timeFrame.getId(), start_.toLocalDateTime(), end_.toLocalDateTime());
                if (Objects.isNull(barEntity)) {
                    barEntity = barsMapper.toBarEntity(bar);
                    barEntity.setTimeFrame(timeFrame.getId());
                    barRepository.save(barEntity);
                }
            }
            if (range.isPresent()) {
                bars = barRepository.findBarsBy(timeFrame.getId(), start.toLocalDateTime(), end.toLocalDateTime());
                data = new BaseTimeSeries(start.toString() + "-" + end.toString() + "_" + timeFrame);
                for (BarEntity barEntity : bars) {
                    Bar bar = barsMapper.toBarDomain(barEntity);
                    try {
                        data.addBar(bar);
                    }
                    catch (IllegalArgumentException e) {
                        log.warn(e.getMessage());
                    }
                }
            }
        }
        else {
            data = new BaseTimeSeries(start.toString() + "-" + end.toString() + "_" + timeFrame);
            for (BarEntity barEntity : bars) {
                Bar bar = barsMapper.toBarDomain(barEntity);
                data.addBar(bar);
            }
        }

        return data.getBarData();
    }

    private Optional<Pair<ZonedDateTime, ZonedDateTime>> containtsBars(List<BarEntity> bars, ZonedDateTime start, ZonedDateTime end) {

        if (bars.stream().map(BarEntity::getEndTime).collect(Collectors.toList()).containsAll(Arrays.asList(start,end))) {
            return Optional.empty();
        }

        if (bars.stream().map(BarEntity::getEndTime).collect(Collectors.toList()).contains(end.toLocalDateTime()) &&
                !bars.stream().map(BarEntity::getEndTime).collect(Collectors.toList()).contains(start.toLocalDateTime())) {
            return Optional.of(Pair.of(start, bars.get(0).getEndTime().atZone(ZoneId.of("UTC"))));
        }

        if (bars.stream().map(BarEntity::getEndTime).collect(Collectors.toList()).contains(start.toLocalDateTime()) &&
                !bars.stream().map(BarEntity::getEndTime).collect(Collectors.toList()).contains(end.toLocalDateTime())) {
            return Optional.of(Pair.of(bars.get(bars.size() - 1).getEndTime().atZone(ZoneId.of("UTC")), end));
        }

        return Optional.empty();
    }
}
