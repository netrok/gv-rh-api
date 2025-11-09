package com.gv.mx.empleados.application.impl;

import com.gv.mx.empleados.application.EmpleadoFilter;
import com.gv.mx.empleados.application.EmpleadoService;
import com.gv.mx.empleados.domain.Empleado;
import com.gv.mx.empleados.dto.EmpleadoDTO;
import com.gv.mx.empleados.infrastructure.EmpleadoRepository;
import com.gv.mx.empleados.infrastructure.EmpleadoSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static com.gv.mx.shared.utils.Pageables.safe;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository repo;

    private static final Set<String> ALLOWED_SORT = Set.of(
            "id","numEmpleado","nombres","apellidoPaterno","apellidoMaterno",
            "email","fechaIngreso","activo","createdAt",
            // si tu entidad maneja estos como scalars:
            "departamentoId","puestoId","turnoId","horarioId","supervisorId"
            // si usas relaciones y mapeas con join, usa:
            // "departamento.id","puesto.id"
    );

    @Override
    public Page<EmpleadoDTO> listar(EmpleadoFilter f, Pageable pageable) {
        Pageable p = safe(pageable, ALLOWED_SORT, "numEmpleado");
        var spec = EmpleadoSpecs.from(f);
        return repo.findAll(spec, p).map(this::toDTO);
    }

    @Override
    public EmpleadoDTO obtener(Long id) {
        Empleado e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        return toDTO(e);
    }

    @Override
    @Transactional
    public EmpleadoDTO crear(EmpleadoDTO dto) {
        Empleado e = fromDTO(new Empleado(), dto);
        if (e.getActivo() == null) e.setActivo(Boolean.TRUE);
        e = repo.save(e);
        return toDTO(e);
    }

    @Override
    @Transactional
    public EmpleadoDTO actualizar(Long id, EmpleadoDTO dto) {
        Empleado e = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        e = fromDTO(e, dto);
        e = repo.save(e);
        return toDTO(e);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado");
        }
        repo.deleteById(id);
    }

    // ====== Mapeos ======
    private EmpleadoDTO toDTO(Empleado e) {
        return EmpleadoDTO.builder()
                .id(e.getId())
                .numEmpleado(e.getNumEmpleado())
                .nombres(e.getNombres())
                .apellidoPaterno(e.getApellidoPaterno())
                .apellidoMaterno(e.getApellidoMaterno())
                .email(e.getEmail())
                .activo(e.getActivo())
                .foto(e.getFoto())

                .telefono(e.getTelefono())
                .fechaNacimiento(e.getFechaNacimiento())
                .genero(e.getGenero())
                .estadoCivil(e.getEstadoCivil())
                .curp(e.getCurp())
                .rfc(e.getRfc())
                .nss(e.getNss())
                .fechaIngreso(e.getFechaIngreso())

                .departamentoId(e.getDepartamentoId())
                .puestoId(e.getPuestoId())
                .turnoId(e.getTurnoId())
                .horarioId(e.getHorarioId())
                .supervisorId(e.getSupervisorId())

                .calle(e.getCalle())
                .numExt(e.getNumExt())
                .numInt(e.getNumInt())
                .colonia(e.getColonia())
                .municipio(e.getMunicipio())
                .estado(e.getEstado())
                .cp(e.getCp())
                .nacionalidad(e.getNacionalidad())
                .lugarNacimiento(e.getLugarNacimiento())
                .escolaridad(e.getEscolaridad())
                .tipoSangre(e.getTipoSangre())

                .contactoNombre(e.getContactoNombre())
                .contactoTelefono(e.getContactoTelefono())
                .contactoParentesco(e.getContactoParentesco())

                .bancoId(e.getBancoId())
                .cuentaBancaria(e.getCuentaBancaria())
                .clabe(e.getClabe())

                .tipoContratoId(e.getTipoContratoId())
                .tipoJornadaId(e.getTipoJornadaId())

                .fechaBaja(e.getFechaBaja())
                .motivoBajaId(e.getMotivoBajaId())

                .imssRegPatronal(e.getImssRegPatronal())
                .infonavitNumero(e.getInfonavitNumero())
                .fonacotNumero(e.getFonacotNumero())
                .build();
    }

    private Empleado fromDTO(Empleado e, EmpleadoDTO dto) {
        e.setNumEmpleado(dto.getNumEmpleado());
        e.setNombres(dto.getNombres());
        e.setApellidoPaterno(dto.getApellidoPaterno());
        e.setApellidoMaterno(dto.getApellidoMaterno());
        e.setEmail(dto.getEmail());
        if (dto.getActivo() != null) e.setActivo(dto.getActivo());
        e.setFoto(dto.getFoto());

        e.setTelefono(dto.getTelefono());
        e.setFechaNacimiento(dto.getFechaNacimiento());
        e.setGenero(dto.getGenero());
        e.setEstadoCivil(dto.getEstadoCivil());
        e.setCurp(dto.getCurp());
        e.setRfc(dto.getRfc());
        e.setNss(dto.getNss());
        e.setFechaIngreso(dto.getFechaIngreso());

        e.setDepartamentoId(dto.getDepartamentoId());
        e.setPuestoId(dto.getPuestoId());
        e.setTurnoId(dto.getTurnoId());
        e.setHorarioId(dto.getHorarioId());
        e.setSupervisorId(dto.getSupervisorId());

        e.setCalle(dto.getCalle());
        e.setNumExt(dto.getNumExt());
        e.setNumInt(dto.getNumInt());
        e.setColonia(dto.getColonia());
        e.setMunicipio(dto.getMunicipio());
        e.setEstado(dto.getEstado());
        e.setCp(dto.getCp());
        e.setNacionalidad(dto.getNacionalidad());
        e.setLugarNacimiento(dto.getLugarNacimiento());
        e.setEscolaridad(dto.getEscolaridad());
        e.setTipoSangre(dto.getTipoSangre());

        e.setContactoNombre(dto.getContactoNombre());
        e.setContactoTelefono(dto.getContactoTelefono());
        e.setContactoParentesco(dto.getContactoParentesco());

        e.setBancoId(dto.getBancoId());
        e.setCuentaBancaria(dto.getCuentaBancaria());
        e.setClabe(dto.getClabe());

        e.setTipoContratoId(dto.getTipoContratoId());
        e.setTipoJornadaId(dto.getTipoJornadaId());

        e.setFechaBaja(dto.getFechaBaja());
        e.setMotivoBajaId(dto.getMotivoBajaId());

        e.setImssRegPatronal(dto.getImssRegPatronal());
        e.setInfonavitNumero(dto.getInfonavitNumero());
        e.setFonacotNumero(dto.getFonacotNumero());

        return e;
    }
}