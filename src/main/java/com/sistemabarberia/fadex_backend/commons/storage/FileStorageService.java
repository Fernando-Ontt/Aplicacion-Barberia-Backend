package com.sistemabarberia.fadex_backend.commons.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String UPLOAD_DIR = "uploads";

    /**
     * @param archivo El archivo a guardar
     * @param subCarpeta Carpeta destino (ej: "categorias", "documentos")
     * @param tiposPermitidos Lista de MimeTypes (ej: "image/jpeg", "application/vnd.ms-excel")
     */

    public String guardarArchivo(MultipartFile archivo, String subCarpeta, List<String> tiposPermitidos) {
        // 1. Validar vacío
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Archivo inválido o vacío.");
        }

        // 2. Validar formato
        String contentType = archivo.getContentType();
        if (tiposPermitidos != null && !tiposPermitidos.contains(contentType)) {
            throw new RuntimeException("Formato no permitido: " + contentType);
        }

        // 3. Generar estructura de fecha
        LocalDate ahora = LocalDate.now();
        String fechaPath = String.format("%d/%02d", ahora.getYear(), ahora.getMonthValue());

        try {
            // 4. Construir ruta física: uploads/productos/2026
            Path rutaDestino = Paths.get(UPLOAD_DIR).resolve(subCarpeta).resolve(fechaPath);

            // Crea todas las carpetas necesarias si no existen
            if (!Files.exists(rutaDestino)) {
                Files.createDirectories(rutaDestino);
            }

            // 5. Nombre único para evitar sobrescribir archivos
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nuevoNombre = UUID.randomUUID().toString() + extension;

            Path rutaCompleta = rutaDestino.resolve(nuevoNombre);
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            // 6. Retornar la ruta relativa que guardaremos en la BD
            return "/" + UPLOAD_DIR + "/" + subCarpeta + "/" + fechaPath + "/" + nuevoNombre;

        } catch (IOException e) {
            throw new RuntimeException("Error crítico al guardar el archivo: " + e.getMessage());
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        return (nombreArchivo != null && nombreArchivo.contains("."))
                ? nombreArchivo.substring(nombreArchivo.lastIndexOf("."))
                : "";
    }

    public void eliminarArchivo(String urlRelativa) {
        if (urlRelativa == null || urlRelativa.isEmpty()) return;

        try {
            // 1. Obtenemos la ruta base (Ej: "uploads")
            String directorioRaiz = "uploads";

            // 2. Limpiamos la URL que viene de la BD
            String rutaLimpia = urlRelativa.replace("/" + directorioRaiz + "/", "");
            rutaLimpia = rutaLimpia.startsWith("/") ? rutaLimpia.substring(1) : rutaLimpia;

            // 3. Combinamos Raíz + Ruta Limpia
            Path rutaCompleta = Paths.get(directorioRaiz).resolve(rutaLimpia).toAbsolutePath();

            // 4. Intento de borrado
            boolean eliminado = Files.deleteIfExists(rutaCompleta);

            if (eliminado) {
                System.out.println("Borrado físico exitoso: " + rutaCompleta);
            } else {
                System.out.println("Archivo no encontrado en: " + rutaCompleta);
            }
        } catch (IOException e) {
            System.err.println("Error al borrar: " + e.getMessage());
        }
    }
}
