package com.sistemabarberia.fadex_backend.modules.venta.comprobante.generator;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ComprobantePdfGenerator {

    private static final DateTimeFormatter FECHA_FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(Venta venta) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(salida);
            PdfDocument pdf = new PdfDocument(writer);
            Document documento = new Document(pdf);

            String tipo = venta.getTipoComprobante() != null
                    ? venta.getTipoComprobante().name()
                    : "COMPROBANTE";
            String numero = String.format("%s-%s",
                    venta.getSerieComprobante(), venta.getNumeroComprobante());

            documento.add(new Paragraph("FadeX - Barbería")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));
            documento.add(new Paragraph(tipo)
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));
            documento.add(new Paragraph(numero)
                    .setTextAlignment(TextAlignment.CENTER));

            Table datos = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();
            String fechaEmision = venta.getFechaEmision() != null
                    ? venta.getFechaEmision().format(FECHA_FORMATO)
                    : "-";
            agregarDato(datos, "Fecha", fechaEmision);
            agregarDato(datos, "Cliente", nombreCliente(venta));
            agregarDato(datos, "Método de pago", "Registrado en la venta");
            documento.add(datos);
            documento.add(new Paragraph(" "));

            Table detalle = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}))
                    .useAllAvailableWidth();
            agregarEncabezado(detalle, "Descripción");
            agregarEncabezado(detalle, "Cant.");
            agregarEncabezado(detalle, "P. unit.");
            agregarEncabezado(detalle, "Importe");

            List<DetalleVenta> detalles = venta.getDetalles() != null
                    ? venta.getDetalles()
                    : List.of();
            for (DetalleVenta item : detalles) {
                String descripcion = item.getProducto() != null
                        ? item.getProducto().getNombre()
                        : item.getServicio() != null
                        ? item.getServicio().getNombre()
                        : "Detalle de venta";
                BigDecimal precio = item.getPrecioUnitario() != null
                        ? item.getPrecioUnitario()
                        : BigDecimal.ZERO;
                int cantidad = item.getCantidad() != null ? item.getCantidad() : 0;
                BigDecimal importe = precio.multiply(BigDecimal.valueOf(cantidad));

                detalle.addCell(descripcion);
                detalle.addCell(String.valueOf(cantidad));
                detalle.addCell(monto(precio));
                detalle.addCell(monto(importe));
            }
            documento.add(detalle);

            Table totales = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(45))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
            agregarDato(totales, "Subtotal", monto(venta.getSubtotal()));
            agregarDato(totales, "IGV", monto(venta.getIgv()));
            agregarDato(totales, "Total", monto(venta.getTotal()));
            documento.add(totales);

            documento.add(new Paragraph("Comprobante interno no válido para SUNAT.")
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(24));

            documento.close();
            return salida.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el comprobante PDF", exception);
        }
    }

    private void agregarEncabezado(Table tabla, String texto) {
        tabla.addHeaderCell(new Cell()
                .add(new Paragraph(texto).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
    }

    private void agregarDato(Table tabla, String etiqueta, String valor) {
        tabla.addCell(new Cell().add(new Paragraph(etiqueta).setBold()));
        tabla.addCell(new Cell().add(new Paragraph(valor != null ? valor : "-")));
    }

    private String nombreCliente(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getPersona() == null) {
            return "Cliente general";
        }
        var persona = venta.getCliente().getPersona();
        return String.format("%s %s",
                persona.getNombre() != null ? persona.getNombre() : "",
                persona.getApellido() != null ? persona.getApellido() : "").trim();
    }

    private String monto(BigDecimal valor) {
        return "S/ " + (valor != null ? valor : BigDecimal.ZERO);
    }
}
