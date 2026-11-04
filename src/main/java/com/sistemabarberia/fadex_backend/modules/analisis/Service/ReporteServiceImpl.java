package com.sistemabarberia.fadex_backend.modules.analisis.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.ResumenDiaDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.sistemabarberia.fadex_backend.modules.pagos.repository.PagoRepository;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;


@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final ReservaRepository reservaRepository;
    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;

    private static final DateTimeFormatter FMT_DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── DATOS DEL LOCAL (se muestran en la cabecera de todos los PDF)
    private static final String LOCAL_RUC       = "20612345678";
    private static final String LOCAL_DIRECCION = "Estación Bayóvar, San Juan de Lurigancho, Lima";
    private static final String LOCAL_TELEFONO  = "954 321 876";
    private static final String LOCAL_WEB       = "@fadex.barberia";

    @Override
    public List<ResumenDiaDTO> getResumenSemanal(LocalDate desde, LocalDate hasta) {
        List<Object[]> rows = reservaRepository.resumenSemanal(desde, hasta);
        return rows.stream().map(r -> new ResumenDiaDTO(
                (String) r[0],
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO
        )).toList();
    }

    // ─── PDF RESERVAS
    @Override
    public byte[] generarReservasPdf(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago) {
        List<Reserva> reservas = reservaRepository.findReservasFiltradas(
                desde, hasta, barberoId, servicioId,
                estado != null ? estado.name() : null,
                metodoPago != null ? metodoPago.name() : null
        );

        List<Reserva> finalizadas = reservas.stream().filter(ReporteServiceImpl::esFinalizada).toList();
        BigDecimal ingresos = sumar(finalizadas);
        long noConcretadas = reservas.stream().filter(ReporteServiceImpl::esNoConcretada).count();

        List<Kpi> kpis = List.of(
                new Kpi("Total reservas", String.valueOf(reservas.size()), "en el periodo"),
                new Kpi("Finalizadas", String.valueOf(finalizadas.size()),
                        porcentaje(finalizadas.size(), reservas.size()) + " del total"),
                new Kpi("Canceladas / no asistió", String.valueOf(noConcretadas),
                        porcentaje(noConcretadas, reservas.size()) + " del total"),
                new Kpi("Ingresos", soles(ingresos),
                        "ticket promedio " + soles(promedio(ingresos, finalizadas.size())))
        );

        List<String[]> porEstado = agrupar(reservas, r -> legible(estadoDe(r))).entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue().size()), soles(sumar(e.getValue()))})
                .toList();

        List<String[]> porBarbero = agrupar(reservas, ReporteServiceImpl::nombreBarbero).entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .map(e -> {
                    List<Reserva> fin = e.getValue().stream().filter(ReporteServiceImpl::esFinalizada).toList();
                    return new String[]{e.getKey(), String.valueOf(e.getValue().size()),
                            String.valueOf(fin.size()), soles(sumar(fin))};
                })
                .toList();

        List<Bloque> resumenes = List.of(
                new Bloque("Reservas por estado", new String[]{"Estado", "Cant.", "Monto"}, porEstado),
                new Bloque("Reservas por barbero", new String[]{"Barbero", "Reservas", "Finalizadas", "Ingresos"}, porBarbero)
        );

        List<Columna> columnas = List.of(
                new Columna("ID", 0.8f, false),
                new Columna("Fecha", 1.4f, false),
                new Columna("Cliente", 2.2f, false),
                new Columna("Barbero", 2.0f, false),
                new Columna("Servicio", 2.0f, false),
                new Columna("Estado", 1.5f, false),
                new Columna("Total", 1.4f, true)
        );

        List<String[]> filas = reservas.stream().map(r -> new String[]{
                String.valueOf(r.getId()),
                fecha(r.getFecha()),
                nombreCliente(r),
                nombreBarbero(r),
                nombreServicio(r),
                legible(estadoDe(r)),
                soles(monto(r.getTotal()))
        }).toList();

        return construirPdf("Reservas", desde, hasta, kpis, resumenes, columnas, filas,
                "Total ingresos", soles(ingresos),
                "Se listan " + reservas.size() + " reservas. El total de ingresos corresponde solo a las reservas finalizadas.");
    }

    // ─── EXCEL RESERVAS
    @Override
    public byte[] generarReservasExcel(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago) {
        List<Reserva> reservas = reservaRepository.findReservasFiltradas(
                desde, hasta, barberoId, servicioId,
                estado != null ? estado.name() : null,
                metodoPago != null ? metodoPago.name() : null
        );
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservas");

            XSSFCellStyle headerStyle    = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle   = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Fecha", "Cliente", "Barbero", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum);
                XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                setCellStyled(row, 0, String.valueOf(r.getId()), estilo);
                setCellStyled(row, 1, r.getFecha().toString(), estilo);
                setCellStyled(row, 2, r.getCliente() != null ? r.getCliente().getPersona().getNombre() : "Sin cliente", estilo);
                setCellStyled(row, 3, r.getBarbero() != null ? r.getBarbero().getPersona().getNombre() : "Sin barbero", estilo);
                setCellStyled(row, 4, r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio", estilo);
                setCellStyled(row, 5, r.getEstadoReserva().toString(), estilo);
                setCellStyled(row, 6, "S/ " + r.getTotal(), estilo);
                rowNum++;
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de reservas", e);
        }
    }


    // ─── PDF VENTAS (lee de la tabla VENTA, igual que Gestión de Ventas)
    @Override
    public byte[] generarVentasPdf(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = ventasDelPeriodo(desde, hasta);
        BigDecimal total = sumarVentas(ventas);

        // Ingresos por producto / servicio
        Map<String, Integer> cantidadPorItem = new LinkedHashMap<>();
        Map<String, BigDecimal> montoPorItem = new LinkedHashMap<>();
        for (Venta v : ventas) {
            if (v.getDetalles() == null) continue;
            for (DetalleVenta d : v.getDetalles()) {
                String nombre = nombreItem(d);
                int cant = d.getCantidad() != null ? d.getCantidad() : 0;
                BigDecimal precio = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO;
                cantidadPorItem.merge(nombre, cant, Integer::sum);
                montoPorItem.merge(nombre, precio.multiply(BigDecimal.valueOf(cant)), BigDecimal::add);
            }
        }

        Map.Entry<String, Integer> top = cantidadPorItem.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        List<Kpi> kpis = List.of(
                new Kpi("Total ingresos", soles(total), "periodo seleccionado"),
                new Kpi("Ventas realizadas", String.valueOf(ventas.size()), "en el periodo"),
                new Kpi("Ticket promedio", soles(promedio(total, ventas.size())), "por venta"),
                new Kpi("Más vendido", top != null ? top.getKey() : "-",
                        top != null ? top.getValue() + " unidades" : "sin ventas")
        );

        List<String[]> ingresosPorItem = montoPorItem.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> new String[]{
                        e.getKey(),
                        String.valueOf(cantidadPorItem.get(e.getKey())),
                        soles(e.getValue())})
                .toList();

        // Ingresos por método de pago
