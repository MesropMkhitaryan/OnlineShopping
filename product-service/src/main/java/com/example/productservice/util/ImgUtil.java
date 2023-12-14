package com.example.productservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class ImgUtil {

    @Value("${onlineShop.imageStorage}")
    private String imageStorage;

    public Path getFileStorage() {
        return Paths.get(imageStorage).toAbsolutePath();
    }

    public String saveImageFile(MultipartFile uploadedImageFile) {
        if (!uploadedImageFile.isEmpty()) {
            String picture = System.currentTimeMillis() + "_" + uploadedImageFile.getOriginalFilename();
            File newFile = new File(getFileStorage() +"/" + picture);
            try {
                uploadedImageFile.transferTo(newFile);
            } catch (IOException e) {
                log.error(Arrays.toString(e.getStackTrace()));
            }
            return picture;
        }

        return null;
    }

    public byte[] getImageFully(String fileName) {
        return getImage(File.separator + fileName);
    }

    private byte[] getImage(String fileName) {
        try {
            File file = new File(imageStorage);
            String absolutePath = file.getAbsolutePath();
            InputStream inputStream = new FileInputStream(absolutePath + File.separator + fileName);
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

}
