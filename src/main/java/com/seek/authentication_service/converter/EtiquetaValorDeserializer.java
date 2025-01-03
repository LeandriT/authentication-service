package com.seek.authentication_service.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.seek.authentication_service.dto.response.infoVehicle.DataVehicleResponseDto;
import java.io.IOException;

public class EtiquetaValorDeserializer extends JsonDeserializer<DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor> {

    @Override
    public DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String value = p.getText();
        DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor etiquetaValor =
                new DataVehicleResponseDto.LsInfoPlaca.EtiquetaValor();
        etiquetaValor.setEtiqueta(value);
        return etiquetaValor;
    }
}