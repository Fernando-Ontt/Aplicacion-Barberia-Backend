package com.sistemabarberia.fadex_backend.modules.venta.comprobante.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ComprobantePdfGenerator {

    private static final DateTimeFormatter FECHA_FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // SpringTemplateEngine usa SpEL (no OGNL), por eso ya no falla con ognl/PropertyAccessor
    private final SpringTemplateEngine templateEngine;

    public ComprobantePdfGenerator() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    public byte[] generar(Venta venta) {
        try {
            Context contexto = new Context();
            contexto.setVariable("local", datosLocal());
            contexto.setVariable("venta", datosVenta(venta));

            String html = templateEngine.process("ticket-comprobante", contexto);
            ConverterProperties propiedades = new ConverterProperties();
            propiedades.setCharset("UTF-8");
            propiedades.setBaseUri(new ClassPathResource("templates/").getURL().toExternalForm());

            try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                HtmlConverter.convertToPdf(html, salida, propiedades);
                return salida.toByteArray();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo cargar la plantilla del comprobante", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el comprobante PDF", exception);
        }
    }

    private Map<String, String> datosLocal() {
        Map<String, String> local = new HashMap<>();
        local.put("ruc", "[TU RUC]");
        local.put("direccion", "[Dirección del local]");
        local.put("telefono", "[Teléfono]");
        local.put("web", "[TU WEB O @REDES]");
        return local;
    }

    private Map<String, Object> datosVenta(Venta venta) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("tipoComprobante", venta.getTipoComprobante());
        datos.put("serieComprobante", valor(venta.getSerieComprobante()));
        datos.put("numeroComprobante", valor(venta.getNumeroComprobante()));
        datos.put("fechaEmision", venta.getFechaEmision() != null
                ? venta.getFechaEmision().format(FECHA_FORMATO)
                : "-");
        datos.put("clienteNombre", nombreCliente(venta));
        datos.put("clienteDocumento", "No registrado");
        datos.put("barberoNombre", nombreBarbero(venta));
        datos.put("numeroCorrelativo", valor(venta.getNumeroCorrelativo()));
        datos.put("metodoPago", "Registrado en la venta");
        BigDecimal total = monto(venta.getTotal());
        BigDecimal subtotal = monto(venta.getSubtotal());
        BigDecimal igv = monto(venta.getIgv());
        if (total.signum() > 0 && igv.signum() == 0) {
            subtotal = total.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
            igv = total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
        }
        datos.put("subtotal", formatoMonto(subtotal));
        datos.put("igv", formatoMonto(igv));
        datos.put("total", formatoMonto(total));
        datos.put("totalEnLetras", "Importe total: S/ " + formatoMonto(total));
        datos.put("detalles", datosDetalles(venta));
        return datos;
    }

    private List<Map<String, Object>> datosDetalles(Venta venta) {
        List<Map<String, Object>> detalles = new ArrayList<>();
        List<DetalleVenta> origen = venta.getDetalles() != null
                ? venta.getDetalles()
                : List.of();

        for (DetalleVenta detalle : origen) {
            BigDecimal precio = detalle.getPrecioUnitario() != null
                    ? detalle.getPrecioUnitario()
                    : BigDecimal.ZERO;
            int cantidad = detalle.getCantidad() != null ? detalle.getCantidad() : 0;
            Map<String, Object> item = new HashMap<>();
            item.put("cantidad", cantidad);
            item.put("descripcion", descripcion(detalle));
            item.put("importe", formatoMonto(precio.multiply(BigDecimal.valueOf(cantidad))));
            detalles.add(item);
        }
        return detalles;
    }

    private String descripcion(DetalleVenta detalle) {
        if (detalle.getProducto() != null) {
            return detalle.getProducto().getNombre();
        }
        if (detalle.getServicio() != null) {
            return detalle.getServicio().getNombre();
        }
        return "Detalle de venta";
    }

    private String nombreCliente(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getPersona() == null) {
            return "Cliente general";
        }
        var persona = venta.getCliente().getPersona();
        return String.format("%s %s",
                valor(persona.getNombre()), valor(persona.getApellido())).trim();
    }

    private String nombreBarbero(Venta venta) {
        if (venta.getBarbero() == null || venta.getBarbero().getPersona() == null) {
            return "No registrado";
        }
        var persona = venta.getBarbero().getPersona();
        return String.format("%s %s",
                valor(persona.getNombre()), valor(persona.getApellido())).trim();
    }

    private String formatoMonto(BigDecimal monto) {
        // HALF_UP evita ArithmeticException si el monto trae más de 2 decimales
        return monto != null ? monto.setScale(2, RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private BigDecimal monto(BigDecimal monto) {
        return monto != null ? monto : BigDecimal.ZERO;
    }

    private String valor(String valor) {
        return valor != null && !valor.isBlank() ? valor : "-";
    }
}