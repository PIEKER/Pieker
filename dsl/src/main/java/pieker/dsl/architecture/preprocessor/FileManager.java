package pieker.dsl.architecture.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pieker.dsl.architecture.exception.ValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public class FileManager {

    public static final String PREFIX = "fM-hash::";
    public static final String SUFFIX = "::hsah-Mf";

    private final String strDirectory;
    private final Path dir;

    private final Map<String, String> hashToContentMap = new HashMap<>();
    private final Set<String> loadedFiles = new HashSet<>();

    ObjectMapper mapper = new ObjectMapper();

    public FileManager(String directory){
        this.strDirectory = directory;
        this.dir = this.strDirectory.equals(".") ? Paths.get("./") : Paths.get(this.strDirectory);
        log.debug("set file-directory to '{}'", this.dir.toUri().getPath());
    }

    public String loadFileAsString(String substring, int index) {
        try {
            Path filePath = Paths.get(this.dir.toString() + substring);

            if (this.loadedFiles.contains(substring)){
                return Integer.toString(substring.concat(Integer.toString(index)).hashCode());
            }

            String fileContent = new String(Files.readAllBytes(filePath));
            String contentHashCode = Integer.toString(substring.concat(Integer.toString(0)).hashCode());

            if (substring.endsWith(".json")) {
                contentHashCode = loadFileFromJson(substring, index, fileContent, contentHashCode);

            } else if (substring.endsWith(".sql")){
                contentHashCode = loadSqlFromFile(substring, index, fileContent, contentHashCode);
            } else {
                this.hashToContentMap.putIfAbsent(contentHashCode, fileContent);
            }
            this.loadedFiles.add(substring);
            return contentHashCode;

        } catch ( IOException e) {
            throw new ValidationException("unable to load file from path '" + substring + "'. Error: " + e.getMessage());
        }
    }

    public String getHashId(String fileHash){
        Pattern pattern = Pattern.compile(PREFIX + "(-)?\\d*" + SUFFIX);
        Matcher matcher = pattern.matcher(fileHash);
        if (matcher.find()) {
            String s = matcher.group();
            return s.substring(PREFIX.length(), s.length() - SUFFIX.length());
        } else {
            return fileHash;
        }
    }

    public String getDataFromFileHash(String fileHash){
        return this.hashToContentMap.get(this.getHashId(fileHash));
    }

    private String loadSqlFromFile(String substring, int index, String fileContent, String contentHashCode) {
        String[] statements = fileContent.split(";");

        for (int i = 0; i < statements.length; i++) {
            String hashCode = Integer.toString(substring.concat(Integer.toString(i)).hashCode());
            if (statements[i].isBlank()){
                continue;
            }
            this.hashToContentMap.putIfAbsent(hashCode, statements[i].trim());
            if (i == index){
                contentHashCode = hashCode; //cache required index
            }
        }
        return contentHashCode;
    }

    private String loadFileFromJson(String substring, int index, String fileContent, String contentHashCode) throws JsonProcessingException {
        JsonNode arrayNode = mapper.readTree(fileContent);

        if (arrayNode.isArray()) {
            for (int i = 0; i < arrayNode.size(); i++) {
                String hashCode = Integer.toString(substring.concat(Integer.toString(i)).hashCode());
                this.hashToContentMap.putIfAbsent(hashCode, arrayNode.get(i).toString());

                if (i == index){
                    contentHashCode = hashCode; //cache required index
                }
            }
        } else {
            this.hashToContentMap.putIfAbsent(contentHashCode, arrayNode.toString());
        }
        return contentHashCode;
    }
}
