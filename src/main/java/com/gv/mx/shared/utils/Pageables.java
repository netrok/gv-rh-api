// src/main/java/com/gv/mx/shared/utils/Pageables.java
package com.gv.mx.shared.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Pageables {
    private Pageables() {}

    /**
     * Devuelve un Pageable con sort filtrado a propiedades permitidas.
     * Si no hay sort válido, usa defaultProp asc.
     */
    public static Pageable safe(Pageable p, Set<String> allowedProps, String defaultProp) {
        if (p == null) {
            return PageRequest.of(0, 20, Sort.by(defaultProp).ascending());
        }

        // Filtra órdenes inválidas (propiedades no permitidas)
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order o : p.getSort()) {
            if (o != null && allowedProps.contains(o.getProperty())) {
                orders.add(o);
            }
        }

        Sort sort = orders.isEmpty()
                ? Sort.by(defaultProp).ascending()
                : Sort.by(orders);

        int page = Math.max(0, p.getPageNumber());
        int size = p.getPageSize() > 0 ? p.getPageSize() : 20;

        return PageRequest.of(page, size, sort);
    }

    /**
     * Overload por si prefieres pasar un Sort por defecto directamente.
     */
    public static Pageable safe(Pageable p, Set<String> allowedProps, Sort defaultSort) {
        if (p == null) {
            return PageRequest.of(0, 20, defaultSort == null ? Sort.unsorted() : defaultSort);
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order o : p.getSort()) {
            if (o != null && allowedProps.contains(o.getProperty())) {
                orders.add(o);
            }
        }

        Sort sort = orders.isEmpty()
                ? (defaultSort == null ? Sort.unsorted() : defaultSort)
                : Sort.by(orders);

        int page = Math.max(0, p.getPageNumber());
        int size = p.getPageSize() > 0 ? p.getPageSize() : 20;

        return PageRequest.of(page, size, sort);
    }
}