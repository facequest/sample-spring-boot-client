package com.facequest.faceverification.demo.controller;

import com.facequest.faceverification.demo.config.RestURIConstants;
import com.facequest.faceverification.demo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class HomeController {

    @Value("${FACEQUEST_SECRET}")
    private String secret;

    @Value("${FACEQUEST_REGISTERED_EMAIL}")
    private String email;

    @Value("${FACEQUEST_VERIFICATION_URL}")
    private String baseVerificationUrl;

    @Value("${FACEQUEST_URL}")
    private String baseAPIUrl;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private Class<Map<String, Object>> genericResponseType =
            (Class<Map<String, Object>>) (Class) Map.class;

    @RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<html><title>FaceQuest Java Test Client</title>" +
                "<body><h3>Initiate test verification request</h3>" +
                "<ul><li><a href='/verificationwithstorage'>Verification With Storage</a> </li>" +
                "<li><a href='/verificationwithoutstorage'>Verification Without Storage</a></li></ul>" +
                "</body></html>";
    }

    @RequestMapping(value = "/verificationwithstorage")
    public Mono<String> initiateFaceVerificationWithStorage() {
        String initialResponse = "Initiating face verification request workflow with Cloud storage";
        String UIResponse = initialResponse + ", please check console output!";

        logger.info("*********************************************************************************************************");

        logger.info(initialResponse);


        getUrlsToUploadPhotos().subscribe(uploadUrlsResponse -> {
            logger.info("Received upload url data: " + uploadUrlsResponse);
            logger.info("Initiating create folder to upload reference photo to reuse...");
            // create a folder and then upload reference photo onto it
            createFolder(UUID.randomUUID()).subscribe(
                    foldersResponse -> {
                        List<Folder> folders = foldersResponse.get_embedded();
                        String folderId = folders.get(0).getId();
                        logger.info("Created folder with Id -> " + folderId);

                        String referencePhotoToFolderUploadUrl = MessageFormat.format(RestURIConstants.POST_PHOTO_TO_FOLDER, folderId);
                        Mono<PhotoToFolderResponse> uploadToFolderMono = uploadPhotoIntoFolder(referencePhotoToFolderUploadUrl).subscribeOn(Schedulers.parallel());
                        Mono<String> photoToBeVerifiedMono = uploadPhotoToBeVerified(uploadUrlsResponse.getData().getPhotoToBeVerified().getUploadUrl())
                                .subscribeOn(Schedulers.parallel());

                        logger.info("Initiating reference photo & photo to be verified upload...");
                        // We execute upload reference photo to folder and photo to be verified to S3 directly
                        // in parallel and once that is completed the results are zipped and returned as tuple2
                        Mono.zip(uploadToFolderMono, photoToBeVerifiedMono.defaultIfEmpty(""))
                                .subscribe(zippedTuple2Response -> {

                                    logger.info("Photo uploads complete...");
                                    // Use the result of the previous call to api and use to initiate verification
                                    fireVerificationRequest(
                                            zippedTuple2Response.getT1().getId(),
                                            uploadUrlsResponse.getData().getPhotoToBeVerified().getFilePath(), "true")
                                            .subscribe(verificationRequestCreatedResponse -> {
                                                logger.info("Request id - " + verificationRequestCreatedResponse.getVerificationRequestId());

                                                logger.info("Initiating request to check the status of verification, waiting for the response...");
                                                // We created the verification request with the expected request body/params
                                                // and now gonna check the status of the issued verification
                                                checkIfVerificationCompleted(verificationRequestCreatedResponse.getVerificationRequestId())
                                                        .delaySubscription(Duration.ofSeconds(10))
                                                        //.repeat(5)
                                                        .subscribe(
                                                                verificationResponse -> {
                                                                    logger.info("Verification complete. Response is :" + verificationResponse);

                                                                    logger.info("Finished face verification workflow");
                                                                    logger.info("*********************************************************************************************************");
                                                                },
                                                                error -> logger.error("Error while fetching the result: " + error.getStackTrace()));
                                            }, error -> error.printStackTrace());
                                });
                    }, error -> error.printStackTrace());
        }, error -> error.printStackTrace());

        return Mono.just(UIResponse);
    }

    @RequestMapping(value = "/verificationwithoutstorage")
    public Mono<String> initiateFaceVerificationWithoutStorage() {
        String initialResponse = "Initiating face verification request workflow without Cloud storage";
        String UIResponse = initialResponse + ", please check console output!";

        logger.info("*********************************************************************************************************");
        logger.info(initialResponse);

        getUrlsToUploadPhotos().subscribe(uploadUrlsResponse -> {
            logger.info("Received upload url data: " + uploadUrlsResponse);
            logger.info("Initiating request to upload reference photo & photo to be verified...");

            Mono<String> referencePhotoMono = uploadReferencePhoto(uploadUrlsResponse.getData().getReferencePhoto().getUploadUrl()).subscribeOn(Schedulers.parallel());

            Mono<String> photoToBeVerifiedMono = uploadPhotoToBeVerified(uploadUrlsResponse.getData().getPhotoToBeVerified().getUploadUrl()).subscribeOn(Schedulers.parallel());

            // We execute upload reference photo & photo to be verified to S3 directly
            // in parallel and once that is completed we gonna issue verification request
            Mono.when(referencePhotoMono, photoToBeVerifiedMono)
                    .then(
                            // Issuing verification request
                            fireVerificationRequest(
                                    uploadUrlsResponse.getData().getReferencePhoto().getFilePath(),
                                    uploadUrlsResponse.getData().getPhotoToBeVerified().getFilePath(), "false"))
                    .subscribe(verificationRequestCreatedResponse -> {
                        logger.info("Request id - " + verificationRequestCreatedResponse.getVerificationRequestId());
                        logger.info("Initiating request to check the status of verification...");

                        // We created the verification request with the expected request body/params
                        // and now gonna check the status of the issued verification
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

    private Mono<FoldersResponse> createFolder(UUID randomFolderId) {
        logger.info("Creating folder named -> " + randomFolderId);

        // Folder details after create API call
        return getWebClientWithCustomHeaders().post()
                .uri(RestURIConstants.CREATE_FOLDERS + randomFolderId)
                .exchange()
                .flatMap(response -> response.bodyToMono(FoldersResponse.class));
    }

    private Mono<UploadUrlResponse> getUrlsToUploadPhotos() {
        return getWebClientWithCustomHeadersForVerification().get()
                .uri(RestURIConstants.GET_UPLOADURL_PATH)
                .retrieve().bodyToMono(UploadUrlResponse.class);
    }

    private Mono<String> uploadReferencePhoto(String url) {
        return uploadPhoto(url, RestURIConstants.REFERENCE_PHOTO_NAME);
    }

    private Mono<String> uploadPhotoToBeVerified(String url) {
        return uploadPhoto(url, RestURIConstants.PHOTO_TO_BE_VERIFIED_NAME);
    }

    private Mono<String> uploadPhoto(String url, String imageName) {
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(imageName).toURI())));
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.displayName());
            logger.info(MessageFormat.format("Received URL - {0} to upload {1}", decodedUrl, imageName));
            return getWebClient(decodedUrl)
                    .put()
                    .uri(decodedUrl)
                    .body(BodyInserters.fromValue(resource))
                    .exchange()
                    .flatMap(response -> response.bodyToMono(String.class));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Mono<PhotoToFolderResponse> uploadPhotoIntoFolder(String url) {
        try {
            logger.info("Initiating reference photo upload to folder...");

            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part(RestURIConstants.REFERENCE_PHOTO_NAME, Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(RestURIConstants.REFERENCE_PHOTO_NAME).toURI())))
                    .header("Content-Disposition", "form-data; name=file; filename=" +
                            RestURIConstants.REFERENCE_PHOTO_NAME)
                    .contentType(MediaType.parseMediaType("image/jpeg"));

            Mono<PhotoToFolderResponse> uploadResponse = getWebClientWithCustomHeaders()
                    .post()
                    .uri(url)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .exchange()
                    .flatMap(response -> response.bodyToMono(PhotoToFolderResponse.class));

            logger.info(MessageFormat.format("Reference photo upload to folder successful -> {0}", url));

            return uploadResponse;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return Mono.empty();
    }

    private Mono<VerificationRequestCreatedResponse> fireVerificationRequest(String referencePhotoFilePath, String photoToBeVerifiedFilePath, String useStoredReferencePhoto) {
        String title = "Verification without storage from java test client";
        if (Boolean.valueOf(useStoredReferencePhoto)) {
            title = "Verification with storage from java test client";
        }
        VerificationRequest request = new VerificationRequest(title, "KYC verification for Mr.Obama", referencePhotoFilePath, photoToBeVerifiedFilePath);
        request.setUseStoredPhotoForReference(useStoredReferencePhoto);
        logger.info("Initiating verification request with -> " + request);
        return getWebClientWithCustomHeadersForVerification().post()
                .body(BodyInserters.fromValue(request))
                .header("content-type", "application/json")
                .retrieve().bodyToMono(VerificationRequestCreatedResponse.class);
    }

    private Mono<VerificationResponse> checkIfVerificationCompleted(String id) {
        return getWebClientWithCustomHeadersForVerification().get()
                .uri("/" + id)
                .retrieve().bodyToMono(VerificationResponse.class);
    }

    private WebClient getWebClientWithCustomHeadersForVerification() {
        return getWebClientWithCustomHeaders(baseVerificationUrl, "");
    }

    private WebClient getWebClientWithCustomHeaders() {
        return getWebClientWithCustomHeaders(baseAPIUrl, "");
    }

    private WebClient getWebClientWithCustomHeaders(String baseUrl, String alongWith) {
        if (StringUtils.isEmpty(alongWith)) {
            alongWith = "/";
        }
        return WebClient.builder()
                .baseUrl(baseUrl.concat(alongWith))
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
                        if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
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