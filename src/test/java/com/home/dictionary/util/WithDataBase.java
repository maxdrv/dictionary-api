package com.home.dictionary.util;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.dataset.ReplacementDataSetLoader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.ApplicationEventsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureMockMvc
@ExtendWith(TruncateExtension.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        ResetMocksTestExecutionListener.class,
        ApplicationEventsTestExecutionListener.class,
})
@DbUnitConfiguration(dataSetLoader = ReplacementDataSetLoader.class, databaseConnection = "dataSource")
@SpringBootTest
@ComponentScan("com.home.calories")
public class WithDataBase {

    @Autowired
    protected MockMvc mockMvc;

    @Qualifier("apiCaller")
    @Autowired
    protected ApiCaller caller;

    @Qualifier("securedApiCaller")
    @Autowired
    protected ApiCaller securedApiCaller;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * for debug on embedded postgres
     */
    @SneakyThrows
    public String getConnectionString() {
        return jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
    }

}
