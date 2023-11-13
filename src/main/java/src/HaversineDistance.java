package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Place implements Comparable<Place> {
    String name;
    double latitude;
    double longitude;
    double distance; // Adicionado o campo de distância

    public Place(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(double targetLatitude, double targetLongitude) {
        // Fórmula de Haversine para calcular a distância entre duas coordenadas
        double earthRadius = 6371; // raio médio da Terra em quilômetros
        double dLat = Math.toRadians(targetLatitude - this.latitude);
        double dLon = Math.toRadians(targetLongitude - this.longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(targetLatitude)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    @Override
    public int compareTo(Place other) {
        return Double.compare(this.distance, other.distance);
    }
}

public class HaversineDistance {
    public static void main(String[] args) {
        // Criar uma lista de lugares
        List<Place> places = new ArrayList<>();
        places.add(new Place("Place1", 40.7128, -74.0060));  // Exemplo de coordenadas para Nova Iorque
        places.add(new Place("Place2", 34.0522, -118.2437)); // Exemplo de coordenadas para Los Angeles
        places.add(new Place("Place3", 41.8781, -87.6298));
        places.add(new Place("Place4", -111.8781, -87.6298));// Exemplo de coordenadas para Chicago
        places.add(new Place("Place5", 221.8781, -87.6298));
        places.add(new Place("Place6", -41.8781, 87.6298));
        places.add(new Place("Place7", -24.8781, -46.6298));
        // Adicione mais lugares conforme necessário

        // Coordenadas do usuário
        double userLatitude = -24.20826 /* valor informado pelo usuário */;
        double userLongitude = -46.84641 /* valor informado pelo usuário */;

        // Calcular distâncias e classificar os lugares
        for (Place place : places) {
            double distance = place.distanceTo(userLatitude, userLongitude);
            place.distance = distance; // Adicionar a distância ao objeto Place
        }

        // Classificar os lugares com base na distância
        Collections.sort(places);

        // Obter os 5 lugares mais próximos
        List<Place> closestPlaces = places.subList(0, Math.min(5, places.size()));

        // Imprimir os 5 lugares mais próximos
        for (Place place : closestPlaces) {
            System.out.println(place.name + ": " + place.distance + " km");
        }
    }
}
