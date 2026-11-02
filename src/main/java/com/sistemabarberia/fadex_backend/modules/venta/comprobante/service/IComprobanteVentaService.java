package com.sistemabarberia.fadex_backend.modules.venta.comprobante.service;

public interface IComprobanteVentaService {

    byte[] generarPdf(Integer ventaId);
}
