package com.example.productservice.util;

import com.example.productservice.customException.FileDontExistException;
import com.example.productservice.customException.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Component
@Slf4j
public class IOUtil {

    @Value("${onlineShop.imageStorage}")
    private String imageStorage;

    public Path getFileStorage() {
        return Paths.get(imageStorage).toAbsolutePath();
    }

    public String saveImageFile(MultipartFile uploadedImageFile) {
        if (!uploadedImageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + uploadedImageFile.getOriginalFilename();
            File newFile = new File(getFileStorage() +"/" + fileName);
            try {
                uploadedImageFile.transferTo(newFile);
            } catch (IOException e) {
                throw new FileStorageException("Failed to save the image file");
            }
            return fileName;
        }
         throw new FileDontExistException("File don't exist");
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
