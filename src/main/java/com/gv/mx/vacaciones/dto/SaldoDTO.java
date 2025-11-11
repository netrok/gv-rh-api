// SaldoDTO.java
package com.gv.mx.vacaciones.dto;
public record SaldoDTO(Long id, Long empleadoId, Integer anio, Integer diasAsignados, Integer diasDisfrutados, Integer diasPendientes) {}