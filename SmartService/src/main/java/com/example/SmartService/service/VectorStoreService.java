package com.example.SmartService.service;

import com.example.SmartService.dto.MediaAnalysisResponse;
import com.example.SmartService.dto.MultipleMediaAnalysisRequest;
import com.example.SmartService.model.MediaAnalysisStoreEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final VectorStore vectorStore;
    private final MediaAnalysisService mediaAnalysisService;

    public void storeVectorAnalysis(MediaAnalysisStoreEvent event) {

        String summaryText = String.join(". ", event.getSummary());

        String detailsText = event.getDetails().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        String content = String.format(
                "Topic : %s Details : %s Summary : %s",
                event.getTopic(),
                detailsText,
                summaryText
        );


        Map<String, Object> metadata = new HashMap<>();
        metadata.put("mediaId", event.getMediaId());
        metadata.put("uploadedBy", event.getUploadedBy());


        Document document = new Document(content, metadata);

        // Step 4: Store in pgvector
        // Internally: Spring AI calls EmbeddingModel → converts content to vector → stores in DB
        try {
            vectorStore.add(List.of(document));
            log.info("Stored vector for mediaId: {} by user: {}", event.getMediaId(), event.getUploadedBy());
        } catch (Exception e) {
            log.error("========================================================================");
            log.error("   GEMINI EMBEDDING OR PGVECTOR STORAGE ERROR");
            log.error("   Failed to vectorize and store media analysis for mediaId: {}", event.getMediaId());
            log.error("   Error Message: {}", e.getMessage());
            log.error("   ------------------------------------------------------------------");
            log.error("   Please troubleshoot the following:");
            log.error("   1. Check if your GEMINI_API_KEY is valid and has not hit rate limits.");
            log.error("   2. Ensure the 'vector' extension is enabled in your Postgres database.");
            log.error("   3. Verify that the 'vector_store' table has the correct dimensions (768).");
            log.error("========================================================================");
            // We catch and swallow the exception here to prevent Spring Kafka from 
            // retrying the message 10 times and exhausting your Gemini API quota.
        }
    }

    public List<MediaAnalysisResponse> semanticSearch(String query) {
        log.info("Performing semantic search for query: '{}'", query);

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );

        if(documents.isEmpty()){
            return List.of();
        }

        List<String> mediaIds = documents.stream()
                .map(doc -> (String) doc.getMetadata().get("mediaId"))
                .collect(Collectors.toList());

        MultipleMediaAnalysisRequest requestBody = MultipleMediaAnalysisRequest.builder()
                .mediaId(mediaIds)
                .build();

        return mediaAnalysisService.getAnalysis(requestBody);


    }
}
