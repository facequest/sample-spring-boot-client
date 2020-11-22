package com.facequest.faceverification.demo.config;

public class RestURIConstants {
    public static final String GET_FOLDERS_PATH = "/folders";
    public static final String GET_UPLOADURL_PATH = "/uploadurl";
    public static final String GET_PHOTOS_FROM_GIVENFOLDER = "/reference-faces/folder/";
    public static final String CREATE_FOLDERS = "/folders/";
    public static final String CREATE_REFERENCE_PHOTO = "/reference-faces/{0}/photo";
    public static final String POST_PHOTO_TO_FOLDER = "reference-faces/reference_photo_for_verification/folder/{0}";

    public static final String REFERENCE_PHOTO_NAME = "reference_photo.jpg";
    public static final String PHOTO_TO_BE_VERIFIED_NAME = "photo_to_be_verified.jpg";
}
