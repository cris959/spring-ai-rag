package com.ai_ollama.spring_ai_rag.ingestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    @Value("classpath:/pdf/spring-boot-reference.pdf")
    private Resource resource;
    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args)  {
        log.info("Iniciando lectura del PDF...");
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        TextSplitter textSplitter = new TokenTextSplitter();

        log.info("Procesando y dividiendo el contenido en chunks...");
        List<Document> documents = textSplitter.split(reader.read());

        log.info("Total de chunks generados: {}", documents.size());

        if (!documents.isEmpty()) {
            log.info("Guardando fragmentos en la base de datos vectorial...");
            vectorStore.accept(documents);
            log.info("¡Completada la ingesta del archivo PDF!");
        } else {
            log.warn("No se encontraron documentos o fragmentos para guardar.");
        }
    }
}