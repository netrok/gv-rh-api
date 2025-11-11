package com.gv.mx.vacaciones.application;

import com.gv.mx.calendario.infrastructure.FeriadoRepo;
import com.gv.mx.calendario.domain.Feriado;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BusinessDayCalculator {

    private final FeriadoRepo feriadoRepo;

    public BusinessDayCalculator(FeriadoRepo feriadoRepo) {
        this.feriadoRepo = feriadoRepo;
    }

    public long countWorkingDays(LocalDate start, LocalDate endInclusive) {
        if (endInclusive.isBefore(start)) return 0;
        Set<LocalDate> feriados = feriadoRepo
                .findByFechaBetween(start, endInclusive)
                .stream()
                .map(Feriado::getFecha)
                .collect(Collectors.toSet());

        long count = 0;
        for (LocalDate d = start; !d.isAfter(endInclusive); d = d.plusDays(1)) {
            if (isWorkingDay(d) && !feriados.contains(d)) count++;
        }
        return count;
    }

    private boolean isWorkingDay(LocalDate d) {
        DayOfWeek w = d.getDayOfWeek();
        return w != DayOfWeek.SATURDAY && w != DayOfWeek.SUNDAY;
    }
}
