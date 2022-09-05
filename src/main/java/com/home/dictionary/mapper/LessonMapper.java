package com.home.dictionary.mapper;

import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.openapi.model.LessonDetailedDto;
import com.home.dictionary.openapi.model.LessonGridDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                DateTimeMapper.class,
                PhraseMapper.class
        }
)
public interface LessonMapper {

    LessonDetailedDto toDetailedDto(Lesson entity);

    LessonGridDto toGridDto(Lesson entity);

}
