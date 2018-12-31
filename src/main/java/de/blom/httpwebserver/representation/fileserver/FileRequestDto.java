package de.blom.httpwebserver.representation.fileserver;

import lombok.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;


@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@ToString
public class FileRequestDto extends CacheableData {
    Boolean found;
    String contentType;
    byte[] fileContent;
    int fileLength;

    @Builder
    public FileRequestDto(Date lastModified, String eTag, Boolean found, String contentType, byte[] fileContent, int fileLength) {
        super(lastModified, eTag);
        this.found = found;
        this.contentType = contentType;
        this.fileContent = fileContent;
        this.fileLength = fileLength;
    }

    public FileRequestDto(Boolean found) {
        this.found = found;
    }

    public static String generateETag(FileRequestDto dto) {
        return DigestUtils.md5Hex(Arrays.toString(dto.fileContent));
    }
}
