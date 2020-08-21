package com.facequest.faceverification.demo.controller;

import com.facequest.faceverification.demo.model.UploadUrlResponse;
import com.facequest.faceverification.demo.model.VerificationRequest;
import com.facequest.faceverification.demo.model.VerificationRequestCreatedResponse;
import com.facequest.faceverification.demo.model.VerificationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Map;

@RestController
public class HomeController {

    @Value("${FACEQUEST_SECRET}")
    private String secret;

    @Value("${FACEQUEST_REGISTERED_EMAIL}")
    private String email;

    @Value("${FACEQUEST_URL}")
    private String baseUrl;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private Class<Map<String, Object>> genericResponseType =
            (Class<Map<String, Object>>) (Class) Map.class;

    @RequestMapping(value = "/")
    public Mono<String> initiateFaceVerification() {
        String initialResponse = "Initiating face verification request workflow";
        String UIResponse =  initialResponse + ", please check console output!";

        logger.info("*********************************************************************************************************");

        logger.info(initialResponse);

        getUrlsToUploadPhotos().subscribe(uploadUrlsResponse -> {
            logger.info("Received upload url data: " + uploadUrlsResponse);

            logger.info("Initiating request to upload reference photo...");
            uploadReferencePhoto(uploadUrlsResponse.getData().getReferencePhoto().getUploadUrl())
                    .subscribe(response -> {
                        logger.info("Successfully uploaded reference photo");
                    });


            logger.info("Initiating request to upload photo to be verified...");
            uploadPhotoToBeVerified(uploadUrlsResponse.getData().getPhotoToBeVerified().getUploadUrl())
                    .subscribe(response -> {
                        logger.info("Successfully uploaded photo to be verified");
                    });

            logger.info("Photos uploaded. Initiating verification request...");
            fireVerificationRequest(uploadUrlsResponse.getData().getReferencePhoto().getFilePath(), uploadUrlsResponse.getData().getPhotoToBeVerified().getFilePath())
                    .subscribe(verificationRequestCreatedResponse -> {
                        logger.info("Request id - " + verificationRequestCreatedResponse.getVerificationRequestId());

                        logger.info("Initiating request to check the status of verification...");

                        checkIfVerificationCompleted(verificationRequestCreatedResponse.getVerificationRequestId())
                                .delayElement(Duration.ofSeconds(4))
                                .subscribe(
                                verificationResponse -> {
                                    logger.info("Verification complete. Response is :" + verificationResponse);

                                    logger.info("Finished face verification workflow");
                                    logger.info("*********************************************************************************************************");
                                },
                                error -> logger.error("Error while fetching the result: " + error.getStackTrace().toString()));

                    }, error -> error.printStackTrace());
        }, error -> error.printStackTrace());

        return Mono.just(UIResponse);
    }

    private Mono<UploadUrlResponse> getUrlsToUploadPhotos() {
        return getWebClientWithCustomHeaders().get()
                .uri("/uploadurl")
                .retrieve().bodyToMono(UploadUrlResponse.class);
    }

    private Mono<String> uploadReferencePhoto(String url) {
        return uploadPhoto(url, "reference_photo.jpg");
    }

    private Mono<String> uploadPhotoToBeVerified(String url) {
        return uploadPhoto(url, "photo_to_be_verified.jpg");
    }

    private Mono<String> uploadPhoto(String url, String imageName) {
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(imageName).toURI())));
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.displayName());
            logger.info(MessageFormat.format("Received {0} to upload", decodedUrl));
            return getWebClient(decodedUrl)
                    .put()
                    .uri(decodedUrl)
                    .body(BodyInserters.fromValue(resource))
                    .exchange()
                    .flatMap(response -> {
                        return response.bodyToMono(String.class);
                    });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Mono<VerificationRequestCreatedResponse> fireVerificationRequest(String referencePhotoFilePath, String photoToBeVerifiedFilePath) {
        VerificationRequest request = new VerificationRequest("Some random title", "KYC verification for Mr.Obama", referencePhotoFilePath, photoToBeVerifiedFilePath);
        return getWebClientWithCustomHeaders().post()
                .uri("/")
                .body(BodyInserters.fromValue(request))
                .header("content-type", "application/json")
                .retrieve().bodyToMono(VerificationRequestCreatedResponse.class);
    }

    private Mono<VerificationResponse> checkIfVerificationCompleted(String id) {
        return getWebClientWithCustomHeaders().get()
                .uri("/" + id)
                .retrieve().bodyToMono(VerificationResponse.class);
    }

    private WebClient getWebClientWithCustomHeaders() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers ->
                {
                    headers.add("authorizationtoken", "bearer");
                    headers.add("secret", secret);
                    headers.add("email", email);
                })
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(clientResponse);
                    } else {
                        if(clientResponse.statusCode() == HttpStatus.UNAUTHORIZED){
                            logger.error("Invalid email/secret. Please check you email/secret in application.properties");
                        }
                        logger.error("Some error occurred! -> " + clientResponse.statusCode().getReasonPhrase().toString());

                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    CustomException customException = new CustomException(errorBody);
                                    customException.printStackTrace();
                                    return Mono.error(customException);
                                });
                    }
                }))
                .build();
    }

    private WebClient getWebClient(String url) {
        return WebClient.builder()
                .baseUrl(url)
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return Mono.just(clientResponse);
                    } else {
                        logger.error("Some error occurred! -> " + clientResponse.statusCode().getReasonPhrase().toString());

                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    CustomException customException = new CustomException(errorBody);
                                    customException.printStackTrace();
                                    return Mono.error(customException);
                                });
                    }
                }))
                .build();
    }

    static class CustomException extends WebClientException {

        public CustomException(String msg) {
            super(msg);
        }

        public CustomException(String msg, Throwable ex) {
            super(msg, ex);
            ex.printStackTrace();
        }
    }
}