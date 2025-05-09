package com.example;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/* Simulador de sensor de temperatura creado por Mariana Valle Moreno */

public class SensorSystemExample {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        // Lista fija de IDs de sensores
        List<String> sensorIds = Arrays.asList("Sensor-A", "Sensor-B", "Sensor-C");

        // Simulamos 5 lecturas de temperatura con un sensor
        Observable<Double> temperatureStream = Observable.interval(1, TimeUnit.SECONDS)
            .map(tick -> {
                // Forzar algunos valores altos para verificar funcionamiento
                if (tick == 1 || tick == 3) return 31 + random.nextDouble() * 5; 
                return 20 + random.nextDouble() * 10; // Temperatura entre 20 y 30 °C
            })
            .take(6); // Solo 6 lecturas

        // Emitimos una lista de IDs una sola vez para emparejar
        Observable<String> sensorIdStream = Observable.fromIterable(sensorIds)
                .repeat(2) // Cada sensor se repetirá 2 veces en el flujo
                .take(6); // Solo 6 IDs

        // Unimos temperatura con ID del sensor
        Observable<SensorReading> combinedStream = Observable.zip(
                sensorIdStream,
                temperatureStream,
                SensorReading::new
        );

        // Flujo secundario (Sensor-D)
        Observable<SensorReading> secondaryStream = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .map(tick -> new SensorReading("Sensor-D", 25 + random.nextDouble() * 10))
                .take(3);

        // Unimos ambos flujos
        Observable<SensorReading> mergedStream = Observable.merge(combinedStream, secondaryStream);

        // Procesamiento del flujo combinado
        mergedStream
                .filter(reading -> reading.getTemperature() > 30) // Solo alertas por temperatura alta (Mayor a 30 °C)
                .map(reading -> "[ALERTA] " + reading.getSensorId() + " reporta " +
                        String.format("%.2f", reading.getTemperature()) + " °C")
                .flatMap(SensorSystemExample::simulateAlertDelivery)
                .doOnComplete(() -> System.out.println("¡Monitoreo completado!"))
                .subscribe(System.out::println);

        // Esperar hasta que terminen los eventos
        Thread.sleep(8000);
    }

    // Simula envío de alerta con pequeño retardo (500 ms)
    private static Observable<String> simulateAlertDelivery(String alert) {
        return Observable.just("Enviando: " + alert)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation());
    }

    // Clase para representar la lectura del sensor
    static class SensorReading {
        private final String sensorId;
        private final double temperature;

        public SensorReading(String sensorId, double temperature) {
            this.sensorId = sensorId;
            this.temperature = temperature;
        }

        public String getSensorId() {
            return sensorId;
        }

        public double getTemperature() {
            return temperature;
        }
    }
}