// Ingresos por método de pago
        Map<String, List<Venta>> porMetodo = ventas.stream()
                .collect(Collectors.groupingBy(this::metodoDe, TreeMap::new, Collectors.toList()));
        List<String[]> ingresosPorMetodo = porMetodo.entrySet().stream()
                .map(e -> new String[]{
                        e.getKey(),
                        String.valueOf(e.getValue().size()),
                        soles(sumarVentas(e.getValue()))})
                .toList();

        // Ingresos por día
        Map<LocalDate, List<Venta>> porDia = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getFecha().toLocalDate(), TreeMap::new, Collectors.toList()));
        List<String[]> ingresosPorDia = porDia.entrySet().stream()
                .map(e -> new String[]{e.getKey().format(FMT_DIA),
                        String.valueOf(e.getValue().size()), soles(sumarVentas(e.getValue()))})
                .toList();

        List<Bloque> resumenes = List.of(
                new Bloque("Ingresos por producto / servicio",
                        new String[]{"Producto / Servicio", "Cant.", "Ingresos"}, ingresosPorItem),
                new Bloque("Ingresos por método de pago",
                        new String[]{"Método", "Ventas", "Ingresos"}, ingresosPorMetodo),
                new Bloque("Ingresos por día",
                        new String[]{"Fecha", "Ventas", "Ingresos"}, ingresosPorDia)
        );

        List<Columna> columnas = List.of(
                new Columna("Fecha", 1.5f, false),
                new Columna("N° venta", 2.2f, false),
                new Columna("Cliente", 2.2f, false),
                new Columna("Comprobante", 2.0f, false),
                new Columna("Pago", 1.4f, false),
                new Columna("Total", 1.4f, true)
        );

        List<String[]> filas = ventas.stream().map(v -> new String[]{
                v.getFecha().toLocalDate().format(FMT_DIA),
                v.getNumeroCorrelativo() != null ? v.getNumeroCorrelativo() : "-",
                nombreClienteVenta(v),
                v.getSerieComprobante() + "-" + v.getNumeroComprobante(),
                metodoDe(v),
                soles(monto(v.getTotal()))
        }).toList();

        return construirPdf("Ventas e Ingresos", desde, hasta, kpis, resumenes, columnas, filas,
                "Total ingresos", soles(total),
                "Se consideran todas las ventas del periodo (productos y servicios).");
    }

    // ─── EXCEL VENTAS
    @Override
    public byte[] generarVentasExcel(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = ventasDelPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ventas");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "N° venta", "Cliente", "Comprobante", "Método de pago", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(rowNum);
                XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                setCellStyled(row, 0, v.getFecha().toLocalDate().format(FMT_DIA), estilo);
                setCellStyled(row, 1, v.getNumeroCorrelativo() != null ? v.getNumeroCorrelativo() : "-", estilo);
                setCellStyled(row, 2, nombreClienteVenta(v), estilo);
                setCellStyled(row, 3, v.getSerieComprobante() + "-" + v.getNumeroComprobante(), estilo);
                setCellStyled(row, 4, metodoDe(v), estilo);
                setCellStyled(row, 5, "S/ " + monto(v.getTotal()), estilo);
                rowNum++;
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de ventas", e);
        }
    }

    // ─── PDF CLIENTES
    @Override
    public byte[] generarClientesPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> lista = reservaRepository.findReservasPorPeriodo(desde, hasta).stream()
                .filter(r -> r.getCliente() != null && r.getCliente().getPersona() != null)
                .toList();

        Map<String, List<Reserva>> porCliente = agrupar(lista, ReporteServiceImpl::nombreCliente);
        BigDecimal gasto = sumar(lista.stream().filter(ReporteServiceImpl::esFinalizada).toList());
        Map.Entry<String, List<Reserva>> frecuente = porCliente.entrySet().stream()
                .max((a, b) -> Integer.compare(a.getValue().size(), b.getValue().size()))
                .orElse(null);

        String visitasPorCliente = porCliente.isEmpty() ? "-"
                : String.format(Locale.US, "%.1f por cliente", (double) lista.size() / porCliente.size());

        List<Kpi> kpis = List.of(
                new Kpi("Clientes únicos", String.valueOf(porCliente.size()), "atendidos en el periodo"),
                new Kpi("Visitas", String.valueOf(lista.size()), visitasPorCliente),
                new Kpi("Gasto total", soles(gasto), "reservas finalizadas"),
                new Kpi("Cliente más frecuente", frecuente != null ? frecuente.getKey() : "-",
                        frecuente != null ? frecuente.getValue().size() + " visitas" : "sin visitas")
        );

        List<String[]> topClientes = porCliente.entrySet().stream()
                .sorted((a, b) -> {
                    int porGasto = sumarFinalizadas(b.getValue()).compareTo(sumarFinalizadas(a.getValue()));
                    return porGasto != 0 ? porGasto
                            : Integer.compare(b.getValue().size(), a.getValue().size());
                })
                .limit(10)
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue().size()),
                        soles(sumarFinalizadas(e.getValue()))})
                .toList();

        List<Bloque> resumenes = List.of(
                new Bloque("Top 10 clientes por gasto", new String[]{"Cliente", "Visitas", "Gasto"}, topClientes)
        );

        List<Columna> columnas = List.of(
                new Columna("Cliente", 2.4f, false),
                new Columna("Fecha", 1.4f, false),
                new Columna("Servicio", 2.2f, false),
                new Columna("Estado", 1.5f, false),
                new Columna("Total", 1.4f, true)
        );

        List<String[]> filas = lista.stream().map(r -> new String[]{
                nombreCliente(r),
                fecha(r.getFecha()),
                nombreServicio(r),
                legible(estadoDe(r)),
                soles(monto(r.getTotal()))
        }).toList();

        return construirPdf("Clientes", desde, hasta, kpis, resumenes, columnas, filas,
                "Gasto total", soles(gasto),
                "El gasto total corresponde solo a las reservas finalizadas.");
    }

    // ─── EXCEL CLIENTES
    @Override
    public byte[] generarClientesExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Clientes");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Cliente", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                if (r.getCliente() != null && r.getCliente().getPersona() != null) {
                    Row row = sheet.createRow(rowNum);
                    XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                    setCellStyled(row, 0, r.getCliente().getPersona().getNombre(), estilo);
                    setCellStyled(row, 1, r.getFecha().toString(), estilo);
                    setCellStyled(row, 2, r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio", estilo);
                    setCellStyled(row, 3, r.getEstadoReserva() != null ? r.getEstadoReserva().toString() : "", estilo);
                    setCellStyled(row, 4, "S/ " + r.getTotal(), estilo);
                    rowNum++;
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de clientes", e);
        }
    }

    // ─── PDF BARBEROS
    @Override
    public byte[] generarBarberosPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> lista = reservaRepository.findReservasPorPeriodo(desde, hasta).stream()
                .filter(r -> r.getBarbero() != null && r.getBarbero().getPersona() != null)
                .toList();

        List<Reserva> finalizadas = lista.stream().filter(ReporteServiceImpl::esFinalizada).toList();
        BigDecimal ingresos = sumar(finalizadas);
        Map<String, List<Reserva>> porBarbero = agrupar(lista, ReporteServiceImpl::nombreBarbero);

        Map.Entry<String, List<Reserva>> top = porBarbero.entrySet().stream()
                .max((a, b) -> sumarFinalizadas(a.getValue()).compareTo(sumarFinalizadas(b.getValue())))
                .orElse(null);

        List<Kpi> kpis = List.of(
                new Kpi("Atenciones", String.valueOf(finalizadas.size()), "de " + lista.size() + " reservas"),
                new Kpi("Ingresos generados", soles(ingresos), "reservas finalizadas"),
                new Kpi("Promedio por atención", soles(promedio(ingresos, finalizadas.size())), "ticket promedio"),
                new Kpi("Barbero destacado", top != null ? top.getKey() : "-",
                        top != null ? soles(sumarFinalizadas(top.getValue())) : "sin atenciones")
        );

        List<String[]> ranking = porBarbero.entrySet().stream()
                .sorted((a, b) -> sumarFinalizadas(b.getValue()).compareTo(sumarFinalizadas(a.getValue())))
                .map(e -> {
                    List<Reserva> fin = e.getValue().stream().filter(ReporteServiceImpl::esFinalizada).toList();
                    return new String[]{e.getKey(), String.valueOf(e.getValue().size()),
                            String.valueOf(fin.size()), soles(sumar(fin)),
                            porcentajeMonto(sumar(fin), ingresos)};
                })
                .toList();

        List<Bloque> resumenes = List.of(
                new Bloque("Ranking de barberos", new String[]{"Barbero", "Reservas", "Atenciones", "Ingresos", "% del total"}, ranking)
        );

        List<Columna> columnas = List.of(
                new Columna("Barbero", 2.4f, false),
                new Columna("Fecha", 1.4f, false),
                new Columna("Servicio", 2.2f, false),
                new Columna("Estado", 1.5f, false),
                new Columna("Total", 1.4f, true)
        );

        List<String[]> filas = lista.stream().map(r -> new String[]{
                nombreBarbero(r),
                fecha(r.getFecha()),
                nombreServicio(r),
                legible(estadoDe(r)),
                soles(monto(r.getTotal()))
        }).toList();

        return construirPdf("Actividad de Barberos", desde, hasta, kpis, resumenes, columnas, filas,
                "Ingresos generados", soles(ingresos),
                "Las atenciones y los ingresos corresponden solo a las reservas finalizadas.");
    }

    // ─── EXCEL BARBEROS
    @Override
    public byte[] generarBarberosExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Barberos");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Barbero", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                if (r.getBarbero() != null && r.getBarbero().getPersona() != null) {
                    Row row = sheet.createRow(rowNum);
                    XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                    setCellStyled(row, 0, r.getBarbero().getPersona().getNombre(), estilo);
                    setCellStyled(row, 1, r.getFecha().toString(), estilo);
                    setCellStyled(row, 2, r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio", estilo);
                    setCellStyled(row, 3, r.getEstadoReserva() != null ? r.getEstadoReserva().toString() : "", estilo);
                    setCellStyled(row, 4, "S/ " + r.getTotal(), estilo);
                    rowNum++;
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de barberos", e);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS DEL REPORTE DE VENTAS
    // ══════════════════════════════════════════════════════════════════

    private List<Venta> ventasDelPeriodo(LocalDate desde, LocalDate hasta) {
        return ventaRepository.buscarParaReporte(
                desde.atStartOfDay(),
                hasta.plusDays(1).atStartOfDay());
    }

    private BigDecimal sumarVentas(List<Venta> ventas) {
        return ventas.stream()
                .map(v -> monto(v.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String nombreItem(DetalleVenta d) {
        if (d.getProducto() != null) return d.getProducto().getNombre();
        if (d.getServicio() != null) return d.getServicio().getNombre();
        return "Otros";
    }

    private String metodoDe(Venta v) {
        return pagoRepository.findFirstByVenta_VentaId(v.getVentaId())
                .map(p -> p.getMetodo() != null ? p.getMetodo().name() : "Sin registrar")
                .orElse("Sin registrar");
    }

    private String nombreClienteVenta(Venta v) {
        if (v.getCliente() == null || v.getCliente().getPersona() == null) return "Cliente general";
        var p = v.getCliente().getPersona();
        return ((p.getNombre() != null ? p.getNombre() : "") + " "
                + (p.getApellido() != null ? p.getApellido() : "")).trim();
    }

    // ══════════════════════════════════════════════════════════════════
    //  ESTILOS EXCEL
    // ══════════════════════════════════════════════════════════════════

    private XSSFCellStyle crearEstiloHeader(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)201, (byte)168, (byte)76}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{0, 0, 0}, null));
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle crearEstiloFilaPar(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle crearEstiloFilaImpar(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void setCellStyled(Row row, int col, String value, XSSFCellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    // ══════════════════════════════════════════════════════════════════
    //  DISEÑO FADEX PARA LOS PDF (negro + dorado, como el ticket)
    // ══════════════════════════════════════════════════════════════════

    private static final DeviceRgb NEGRO      = new DeviceRgb(12, 12, 12);
    private static final DeviceRgb DORADO     = new DeviceRgb(201, 168, 76);
    private static final DeviceRgb DORADO_OSC = new DeviceRgb(138, 109, 29);
    private static final DeviceRgb BLANCO     = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb ZEBRA      = new DeviceRgb(246, 243, 233);
    private static final DeviceRgb GRIS       = new DeviceRgb(90, 90, 90);
    private static final DeviceRgb GRIS_CLARO = new DeviceRgb(189, 189, 189);
    private static final DeviceRgb LINEA      = new DeviceRgb(201, 201, 201);
    private static final DeviceRgb TEXTO      = new DeviceRgb(17, 17, 17);

    private static final Locale ES = Locale.forLanguageTag("es-PE");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Tarjeta de resumen: etiqueta, valor grande y detalle. */
    private record Kpi(String etiqueta, String valor, String detalle) {}

    /** Columna de la tabla de detalle: título, ancho relativo y alineación. */
    private record Columna(String titulo, float peso, boolean derecha) {}

    /** Tabla de apoyo (por estado, por barbero, ranking...). La primera columna va a la izquierda. */
    private record Bloque(String titulo, String[] columnas, List<String[]> filas) {}

    /** Arma el PDF completo con el diseño de FadeX. */
    private byte[] construirPdf(String titulo, LocalDate desde, LocalDate hasta,
                                List<Kpi> kpis, List<Bloque> resumenes,
                                List<Columna> columnas, List<String[]> filas,
                                String totalEtiqueta, String totalValor, String nota) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfFont fuente = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            PieDePagina pie = new PieDePagina(fuente);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, pie);

            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(28, 28, 48, 28);
            doc.setFont(fuente);
            doc.setFontSize(9);

            doc.add(cabeceraMarca());
            doc.add(franjaDorada());
            doc.add(bloqueTitulo(titulo, desde, hasta));

            if (kpis != null && !kpis.isEmpty()) {
                doc.add(seccion("Resumen del periodo"));
                doc.add(tarjetas(kpis));
            }
            if (resumenes != null) {
                for (Bloque b : resumenes) {
                    if (!b.filas().isEmpty()) {
                        doc.add(seccion(b.titulo()));
                        doc.add(tablaResumen(b));
                    }
                }
            }

            doc.add(seccion("Detalle"));
            doc.add(tablaDetalle(columnas, filas));
            if (filas.isEmpty()) {
                doc.add(new Paragraph("No hay registros en este periodo.")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(9).setFontColor(GRIS).setMarginTop(14).setMarginBottom(14));
            }

            if (totalValor != null) {
                doc.add(cajaTotal(totalEtiqueta, totalValor));
            }
            if (nota != null && !nota.isBlank()) {
                doc.add(new Paragraph(nota).setFontSize(7).setFontColor(GRIS).setMarginTop(8).setMarginBottom(0));
            }
            doc.add(barraBase());

            pie.escribirTotal(pdf);
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException("Error generando PDF de " + titulo, e);
        }
        return out.toByteArray();
    }

    // ─── Cabecera: FADEX / BARBERÍA + datos del local, sobre fondo negro
    private Table cabeceraMarca() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{3, 2})).useAllAvailableWidth();
        t.addCell(new Cell().setBorder(Border.NO_BORDER).setBackgroundColor(NEGRO)
                .setPaddingTop(18).setPaddingBottom(14).setPaddingLeft(20)
                .add(new Paragraph("FADEX").setBold().setFontSize(28).setCharacterSpacing(4)
                        .setFontColor(DORADO).setMargin(0))
                .add(new Paragraph("BARBERÍA").setFontSize(8).setCharacterSpacing(6)
                        .setFontColor(BLANCO).setMarginTop(2).setMarginBottom(0)));
        t.addCell(new Cell().setBorder(Border.NO_BORDER).setBackgroundColor(NEGRO)
                .setPaddingTop(22).setPaddingBottom(14).setPaddingRight(20)
                .setTextAlignment(TextAlignment.RIGHT)
                .add(lineaLocal("R.U.C. " + LOCAL_RUC))
                .add(lineaLocal(LOCAL_DIRECCION))
                .add(lineaLocal(LOCAL_TELEFONO)));
        return t;
    }

    private Paragraph lineaLocal(String texto) {
        return new Paragraph(texto).setFontSize(7).setFontColor(GRIS_CLARO).setMargin(0).setMultipliedLeading(1.4f);
    }

    private Table franjaDorada() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        t.addCell(new Cell().setHeight(6).setPadding(0).setBorder(Border.NO_BORDER).setBackgroundColor(DORADO));
        return t;
    }

    // ─── Título del reporte + periodo y fecha de generación
    private Table bloqueTitulo(String titulo, LocalDate desde, LocalDate hasta) {
        Table t = new Table(UnitValue.createPercentArray(new float[]{3, 2})).useAllAvailableWidth().setMarginTop(16);
        t.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0)
                .add(new Paragraph("REPORTE").setBold().setFontSize(7.5f).setCharacterSpacing(3)
                        .setFontColor(DORADO_OSC).setMargin(0))
                .add(new Paragraph(titulo).setBold().setFontSize(17).setCharacterSpacing(0.8f)
                        .setFontColor(NEGRO).setMarginTop(1).setMarginBottom(0)));
        t.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0).setTextAlignment(TextAlignment.RIGHT)
                .add(datoMeta("Periodo: ", fecha(desde) + " al " + fecha(hasta)))
                .add(datoMeta("Generado: ", LocalDateTime.now().format(FORMATO_FECHA_HORA))));
        return t;
    }

    private Paragraph datoMeta(String etiqueta, String valor) {
        return new Paragraph()
                .add(new Text(etiqueta).setBold().setFontColor(TEXTO))
                .add(new Text(valor).setFontColor(GRIS))
                .setFontSize(8).setMargin(0).setMultipliedLeading(1.5f);
    }

    // ─── Título de sección con línea dorada
    private Paragraph seccion(String texto) {
        return new Paragraph(texto.toUpperCase(ES)).setBold().setFontSize(8).setCharacterSpacing(2)
                .setFontColor(NEGRO).setMarginTop(16).setMarginBottom(6).setPaddingBottom(2)
                .setBorderBottom(new SolidBorder(DORADO, 1.2f));
    }

    // ─── Tarjetas de resumen (negro, borde dorado a la izquierda)
    private Table tarjetas(List<Kpi> kpis) {
        int n = kpis.size();
        float[] anchos = new float[n * 2 - 1];
        for (int i = 0; i < anchos.length; i++) {
            anchos[i] = (i % 2 == 0) ? 1f : 0.06f;
        }
        Table t = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();
        for (int i = 0; i < n; i++) {
            Kpi k = kpis.get(i);
            float tamano = k.valor().length() > 13 ? 10f : 13f;
            t.addCell(new Cell().setBackgroundColor(NEGRO)
                    .setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(DORADO, 4))
                    .setPaddingTop(8).setPaddingBottom(8).setPaddingLeft(9).setPaddingRight(6)
                    .add(new Paragraph(k.etiqueta().toUpperCase(ES)).setFontSize(6).setCharacterSpacing(1)
                            .setFontColor(GRIS_CLARO).setMargin(0))
                    .add(new Paragraph(k.valor()).setBold().setFontSize(tamano)
                            .setFontColor(DORADO).setMarginTop(3).setMarginBottom(0))
                    .add(new Paragraph(k.detalle()).setFontSize(6.5f)
                            .setFontColor(BLANCO).setMarginTop(1).setMarginBottom(0)));
            if (i < n - 1) {
                t.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0));
            }
        }
        return t;
    }

    // ─── Tabla de apoyo (por estado, ranking, etc.)
    private Table tablaResumen(Bloque b) {
        float[] anchos = new float[b.columnas().length];
        for (int i = 0; i < anchos.length; i++) {
            anchos[i] = (i == 0) ? 3f : 1.4f;
        }
        Table t = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();
        for (int i = 0; i < b.columnas().length; i++) {
            t.addHeaderCell(celdaEncabezado(b.columnas()[i], i > 0));
        }
        boolean zebra = false;
        for (String[] fila : b.filas()) {
            for (int i = 0; i < fila.length; i++) {
                t.addCell(celdaFila(fila[i], i > 0, zebra, i > 0));
            }
            zebra = !zebra;
        }
        return t;
    }

    // ─── Tabla de detalle
    private Table tablaDetalle(List<Columna> columnas, List<String[]> filas) {
        float[] anchos = new float[columnas.size()];
        for (int i = 0; i < anchos.length; i++) {
            anchos[i] = columnas.get(i).peso();
        }
        Table t = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();
        for (Columna c : columnas) {
            t.addHeaderCell(celdaEncabezado(c.titulo(), c.derecha()));
        }
        boolean zebra = false;
        for (String[] fila : filas) {
            for (int i = 0; i < fila.length; i++) {
                boolean derecha = columnas.get(i).derecha();
                t.addCell(celdaFila(fila[i], derecha, zebra, derecha));
            }
            zebra = !zebra;
        }
        return t;
    }

    private Cell celdaEncabezado(String texto, boolean derecha) {
        Cell c = new Cell().setBackgroundColor(NEGRO).setBorder(Border.NO_BORDER).setPadding(6)
                .add(new Paragraph(texto.toUpperCase(ES)).setBold().setFontSize(7).setCharacterSpacing(0.8f)
                        .setFontColor(DORADO).setMargin(0));
        if (derecha) {
            c.setTextAlignment(TextAlignment.RIGHT);
        }
        return c;
    }

    private Cell celdaFila(String texto, boolean derecha, boolean zebra, boolean negrita) {
        Paragraph p = new Paragraph(texto == null ? "" : texto).setFontSize(8).setFontColor(TEXTO).setMargin(0);
        if (negrita) {
            p.setBold();
        }
        Cell c = new Cell().setBorder(Border.NO_BORDER).setBorderBottom(new DashedBorder(LINEA, 0.6f))
                .setPaddingTop(5).setPaddingBottom(5).setPaddingLeft(6).setPaddingRight(6)
                .setKeepTogether(true).add(p);
        if (zebra) {
            c.setBackgroundColor(ZEBRA);
        }
        if (derecha) {
            c.setTextAlignment(TextAlignment.RIGHT);
        }
        return c;
    }

    // ─── Caja de total (igual que el ticket)
    private Table cajaTotal(String etiqueta, String valor) {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth().setMarginTop(14);
        t.addCell(new Cell().setBackgroundColor(NEGRO)
                .setBorder(Border.NO_BORDER).setBorderLeft(new SolidBorder(DORADO, 6))
                .setPaddingTop(11).setPaddingBottom(11).setPaddingLeft(14)
                .add(new Paragraph(etiqueta.toUpperCase(ES)).setBold().setFontSize(10).setCharacterSpacing(2.5f)
                        .setFontColor(BLANCO).setMargin(0)));
        t.addCell(new Cell().setBackgroundColor(NEGRO).setBorder(Border.NO_BORDER)
                .setPaddingTop(8).setPaddingBottom(8).setPaddingRight(14)
                .setTextAlignment(TextAlignment.RIGHT)
                .add(new Paragraph(valor).setBold().setFontSize(17).setFontColor(DORADO).setMargin(0)));
        return t;
    }

    private Table barraBase() {
        Table t = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth().setMarginTop(22);
        t.addCell(new Cell().setBackgroundColor(NEGRO).setBorder(Border.NO_BORDER).setPadding(7)
                .setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(LOCAL_WEB.toUpperCase(ES)).setFontSize(7).setCharacterSpacing(2.5f)
                        .setFontColor(DORADO).setMargin(0)));
        return t;
    }

    /** Pie de cada página: línea dorada, "FADEX BARBERÍA" y "Página N de M". */
    private static class PieDePagina implements IEventHandler {
        private final PdfFormXObject totalPaginas = new PdfFormXObject(new Rectangle(0, 0, 20, 12));
        private final PdfFont fuente;

        PieDePagina(PdfFont fuente) {
            this.fuente = fuente;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int numero = pdf.getPageNumber(page);
            Rectangle tam = page.getPageSize();
            float izq = 28;
            float der = tam.getWidth() - 28;
            float y = 22;

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdf);
            pdfCanvas.setStrokeColor(DORADO).setLineWidth(0.6f).moveTo(izq, y + 12).lineTo(der, y + 12).stroke();

            Canvas canvas = new Canvas(pdfCanvas, tam);
            canvas.showTextAligned(texto("FADEX BARBERÍA"), izq, y, TextAlignment.LEFT);
            canvas.showTextAligned(texto("Página " + numero + " de"), der - 12, y, TextAlignment.RIGHT);
            canvas.close();
            pdfCanvas.addXObjectAt(totalPaginas, der - 10, y - 2);
        }

        /** Se llama una sola vez, antes de cerrar, cuando ya se sabe cuántas páginas hay. */
        void escribirTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(totalPaginas, pdf);
            canvas.showTextAligned(texto(String.valueOf(pdf.getNumberOfPages())), 0, 2, TextAlignment.LEFT);
            canvas.close();
        }

        private Paragraph texto(String s) {
            return new Paragraph(s).setFont(fuente).setFontSize(7).setFontColor(GRIS);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES DE DATOS
    // ══════════════════════════════════════════════════════════════════

    private static boolean esFinalizada(Reserva r) {
        return r.getEstadoReserva() != null && "FINALIZADA".equals(r.getEstadoReserva().toString());
    }

    private static boolean esNoConcretada(Reserva r) {
        String e = estadoDe(r);
        return e.startsWith("CANCELADA") || "NO_ASISTIO".equals(e);
    }

    private static String estadoDe(Reserva r) {
        return r.getEstadoReserva() != null ? r.getEstadoReserva().toString() : "";
    }

    /** EN_PROCESO -> "En proceso" */
    private static String legible(String estado) {
        if (estado == null || estado.isBlank()) {
            return "Sin estado";
        }
        String t = estado.replace('_', ' ').toLowerCase(ES);
        return Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }

    private static String nombreCliente(Reserva r) {
        return r.getCliente() != null && r.getCliente().getPersona() != null
                ? r.getCliente().getPersona().getNombre() : "Sin cliente";
    }

    private static String nombreBarbero(Reserva r) {
        return r.getBarbero() != null && r.getBarbero().getPersona() != null
                ? r.getBarbero().getPersona().getNombre() : "Sin barbero";
    }

    private static String nombreServicio(Reserva r) {
        return r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio";
    }

    private static Map<String, List<Reserva>> agrupar(List<Reserva> lista, Function<Reserva, String> clave) {
        return lista.stream().collect(Collectors.groupingBy(clave, LinkedHashMap::new, Collectors.toList()));
    }

    /** Convierte el total de la reserva a BigDecimal sin importar si es BigDecimal, Double, etc. */
    private static BigDecimal monto(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal b) {
            return b;
        }
        try {
            return new BigDecimal(valor.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal sumar(List<Reserva> lista) {
        return lista.stream().map(r -> monto(r.getTotal())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal sumarFinalizadas(List<Reserva> lista) {
        return sumar(lista.stream().filter(ReporteServiceImpl::esFinalizada).toList());
    }

    private static BigDecimal promedio(BigDecimal total, long cantidad) {
        return cantidad == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
    }

    private static String porcentaje(long parte, long total) {
        return total == 0 ? "0%" : Math.round(parte * 100.0 / total) + "%";
    }

    private static String porcentajeMonto(BigDecimal parte, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return "0%";
        }
        return parte.multiply(BigDecimal.valueOf(100)).divide(total, 0, RoundingMode.HALF_UP) + "%";
    }

    /** 2003.6 -> "S/ 2,003.60" */
    private static String soles(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "S/ " + formato.format(valor == null ? BigDecimal.ZERO : valor);
    }

    private static String fecha(Object f) {
        if (f == null) {
            return "-";
        }
        if (f instanceof LocalDate d) {
            return d.format(FORMATO_FECHA);
        }
        if (f instanceof LocalDateTime d) {
            return d.format(FORMATO_FECHA);
        }
        return f.toString();
    }

}