package com.seek.authentication_service.dto.response.infoVehicle;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seek.authentication_service.converter.EtiquetaValorDeserializer;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataVehicleResponseDto {

    @JsonProperty("lsInfoPlaca")
    private LsInfoPlaca lsInfoPlaca;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class LsInfoPlaca {

        @JsonProperty("SERVICIO_COD")
        private Map<String, EtiquetaValor> servicioCod;

        @JsonProperty("MATRICULA")
        private Map<String, EtiquetaValor> matricula;

        @JsonProperty("DATOSRTV")
        private List<Map<String, EtiquetaValor>> datosRtv;

        @JsonProperty("MARCA")
        private Map<String, EtiquetaValor> marca;

        @JsonProperty("CADMATRICULA")
        private Map<String, EtiquetaValor> cadMatricula;

        @JsonProperty("tipoIdent")
        private String tipoIdent;

        @JsonProperty("PLACA")
        private Map<String, EtiquetaValor> placa;

        @JsonProperty("identificacion")
        private String identificacion;

        @JsonProperty("FECHAMATRICULA")
        private Map<String, EtiquetaValor> fechaMatricula;

        @JsonProperty("ANIO")
        private Map<String, EtiquetaValor> anio;

        @JsonProperty("SERVICIO")
        private Map<String, EtiquetaValor> servicio;

        @JsonProperty("nombres")
        private String nombres;

        @JsonProperty("MODELO")
        private Map<String, EtiquetaValor> modelo;

        @JsonProperty("CLASE")
        private Map<String, EtiquetaValor> clase;

        @JsonProperty("LUGARMATRICULA")
        private Map<String, EtiquetaValor> lugarMatricula;

        @JsonProperty("PAIS")
        private Map<String, EtiquetaValor> pais;

        @JsonProperty("TIPOVEHICULO")
        private Map<String, EtiquetaValor> tipoVehiculo;

        @JsonProperty("COLOR1")
        private Map<String, EtiquetaValor> color1;

        @Data
        @JsonDeserialize(using = EtiquetaValorDeserializer.class)
        public static class EtiquetaValor {
            private String etiqueta;
            private String valor;
        }
    }

}