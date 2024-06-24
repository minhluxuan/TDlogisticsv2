package project.tdlogistics.schedule.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.io.IOException;
import java.net.MalformedURLException;

@Service
public class FileUploadService {

    private final Path root = Paths.get("uploads");

    public FileUploadService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public void save(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public boolean directoryExists(Path directory) {
        return Files.exists(directory);
    }

    public void createDirectoryIfNotExists(Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create directory. Error: " + e.getMessage());
        }
    }

    public void saveFileToDirectory(MultipartFile file, Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Files.copy(file.getInputStream(), directory.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public void deleteDirectoryIfExists(Path directory) {
        try {
            if (Files.exists(directory)) {
                Files.walk(directory)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete directory!", e);
                            }
                        });
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting directory", e);
        }
    }

    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}