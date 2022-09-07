package com.home.dictionary.mapper;

import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.lesson.LessonItem;
import com.home.dictionary.model.lesson.LessonItemStatus;
import com.home.dictionary.model.lesson.LessonStatus;
import com.home.dictionary.openapi.model.LessonDto;
import com.home.dictionary.openapi.model.LessonItemDto;
import com.home.dictionary.openapi.model.LessonItemStatusDto;
import com.home.dictionary.openapi.model.LessonStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface LessonMapper {

    LessonDto map(Lesson lesson);

    LessonItemDto map(LessonItem lessonItem);

    LessonStatusDto map(LessonStatus status);

    LessonStatus map(LessonStatusDto status);

    LessonItemStatusDto map(LessonItemStatus status);

    LessonItemStatus map(LessonItemStatusDto status);

}
