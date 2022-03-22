package io.quarkus.ts.http.restclient.reactive.files;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

public class MultipartFile {
    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public File file;

    public MultipartFile(File file) {
        this.file = file;
    }

    public MultipartFile() {
    }
}
