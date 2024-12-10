package com.seek.authentication_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInfoDto {

    @JsonProperty("fechaUltimaMatricula")
    private Long fechaUltimaMatricula;

    @JsonProperty("fechaCaducidadMatricula")
    private Long fechaCaducidadMatricula;

    @JsonProperty("cantonMatricula")
    private String cantonMatricula;

    @JsonProperty("fechaRevision")
    private Long fechaRevision;

    @JsonProperty("total")
    private Double total;

    @JsonProperty("informacion")
    private String informacion;

    @JsonProperty("estadoAuto")
    private String estadoAuto;

    @JsonProperty("mensajeMotivoAuto")
    private String mensajeMotivoAuto;

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("camvCpn")
    private String camvCpn;

    @JsonProperty("cilindraje")
    private Double cilindraje;

    @JsonProperty("fechaCompra")
    private Long fechaCompra;

    @JsonProperty("anioUltimoPago")
    private Integer anioUltimoPago;

    @JsonProperty("marca")
    private String marca;

    @JsonProperty("modelo")
    private String modelo;

    @JsonProperty("anioModelo")
    private Integer anioModelo;

    @JsonProperty("paisFabricacion")
    private String paisFabricacion;

    @JsonProperty("clase")
    private String clase;

    @JsonProperty("servicio")
    private String servicio;

    @JsonProperty("tipoUso")
    private String tipoUso;

    @JsonProperty("deudas")
    private Double deudas;

    @JsonProperty("tasas")
    private Double tasas;

    @JsonProperty("remision")
    private Double remision;
}