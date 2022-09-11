package com.home.dictionary;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.home.dictionary.util.WithDataBase;
import org.junit.jupiter.api.Test;

public class CurrentLessonTest extends WithDataBase {

    @Test
    @DatabaseSetup("/repository/plan/plan.xml")
    void createLesson() {

    }

}
