package com.example.health.repository;

import com.example.health.model.Thumbnail;
import com.example.health.model.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ThumbnailRepository extends CrudRepository<Thumbnail, Long> {

    Thumbnail findThumbnailByVideoAndSize(@Param("video") Video video, @Param("size") String size);

}
