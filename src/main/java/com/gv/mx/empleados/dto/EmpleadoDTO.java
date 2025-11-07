// src/main/java/com/gv/mx/empleados/dto/EmpleadoDTO.java
package com.gv.mx.empleados.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoDTO {

    private Long id;

    // ===== Básicos =====
    @NotBlank(message = "El número de empleado es obligatorio")
    @Size(max = 20, message = "El número de empleado no debe exceder 20 caracteres")
    private String numEmpleado;

    @NotBlank(message = "Los nombres son obligatorios")
    @Length(max = 120, message = "Nombres demasiado largos")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Length(max = 120, message = "Apellido paterno demasiado largo")
    private String apellidoPaterno;

    @Length(max = 120, message = "Apellido materno demasiado largo")
    private String apellidoMaterno;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 200, message = "Email demasiado largo")
    private String email;

    @NotNull(message = "El estado 'activo' es obligatorio")
    private Boolean activo;

    @Size(max = 255, message = "Ruta/URL de foto demasiado larga")
    private String foto;

    // ===== Personales =====
    @Past(message = "La fecha de nacimiento debe ser pasada")
    private LocalDate fechaNacimiento;

    @Size(max = 20)
    private String genero;

    @Size(max = 30)
    private String estadoCivil;

    // CURP (18)
    @Pattern(
            regexp =
                    "^[A-Z][AEIOU][A-Z]{2}\\d{2}" +           // 4 letras + año
                            "(0[1-9]|1[0-2])" +                       // mes
                            "(0[1-9]|[12]\\d|3[01])" +                // día
                            "[HM]" +                                  // sexo
                            "[A-Z]{2}" +                              // entidad
                            "[B-DF-HJ-NP-TV-Z]{3}" +                  // consonantes internas
                            "[0-9A-Z]\\d$",
            message = "CURP inválida"
    )
    @Size(min = 18, max = 18, message = "CURP debe tener 18 caracteres")
    private String curp;

    // RFC (12 o 13) formato SAT
    @Pattern(
            regexp = "^[A-ZÑ&]{3,4}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])[A-Z0-9]{3}$",
            message = "RFC inválido"
    )
    @Size(max = 13)
    private String rfc;

    // NSS (11 dígitos)
    @Pattern(regexp = "^\\d{11}$", message = "NSS inválido (11 dígitos)")
    private String nss;

    // Teléfono MX (10 dígitos)
    @Pattern(regexp = "^\\d{10}$", message = "Teléfono a 10 dígitos")
    private String telefono;

    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    private LocalDate fechaIngreso;

    // ===== Catálogos como IDs =====
    private Long departamentoId;
    private Long puestoId;
    private Long turnoId;
    private Long horarioId;
    private Long supervisorId;

    // ===== Dirección =====
    @Size(max = 200)
    private String calle;

    @Size(max = 20)
    private String numExt;

    @Size(max = 20)
    private String numInt;

    @Size(max = 120)
    private String colonia;

    @Size(max = 120)
    private String municipio;

    @Size(max = 120)
    private String estado;

    @Pattern(regexp = "^\\d{5}$", message = "CP debe ser de 5 dígitos")
    private String cp;

    @Size(max = 80)
    private String nacionalidad;

    @Size(max = 120)
    private String lugarNacimiento;

    @Size(max = 80)
    private String escolaridad;

    @Size(max = 10)
    private String tipoSangre;

    // ===== Contacto de emergencia =====
    @Size(max = 120)
    private String contactoNombre;

    @Pattern(regexp = "^\\d{10}$", message = "Teléfono de contacto a 10 dígitos")
    private String contactoTelefono;

    @Size(max = 60)
    private String contactoParentesco;

    // ===== Bancario =====
    private Long bancoId;

    @Size(max = 30)
    private String cuentaBancaria;

    // CLABE (18 dígitos)
    @Pattern(regexp = "^\\d{18}$", message = "CLABE debe tener 18 dígitos")
    private String clabe;

    // ===== Contrato/Jornada =====
    private Long tipoContratoId;
    private Long tipoJornadaId;

    // ===== Bajas =====
    @PastOrPresent(message = "La fecha de baja no puede ser futura")
    private LocalDate fechaBaja;

    private Long motivoBajaId;

    // ===== Otros =====
    @Size(max = 20)
    private String imssRegPatronal;

    @Size(max = 20)
    private String infonavitNumero;

    @Size(max = 20)
    private String fonacotNumero;
}
