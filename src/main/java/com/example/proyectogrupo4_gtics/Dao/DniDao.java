package com.example.proyectogrupo4_gtics.Dao;

import com.example.proyectogrupo4_gtics.Entity.User;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DniDao {
    public PersonaDni buscarDatosPorDNI(String dni ){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PersonaDni> response = restTemplate.getForEntity(
                "https://api.apis.net.pe/v2/reniec/dni?numero="+dni+"&token=apis-token-8891.nCKic6UVmhldqck9sTCRzFbh2wGdR-dI", PersonaDni.class);
        return response.getBody();
    }

}



