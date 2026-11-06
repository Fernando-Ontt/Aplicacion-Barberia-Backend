package com.sistemabarberia.fadex_backend.modules.venta.comprobante.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.sistemabarberia.fadex_backend.modules.pagos.repository.PagoRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
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

    private static final DateTimeFormatter SOLO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter SOLO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final SpringTemplateEngine templateEngine;
    private final PagoRepository pagoRepository;

    // Constructor con inyección de dependencias
    public ComprobantePdfGenerator(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;

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

            // Datos del local
            contexto.setVariable("local", datosLocal());

            // Consultar el método de pago registrado para esta venta
            String metodoPago = pagoRepository
                    .findFirstByVenta_VentaId(venta.getVentaId())
                    .map(pago -> pago.getMetodo() != null
                            ? pago.getMetodo().name()
                            : "No registrado")
                    .orElse("No registrado");

            // Datos de la venta, incluyendo el método de pago
            contexto.setVariable("venta", datosVenta(venta, metodoPago));

            String html = templateEngine.process("ticket-comprobante", contexto);

            ConverterProperties propiedades = new ConverterProperties();
            propiedades.setCharset("UTF-8");
            propiedades.setBaseUri(
                    new ClassPathResource("templates/").getURL().toExternalForm()
            );

            try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
                HtmlConverter.convertToPdf(html, salida, propiedades);
                return salida.toByteArray();
            }

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo cargar la plantilla del comprobante",
                    exception
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No se pudo generar el comprobante PDF",
                    exception
            );
        }
    }

    private Map<String, String> datosLocal() {
        Map<String, String> local = new HashMap<>();

        local.put("ruc", "20612345678");
        local.put("direccion", "Av. de la Marina 2274, San Miguel 15088, Perú");
        local.put("telefono", "954 321 876");

        // Página web
        local.put("web", "www.fadexbarberia.com");

        // Redes sociales
        local.put("instagram", "@fadex.barberia");
        local.put("facebook", "FadeX Barbería");
        local.put("tiktok", "@fadex.barberia");

        return local;
    }

    private Map<String, Object> datosVenta(Venta venta, String metodoPago) {
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

        // Método de pago obtenido desde la tabla pago
        datos.put("metodoPago", metodoPago);

        BigDecimal total = monto(venta.getTotal());
        BigDecimal subtotal = monto(venta.getSubtotal());
        BigDecimal igv = monto(venta.getIgv());

        if (total.signum() > 0 && igv.signum() == 0) {
            subtotal = total.divide(
                    new BigDecimal("1.18"),
                    2,
                    RoundingMode.HALF_UP
            );

            igv = total.subtract(subtotal)
                    .setScale(2, RoundingMode.HALF_UP);
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

        // Se busca la reserva asociada una sola vez (null si es venta de tienda)
        Reserva reserva = pagoRepository
                .findFirstByVenta_VentaId(venta.getVentaId())
                .map(pago -> pago.getReserva())
                .orElse(null);

        for (DetalleVenta detalle : origen) {
            BigDecimal precio = detalle.getPrecioUnitario() != null
                    ? detalle.getPrecioUnitario()
                    : BigDecimal.ZERO;

            int cantidad = detalle.getCantidad() != null
                    ? detalle.getCantidad()
                    : 0;

            Map<String, Object> item = new HashMap<>();

            item.put("cantidad", cantidad);
            item.put("descripcion", descripcion(detalle, reserva));
            item.put("importe", formatoMonto(
                    precio.multiply(BigDecimal.valueOf(cantidad))
            ));

            detalles.add(item);
        }

        return detalles;
    }

    private String descripcion(DetalleVenta detalle, Reserva reserva) {
        if (detalle.getProducto() != null) {
            return detalle.getProducto().getNombre();
        }

        if (detalle.getServicio() != null) {
            String nombreServicio = detalle.getServicio().getNombre();

            // Si la venta vino de una reserva: servicio + fecha y hora de la cita
            if (reserva != null
                    && reserva.getFecha() != null
                    && reserva.getHoraInicio() != null) {

                return nombreServicio + " - "
                        + reserva.getFecha().format(SOLO_FECHA) + " "
                        + reserva.getHoraInicio().format(SOLO_HORA);
            }

            return nombreServicio;
        }

        return "Detalle de venta";
    }

    private String nombreCliente(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getPersona() == null) {
            return "Cliente general";
        }

        var persona = venta.getCliente().getPersona();

        return String.format("%s %s",
                valor(persona.getNombre()),
                valor(persona.getApellido())
        ).trim();
    }

    private String nombreBarbero(Venta venta) {
        if (venta.getBarbero() == null || venta.getBarbero().getPersona() == null) {
            return "No registrado";
        }

        var persona = venta.getBarbero().getPersona();

        return String.format("%s %s",
                valor(persona.getNombre()),
                valor(persona.getApellido())
        ).trim();
    }

    private String formatoMonto(BigDecimal monto) {
        return monto != null
                ? monto.setScale(2, RoundingMode.HALF_UP).toPlainString()
                : "0.00";
    }

    private BigDecimal monto(BigDecimal monto) {
        return monto != null ? monto : BigDecimal.ZERO;
    }

    private String valor(String valor) {
        return valor != null && !valor.isBlank() ? valor : "-";
    }
}