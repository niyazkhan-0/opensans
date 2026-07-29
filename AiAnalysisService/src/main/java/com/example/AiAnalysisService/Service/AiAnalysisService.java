package com.example.AiAnalysisService.Service;

import com.example.AiAnalysisService.Model.MediaAnalysisRequest;
import com.example.AiAnalysisService.Model.MediaUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAnalysisService {

    private final ChatModel chatModel;
    private final WebClient genericWebClient;


    public Mono<MediaAnalysisRequest> getAnalysis(MediaUploadedEvent uploadedEvent) {

        BeanOutputConverter<MediaAnalysisRequest> converter = new BeanOutputConverter<>(MediaAnalysisRequest.class);

        String preSignedUrl = uploadedEvent.getPreSignedUrl();
        String mimeType = uploadedEvent.getContentType();
        String promptText = generatePrompt(converter);

        return downloadFile(preSignedUrl)
                .map(fileBytes -> {
                    Media media = new Media(
                            MimeTypeUtils.parseMimeType(uploadedEvent.getContentType()),
                            new ByteArrayResource(fileBytes)
                    );

                    UserMessage userMessage = new UserMessage.Builder()
                            .text(promptText)
                            .media(media)
                            .build();

                    log.info("sending info to gemini flash model");

                    ChatResponse chatResponse = chatModel.call(
                            new Prompt(userMessage)
                    );

                    String aiResponse = Objects.requireNonNull(chatResponse.getResult()).getOutput().getText();

                    log.info(aiResponse);

                    assert aiResponse != null;
                    String cleanedJson = aiResponse.trim();
                    if(cleanedJson.startsWith("```")){
                        cleanedJson.replaceAll("(?s)^```(?:json)?|```$", "").trim();
                    }

                    return converter.convert(cleanedJson);

                })
                .onErrorResume(e ->{
                    log.error("failed to fetch the data", e);
                    return Mono.error(new RuntimeException("failed to get analysis" + e.getMessage())) ;
                });

    }

    private String generatePrompt(BeanOutputConverter<MediaAnalysisRequest> converter) {
        return """
                Analyze the uploaded media file and provide a structured JSON response.
               
                Extract the following details:
                1. Identify a general "topic" or category name for the media content.
                2. Provide detailed key-value metadata in "details" (e.g. detected objects, activities, sentiments, transcript keywords, colors, duration etc.).
                3. A list of 3 to 5 concise observation points in the "summary".
               
                You must format your response exactly according to this schema:
                {format}
                """.replace("{format}", converter.getFormat());

    }

    private Mono<byte[]> downloadFile(String preSignedUrl) {
        return genericWebClient.get()
                .uri(URI.create(preSignedUrl) )
                .retrieve()
                .bodyToMono(byte[].class);
    }


}
