package com.gv.mx.empleados.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_empleado", length = 20, nullable = false)
    private String numEmpleado;

    @Column(length = 120, nullable = false)
    private String nombres;

    @Column(name = "apellido_paterno", length = 120, nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 120)
    private String apellidoMaterno;

    @Column(length = 200, nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean activo;

    @Column(length = 255)
    private String foto;

    // Personales / Identificadores
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String genero;

    @Column(name = "estado_civil", length = 30)
    private String estadoCivil;

    @Column(length = 18)
    private String curp;

    @Column(length = 13)
    private String rfc;

    @Column(length = 15)
    private String nss;

    @Column(length = 30)
    private String telefono;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    // Catálogos como IDs (por ahora)
    @Column(name = "departamento_id")
    private Long departamentoId;

    @Column(name = "puesto_id")
    private Long puestoId;

    @Column(name = "turno_id")
    private Long turnoId;

    @Column(name = "horario_id")
    private Long horarioId;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    // Dirección
    @Column(length = 200)
    private String calle;
    @Column(name = "num_ext", length = 20)
    private String numExt;
    @Column(name = "num_int", length = 20)
    private String numInt;
    @Column(length = 120)
    private String colonia;
    @Column(length = 120)
    private String municipio;
    @Column(length = 120)
    private String estado;
    @Column(length = 10)
    private String cp;

    @Column(length = 80)
    private String nacionalidad;
    @Column(name = "lugar_nacimiento", length = 120)
    private String lugarNacimiento;
    @Column(length = 80)
    private String escolaridad;
    @Column(name = "tipo_sangre", length = 10)
    private String tipoSangre;

    // Contacto de emergencia
    @Column(name = "contacto_nombre", length = 120)
    private String contactoNombre;
    @Column(name = "contacto_telefono", length = 30)
    private String contactoTelefono;
    @Column(name = "contacto_parentesco", length = 60)
    private String contactoParentesco;

    // Bancario
    @Column(name = "banco_id")
    private Long bancoId;
    @Column(name = "cuenta_bancaria", length = 30)
    private String cuentaBancaria;
    @Column(length = 18)
    private String clabe;

    // Contrato/Jornada
    @Column(name = "tipo_contrato_id")
    private Long tipoContratoId;
    @Column(name = "tipo_jornada_id")
    private Long tipoJornadaId;

    // Bajas
    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;
    @Column(name = "motivo_baja_id")
    private Long motivoBajaId;

    // Otros
    @Column(name = "imss_reg_patronal", length = 20)
    private String imssRegPatronal;
    @Column(name = "infonavit_numero", length = 20)
    private String infonavitNumero;
    @Column(name = "fonacot_numero", length = 20)
    private String fonacotNumero;
}
