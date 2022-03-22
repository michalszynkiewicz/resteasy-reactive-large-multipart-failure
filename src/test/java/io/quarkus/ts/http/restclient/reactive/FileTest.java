package io.quarkus.ts.http.restclient.reactive;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

@QuarkusTest
public class FileTest {

    private static final Path DOWNLOADED = Paths.get("target", "FileIT", "downloaded.txt").toAbsolutePath();
    private static final Path UPLOADED = Paths.get("target", "FileIT", "uploaded.txt").toAbsolutePath();
    private static final String BIGGER_THAN_TWO_GIGABYTES = "2MiB";


    @Test
    public void wrapperTest() {
        Response original = given().get("/files/hash");
        Response wrapped = given().get("/client-wrapper/hash");
        assertEquals(HttpStatus.SC_OK, original.statusCode());
        assertEquals(HttpStatus.SC_OK, wrapped.statusCode());
        assertNotNull(original.body().asString());
        assertEquals(original.body().asString(), wrapped.body().asString());
    }

    @Test
    public void downloadManually() throws IOException, InterruptedException {
        Response hashSum = given().get("/files/hash");
        assertEquals(HttpStatus.SC_OK, hashSum.statusCode());
        String serverSum = hashSum.body().asString();

        Response download = given().get("/files/download");
        assertEquals(HttpStatus.SC_OK, download.statusCode());
        InputStream stream = download.body().asInputStream();
        Files.copy(stream, DOWNLOADED);
        String clientSum = BashUtils.getSum(DOWNLOADED.toString());
        assertEquals(serverSum, clientSum);
    }

    @Test
    public void download() {
        Response hashSum = given().get("/files/hash");
        assertEquals(HttpStatus.SC_OK, hashSum.statusCode());
        String serverSum = hashSum.body().asString();

        Response download = given().get("/client-wrapper/download"); //FIXME file is not downloaded completely, should create an issue
        assertEquals(HttpStatus.SC_OK, download.statusCode());
        String clientSum = download.body().asString();

        assertEquals(serverSum, clientSum);
    }

    @Test
    public void downloadMultipart() {
        Response hashSum = given().get("/files/hash");
        assertEquals(HttpStatus.SC_OK, hashSum.statusCode());
        String serverSum = hashSum.body().asString();

        Response download = given().get("/client-wrapper/download-multipart"); //FIXME file is not downloaded completely, should create an issue
        assertEquals(HttpStatus.SC_OK, download.statusCode());
        String clientSum = download.body().asString();

        assertEquals(serverSum, clientSum);
    }

    @Test
    //FIXME create issue
    public void uploadRest() throws IOException, InterruptedException {
        BashUtils.createFile(UPLOADED.toString(), BIGGER_THAN_TWO_GIGABYTES);
        String hashsum = BashUtils.getSum(UPLOADED.toString());
        try (InputStream stream = new FileInputStream(UPLOADED.toFile())) {
            Response response = given()
                    .body(stream)
                    .post("/files/upload/");
            assertEquals(HttpStatus.SC_OK, response.statusCode());
            assertEquals(hashsum, response.body().asString());
        }
    }

    @Test
    public void uploadPath() {
        Response hashSum = given().get("/client-wrapper/client-hash");
        assertEquals(HttpStatus.SC_OK, hashSum.statusCode());
        String before = hashSum.body().asString();

        Response upload = given().post("/client-wrapper/upload-path");
        assertEquals(HttpStatus.SC_OK, upload.statusCode());
        String after = upload.body().asString();

        assertEquals(before, after);
    }

    @Test
    public void uploadFile() {
        Response hashSum = given().get("/client-wrapper/client-hash");
        assertEquals(HttpStatus.SC_OK, hashSum.statusCode());
        String before = hashSum.body().asString();

        Response upload = given().post("/client-wrapper/upload-file");
        assertEquals(HttpStatus.SC_OK, upload.statusCode());
        String after = upload.body().asString();

        assertEquals(before, after);
    }
}
