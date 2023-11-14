package cl.dsoto.trading.mappers;


import cl.dsoto.trading.entities.BarEntity;
import cl.dsoto.trading.entities.BarsEntity;
import cl.dsoto.trading.model.Bars;
import cl.dsoto.trading.model.TimeFrame;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.Decimal;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by root on 05-05-23.
 */
@Mapper(componentModel = "cdi")
public interface BarsMapper {

    @Mappings({
            @Mapping(target = "bars", source = "bars", qualifiedByName = "toBarEntity")
    })
    BarsEntity toEntity(Bars domain);

    @Mappings({
            @Mapping(target = "bars", source = "bars", qualifiedByName = "toBarDomain")
    })
    Bars toDomain(BarsEntity entity);

    @Named("toBarEntity")
    default BarEntity toBarEntity(Bar bar) {
        return  BarEntity.builder()
                .open(bar.getOpenPrice().doubleValue())
                .close(bar.getOpenPrice().doubleValue())
                .low(bar.getMinPrice().doubleValue())
                .high(bar.getMaxPrice().doubleValue())
                .volume(bar.getVolume().doubleValue())
                .beginTime(bar.getBeginTime().toLocalDateTime())
                .endTime(bar.getEndTime().toLocalDateTime())
                .build();
    }

    @Named("toBarDomain")
    default Bar toBarDomain(BarEntity bar) {
        return new BaseBar(
                bar.getEndTime().atZone(ZoneId.of("UTC")),
                Decimal.valueOf(bar.getOpen()),
                Decimal.valueOf(bar.getHigh()),
                Decimal.valueOf(bar.getLow()),
                Decimal.valueOf(bar.getClose()),
                Decimal.valueOf(bar.getVolume()));
    }



}
