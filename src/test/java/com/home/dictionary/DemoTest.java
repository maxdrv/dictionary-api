package com.home.dictionary;

import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.home.dictionary.openapi.model.CreateDemoDto;
import com.home.dictionary.openapi.model.DemoDto;
import com.home.dictionary.openapi.model.DemoTypeDto;
import com.home.dictionary.openapi.model.UpdateDemoDto;
import com.home.dictionary.util.WithDataBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
public class DemoTest extends WithDataBase {

    @ExpectedDatabase(
            value = "/repository/demo/after/demo_created.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
    )
    @Test
    void post() {
        var request = new CreateDemoDto();
        request.setName("Test");
        request.setType(DemoTypeDto.ONE);

        caller.create(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(
                        """
                                {
                                   "name": "Test",
                                   "type": "ONE"
                                 }
                                """,
                        false
                ));
    }

    @ExpectedDatabase(
            value = "/repository/demo/after/demo_updated.xml",
            assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED
    )
    @Test
    void put() {
        var createRequest = new CreateDemoDto();
        createRequest.setName("Test");
        createRequest.setType(DemoTypeDto.ONE);

        var createdId = caller.create(createRequest)
                .andExpect(status().isCreated())
                .andReturnAs(DemoDto.class)
                .getId();


        var updateRequest = new UpdateDemoDto();
        updateRequest.setName("Test2");
        updateRequest.setType(DemoTypeDto.TWO);
        caller.update(createdId, updateRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                   "name": "Test2",
                                   "type": "TWO"
                                 }
                                """,
                        false
                ));
    }

    @Test
    void getById() {
        var createRequest = new CreateDemoDto();
        createRequest.setName("getByIdTest");
        createRequest.setType(DemoTypeDto.ONE);

        var createdId = caller.create(createRequest)
                .andExpect(status().isCreated())
                .andReturnAs(DemoDto.class)
                .getId();

        caller.getById(createdId)
                .andExpect(status().isOk())
                .andExpect(content().json(
                        """
                                {
                                   "name": "getByIdTest",
                                   "type": "ONE"
                                 }
                                """,
                        false
                ));
    }

    @Test
    void deleteById() {
        var createRequest = new CreateDemoDto();
        createRequest.setName("deleteByIdTest");
        createRequest.setType(DemoTypeDto.ONE);

        var createdId = caller.create(createRequest)
                .andExpect(status().isCreated())
                .andReturnAs(DemoDto.class)
                .getId();

        caller.delete(createdId).andExpect(status().isOk());

        caller.getById(createdId)
                .andExpect(status().isNotFound());
    }

    @Test
    void getPage() {
        var createRequest = new CreateDemoDto();
        createRequest.setName("getPageTest");
        createRequest.setType(DemoTypeDto.ONE);

        caller.create(createRequest).andExpect(status().isCreated());

        caller.page("?&page=0&size=100")
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "size": 100,
                          "number": 0,
                          "totalElements": 1,
                          "totalPages": 1,
                          "content": [
                              {
                                  "name": "getPageTest",
                                  "type": "ONE"
                              }
                          ]
                        }""", false));
    }

}
