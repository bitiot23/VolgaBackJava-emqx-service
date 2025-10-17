package com.bitiot.volga3.emqx_to_rabbit.app.Controller;

import com.bitiot.volga3.emqx_to_rabbit.app.model.CameraData;
import com.bitiot.volga3.emqx_to_rabbit.app.service.RabbitMQSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/camera-data")
public class CameraDataController {

    private final RabbitMQSenderService rabbitMQSenderService;

    public CameraDataController(RabbitMQSenderService rabbitMQSenderService){
        this.rabbitMQSenderService = rabbitMQSenderService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> receiveCameraData(@RequestBody CameraData payload){
        log.info("Recibido payload vía HTTP para ingesta manual: {}", payload);
        try {
            rabbitMQSenderService.sendCameraData(payload);
            return ResponseEntity.ok("Datos recibidos y encolados en RabbitMQ exitosamente.");
        }catch (Exception e){
            log.error("Error en el endpoint de ingesta manual", e);
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud.");
        }
    }
}
