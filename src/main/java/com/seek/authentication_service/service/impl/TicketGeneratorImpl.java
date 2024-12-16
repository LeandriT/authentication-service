package com.seek.authentication_service.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.seek.authentication_service.dto.response.VehicleResponse;
import com.seek.authentication_service.exceptions.GenericException;
import com.seek.authentication_service.service.TicketGenerator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketGeneratorImpl implements TicketGenerator {

    @Override


    public void generateTicket(VehicleResponse vehicleResponse) {
        String outputPath = "ticket.pdf";
        try (PdfWriter writer = new PdfWriter(outputPath); PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            String qrContent = vehicleResponse.getPlate(); // Contenido del QR
            String licensePlate = vehicleResponse.getPlate();
            String entry = "1";
            String date = this.formatDate(vehicleResponse.getParkingDate());

            // Generar QR
            BufferedImage qrImage = generateQRCodeImage(qrContent);

            // Guardar QR temporalmente
            File tempFile = new File("qr.png");
            ImageIO.write(qrImage, "png", tempFile);

            // Crear PDF

            // Ajustar márgenes pequeños para simular un ticket angosto
            document.setMargins(10, 20, 10, 20);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

// Crear un texto con la fuente en negrita
            Text boldText = new Text(vehicleResponse.getLocation().getName()).setFont(boldFont).setFontSize(14);

            // Agregar encabezado
            document.add(new Paragraph(boldText).setTextAlignment(TextAlignment.CENTER));


            document.add(new Paragraph("Sistema Parking Control - www.onlycontrol.com").setTextAlignment(
                    TextAlignment.CENTER).setFontSize(10));

            // Agregar datos principales
            document.add(new Paragraph("FECHA: " + date).setTextAlignment(TextAlignment.LEFT).setFontSize(10));
            document.add(new Paragraph("PLACA: " + licensePlate).setTextAlignment(TextAlignment.LEFT).setFontSize(10));
            document.add(new Paragraph("ENTRY: " + entry).setTextAlignment(TextAlignment.LEFT).setFontSize(10));

            // Agregar QR
            Image qr = new Image(ImageDataFactory.create(tempFile.getAbsolutePath()));
            qr.setWidth(100).setHeight(100).setTextAlignment(TextAlignment.CENTER);
            document.add(qr);

            // Agregar contenido adicional
            document.add(new Paragraph(qrContent).setTextAlignment(TextAlignment.CENTER).setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY));

            // Agregar notas
            document.add(new Paragraph(
                    "1. NO OLVIDE LA UBICACION DE SU PARQUEO, PRESENTE TICKET EN LA SALIDA.").setTextAlignment(
                    TextAlignment.LEFT).setFontSize(8));
            document.add(new Paragraph(
                    "2. NO NOS RESPONSABILIZAMOS POR SINIESTROS O HURTOS QUE AFECTEN SU VEHICULO.").setTextAlignment(
                    TextAlignment.LEFT).setFontSize(8));

            // Cerrar documento

            // Eliminar QR temporal
            if (tempFile.delete()) {
                log.info("archivo eliminado correctamente");
            }
            log.info("Ticket generado en {}", outputPath);
        } catch (IOException | com.itextpdf.kernel.exceptions.PdfException e) {
            log.error("Error al generar el ticket: ", e);
            throw new GenericException("Error al generar ticket catch 1: ", e);
        } catch (WriterException e) {
            throw new GenericException("Error al generar ticket catch 2: ", e);
        }
    }

    private static BufferedImage generateQRCodeImage(String text) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Crear un mapa de sugerencias
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Margen opcional para el QR

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 100, 100, hints);

        // Convertir el BitMatrix a una imagen
        BufferedImage qrImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return qrImage;
    }

    private String formatDate(LocalDateTime localDateTime) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

        String formattedDate = localDateTime.format(formatter);
        return formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
    }
}
