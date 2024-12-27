package com.seek.authentication_service.service.impl;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.seek.authentication_service.dto.response.DailyTransactionSummaryDto;
import com.seek.authentication_service.dto.response.VehicleTransactionLineDto;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.service.PdfFileGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor

public class PdfFileGeneratorImpl implements PdfFileGenerator {

    @Override
    public byte[] totalToDay(DailyTransactionSummaryDto dailyTransactionSummaryDto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Fuentes y colores
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            Color headerColor = ColorConstants.LIGHT_GRAY;
            Color alternateRowColor = new DeviceRgb(240, 240, 240);

            // Encabezado principal
            document.add(new Paragraph("Easy Parking CA")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            document.add(new Paragraph(dailyTransactionSummaryDto.getLocation())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Usuario
            document.add(new Paragraph("Usuario: " + dailyTransactionSummaryDto.getUser())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));

            document.add(new Paragraph("Tarifa $: " + dailyTransactionSummaryDto.getRate())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginBottom(10));

            // Resumen de transacciones
            document.add(new Paragraph("Resumen de Parqueo")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            if (dailyTransactionSummaryDto.getTotals().isEmpty()) {
                document.add(new Paragraph("No se encontraron transacciones para la fecha especificada.")
                        .setFontSize(12)
                        .setFontColor(ColorConstants.RED)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(10));
            } else {
                // Ajuste de tabla para llenar toda la hoja
                float[] columnWidths = {1, 2, 2, 1, 1, 1}; // Proporción relativa de las columnas
                Table table = new Table(columnWidths).useAllAvailableWidth().setMarginTop(10);

// Encabezados de tabla con fondo
                table.addHeaderCell(new Cell().add(new Paragraph("PLACA").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addHeaderCell(new Cell().add(new Paragraph("NOMBRES").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addHeaderCell(new Cell().add(new Paragraph("TELEFONO").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addHeaderCell(new Cell().add(new Paragraph("TIEMPO H").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addHeaderCell(new Cell().add(new Paragraph("CALCULADO $").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));
                table.addHeaderCell(new Cell().add(new Paragraph("COBRADO $").setFont(boldFont))
                        .setBackgroundColor(headerColor)
                        .setTextAlignment(TextAlignment.CENTER));

// Filas de datos con fondo alternado
                boolean alternate = false;
                for (VehicleTransactionLineDto line : dailyTransactionSummaryDto.getTotals()) {
                    Color rowColor = alternate ? alternateRowColor : ColorConstants.WHITE;
                    table.addCell(new Cell().add(new Paragraph(line.getPlate()).setFont(regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(line.getFullName()).setFont(regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(new Paragraph(line.getPhoneNumber()).setFont(regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));

                    table.addCell(new Cell().add(
                                    new Paragraph(this.convertMinutesToReadableFormat(line.getParkedTime())).setFont(
                                            regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(
                                    new Paragraph(line.getAmountCalculated().toString()).setFont(regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell().add(
                                    new Paragraph(line.getAmountCharged().toString()).setFont(regularFont))
                            .setBackgroundColor(rowColor)
                            .setTextAlignment(TextAlignment.CENTER));
                    alternate = !alternate;
                }
                // Agregar fila de total
                Cell totalLabelCell = new Cell(1, 4) // Fila con colspan de 3
                        .add(new Paragraph("TOTAL $").setFont(boldFont).setTextAlignment(TextAlignment.RIGHT))
                        .setBackgroundColor(headerColor);
                table.addCell(totalLabelCell);

                Cell totalCalculatedValueCell = new Cell() // Celda para el valor del total
                        .add(new Paragraph(dailyTransactionSummaryDto.getTotalCalculated().toString())
                                .setFont(boldFont)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(headerColor);


                Cell totalChargedValueCell = new Cell() // Celda para el valor del total
                        .add(new Paragraph(dailyTransactionSummaryDto.getTotalCharged().toString())
                                .setFont(boldFont)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(headerColor);


                table.addCell(totalCalculatedValueCell);
                table.addCell(totalChargedValueCell);

                document.add(table);
            }

            document.close();
            log.info("PDF generado correctamente.");
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error al generar el PDF: ", e);
            throw new GenericException("Error al generar el reporte", e);
        }
    }

    String convertMinutesToReadableFormat(long minutes) {
        // Calcular días, horas y minutos
        long days = minutes / (24 * 60); // Un día tiene 24 horas * 60 minutos
        long remainingMinutesAfterDays = minutes % (24 * 60);

        long hours = remainingMinutesAfterDays / 60; // Una hora tiene 60 minutos
        long remainingMinutes = remainingMinutesAfterDays % 60;

        // Construir el resultado
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" d").append(days > 1 ? "s" : "").append(", ");
        }
        if (hours > 0 || days > 0) {
            result.append(hours).append("h ");
        }
        result.append(remainingMinutes).append("m");

        return result.toString();
    }
}
