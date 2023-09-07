package ru.citeck.ecos.records.demo.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.citeck.ecos.records3.RecordsProperties;
import ru.citeck.ecos.records3.RecordsService;
import ru.citeck.ecos.records3.RecordsServiceFactory;
import ru.citeck.ecos.records3.rest.v1.RestHandlerV1;
import ru.citeck.ecos.webapp.api.properties.EcosWebAppProps;

import java.util.UUID;

@Configuration
public class RecordsConfiguration extends RecordsServiceFactory {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public RecordsService recordsServiceV1() {
        return super.getRecordsServiceV1();
    }

    @Bean
    public RestHandlerV1 restHandlerV1() {
        return super.getRestHandlerAdapter().getV1Handler();
    }

    @NotNull
    @Override
    protected RecordsProperties createProperties() {
        return super.createProperties();
    }

    @NotNull
    @Override
    protected EcosWebAppProps evalWebAppProps() {
        return EcosWebAppProps.Companion.create(appName, UUID.randomUUID().toString());
    }
}
