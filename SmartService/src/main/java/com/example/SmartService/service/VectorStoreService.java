package com.example.SmartService.service;

import com.example.SmartService.model.MediaAnalysisStoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;

    public void storeVectorAnalysis(MediaAnalysisStoreEvent event) {

        // Step 1: Build a natural language text from the analysis
        // This is what gets converted into a vector — quality of text = quality of search
        String summaryText = String.join(". ", event.getSummary() );

        String detailsText = event.getDetails().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        String content = String.format(
                "Topic : %s Details : %s Summary : %s",
                event.getTopic(),
                detailsText,
                summaryText
        );

        // Step 2: Attach metadata — used for filtering and retrieving the actual media
        Map<String, Object> metadata = Map.of(
                "mediaId",    event.getMediaId(),
                "uploadedBy", event.getUploadedBy()
        );

        // Step 3: Create a Spring AI Document (content + metadata)
        Document document = new Document(content, metadata);

        // Step 4: Store in pgvector
        // Internally: Spring AI calls EmbeddingModel → converts content to vector → stores in DB
        vectorStore.add(List.of(document));

        log.info("Stored vector for mediaId: {} by user: {}", event.getMediaId(), event.getUploadedBy());
    }
}

