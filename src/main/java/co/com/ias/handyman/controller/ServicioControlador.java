package co.com.ias.handyman.controller;

import co.com.ias.handyman.domain.Servicio;
import co.com.ias.handyman.domain.Tecnico;
import co.com.ias.handyman.model.ErrorMessage;
import co.com.ias.handyman.services.ServicioServicio;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping(value = "/servicio")
public class ServicioControlador {
    private final ServicioServicio servicioServicio;

    public ServicioControlador(ServicioServicio servicioServicio) {
        this.servicioServicio = servicioServicio;
    }


    @GetMapping
    public ResponseEntity<List<Servicio>> listaServicios() {
        List<Servicio> servicios = servicioServicio.obtenerTodoServicios();
        if (servicios == null ) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(servicios);
    }

    @GetMapping(value = "/{idServicio}")
    public ResponseEntity<Servicio> obtenerUnServicio(@PathVariable("idServicio") String idServicio){
        Servicio servicioBD = servicioServicio.obtenerUnServicio(idServicio);
        if(servicioBD == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(servicioBD);
    }

    @PostMapping
    public ResponseEntity<Servicio> guardarServicio(@Valid @RequestBody Servicio servicio, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }
        Servicio servicioBD = servicioServicio.guardarServicio(servicio);
        return ResponseEntity.ok(servicioBD);
    }



    private String formatMessage(BindingResult result) {
        List<Map<String, String>> messages = result.getFieldErrors().stream()
                .map( err -> {
                    Map<String, String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                }).collect(Collectors.toList());
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code("01")
                .messages(messages).build();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

}
