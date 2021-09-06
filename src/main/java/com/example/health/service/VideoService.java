package com.example.health.service;

import com.example.health.co.CategoryResponseCO;
import com.example.health.co.VideoListCO;
import com.example.health.co.VideoResponseCO;
import com.example.health.exception.CategoryNotFoundException;
import com.example.health.exception.FileFormatNotSupportedException;
import com.example.health.exception.FileSizeExceedException;
import com.example.health.exception.NoVideosFoundException;
import com.example.health.model.Category;
import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import com.example.health.repository.CategoryRepository;
import com.example.health.repository.ThumbnailRepository;
import com.example.health.repository.VideoRepository;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.io.IOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final CategoryRepository categoryRepository;
    private final ThumbnailRepository thumbnailRepository;

    private final String fileUploadDir = "/videos/";

    @Value("${maxFileSizeAllowed}")
    private Long fileSizeAllowedInMB;

    @Value("#{'${allowed-video-extensions}'.split(',')}")
    private List<String> allowedFileExtensions;


    public VideoService(VideoRepository videoRepository, CategoryRepository categoryRepository, ThumbnailRepository thumbnailRepository) {
        this.videoRepository = videoRepository;
        this.categoryRepository = categoryRepository;
        this.thumbnailRepository = thumbnailRepository;
    }

    public VideoResponseCO uploadNewVideoAndThumbnails(String title, MultipartFile multipartFile, Long categoryId) throws Exception {
        String fileNameWithoutExtension = JCodecUtil.removeExtension(multipartFile.getOriginalFilename());
        String fileExtension = multipartFile.getOriginalFilename().replace(fileNameWithoutExtension+".", "");
        if (!allowedFileExtensions.contains(fileExtension)) {
            // throw exception if the file extension is not allowed or supported
            throw new FileFormatNotSupportedException(String.format("The %s file format is not supported", fileExtension));
        }
        long fileSize = multipartFile.getSize() / 1000;
        if (fileSize > (fileSizeAllowedInMB * 1000)) {
            throw new FileSizeExceedException("Maximum size allowed in MB is: " + fileSizeAllowedInMB);
        }
        String initialPath = System.getProperty("user.dir");
        Path filePath  = Paths.get(initialPath + "/videos/");
        String fileNameToUse = String.format("%s_%s", title, multipartFile.getOriginalFilename());
        filePath = filePath.resolve(fileNameToUse);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // check if the extension is supported
        File originalSource = filePath.toFile();
        // create video object and persist it
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (Objects.isNull(category)) {
            // Category not found, will throw exception
            throw new CategoryNotFoundException(String.format("Category not found with ID: %d", categoryId));
        }
        String relativeFilePath = String.format("%s%s", fileUploadDir, fileNameToUse);
        Video video = new Video(title, relativeFilePath, fileExtension, category);
        video = videoRepository.save(video);
        // Create thumbnail from the
        List<Thumbnail> thumbnails = captureThumbnail(title, originalSource, fileNameWithoutExtension, video);
        thumbnailRepository.saveAll(thumbnails);
        String thumbnailUrl = "/thumbnail/"+video.getId();
        List<String> thumbnailUrls = Arrays.asList(thumbnailUrl+"/64", thumbnailUrl+"/128", thumbnailUrl+"/256");
        return new VideoResponseCO(video.getTitle(), video.getVideoUrl(), thumbnailUrls, video.getId(),
                CategoryResponseCO.toCategoryResponseCO(category));
    }

    private List<Thumbnail> captureThumbnail(String title, File originalSource, String fileNameWithoutExtension, Video video) throws IOException, JCodecException {
        Picture picture = FrameGrab.getFrameAtSec(originalSource, 1.0);
        Integer[] sizeArray = new Integer[]{64, 128, 256};
        List<Thumbnail> thumbnails = new ArrayList<>();
        for (Integer size : sizeArray) {
            Picture cropped = Picture.copyPicture(picture);
            cropped.setCrop(new Rect(picture.getStartX(), picture.getStartY(), size, size));
            String initialPath = System.getProperty("user.dir");
            Path relativeFilePath = Paths.get(initialPath + fileUploadDir+"/thumbnails/");
            relativeFilePath = relativeFilePath.resolve(title +"_"+ fileNameWithoutExtension +"_"+size+".png");
            File file = relativeFilePath.toFile();
            JCodecUtil.savePictureAsPPM(cropped, file);
            Thumbnail thumbnail = new Thumbnail(String.format("%sX%s", size, size), IOUtils.readFileToByteArray(file));
            thumbnail.setVideos(video);
            thumbnails.add(thumbnail);
            file.delete();
        }
        return thumbnails;
    }

    public VideoListCO findAllVideos() {
        List<Video> videos = StreamSupport.stream(videoRepository.findAll().spliterator(), false).collect(Collectors.toList());
        List<VideoResponseCO> videoResponseCOS = videos.stream().map(VideoResponseCO::toVideoResponseCO).collect(Collectors.toList());
        if (videoResponseCOS.isEmpty()) {
            throw new NoVideosFoundException("No videos were found in the system.");
        }
        return new VideoListCO((long) videoResponseCOS.size(), videoResponseCOS);
    }
}
