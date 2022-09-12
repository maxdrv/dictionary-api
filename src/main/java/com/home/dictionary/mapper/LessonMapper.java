package com.home.dictionary.mapper;

import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.lesson.LessonItem;
import com.home.dictionary.model.lesson.LessonItemStatus;
import com.home.dictionary.model.lesson.LessonStatus;
import com.home.dictionary.openapi.model.LessonDto;
import com.home.dictionary.openapi.model.LessonItemDto;
import com.home.dictionary.openapi.model.LessonItemStatusDto;
import com.home.dictionary.openapi.model.LessonStatusDto;
import one.util.streamex.StreamEx;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface LessonMapper {

    LessonDto map(Lesson lesson, boolean active);

    LessonItemDto map(LessonItem lessonItem);

    LessonStatusDto map(LessonStatus status);

    LessonStatus map(LessonStatusDto status);

    LessonItemStatusDto map(LessonItemStatus status);

    LessonItemStatus map(LessonItemStatusDto status);

    default List<LessonItemDto> mapLessonItemList(List<LessonItem> lessonItemList) {
        if (lessonItemList == null) {
            return null;
        }
        return StreamEx.of(lessonItemList)
                .map(this::map)
                .sortedBy(LessonItemDto::getItemOrder)
                .toList();
    }

}
