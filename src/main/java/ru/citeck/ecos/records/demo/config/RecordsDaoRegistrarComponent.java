package ru.citeck.ecos.records.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.citeck.ecos.records3.record.dao.RecordsDao;
import ru.citeck.ecos.records3.RecordsDaoRegistrar;
import ru.citeck.ecos.records3.RecordsServiceFactory;

import java.util.List;

@Component
public class RecordsDaoRegistrarComponent extends RecordsDaoRegistrar {

    public RecordsDaoRegistrarComponent(RecordsServiceFactory services) {
        super(services);
    }

    @PostConstruct
    public void init() {
        register();
    }

    @Autowired(required = false)
    public void setSources(List<? extends RecordsDao> sources) {
        super.setSources(sources);
    }
}
