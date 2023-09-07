package ru.citeck.ecos.records.demo.recordsdao;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.citeck.ecos.commons.json.Json;
import ru.citeck.ecos.records2.predicate.PredicateService;
import ru.citeck.ecos.records2.predicate.model.Predicate;
import ru.citeck.ecos.records3.record.atts.dto.LocalRecordAtts;
import ru.citeck.ecos.records3.record.dao.AbstractRecordsDao;
import ru.citeck.ecos.records3.record.dao.atts.RecordAttsDao;
import ru.citeck.ecos.records3.record.dao.delete.DelStatus;
import ru.citeck.ecos.records3.record.dao.delete.RecordDeleteDao;
import ru.citeck.ecos.records3.record.dao.mutate.RecordMutateDao;
import ru.citeck.ecos.records3.record.dao.query.RecordsQueryDao;
import ru.citeck.ecos.records3.record.dao.query.dto.query.RecordsQuery;
import ru.citeck.ecos.records3.record.dao.query.dto.res.RecsQueryRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Simple demo with in-memory storage
 */
@Component
public class TestRecordsDao extends AbstractRecordsDao implements RecordAttsDao, RecordsQueryDao,
    RecordMutateDao,
    RecordDeleteDao {

    public static final String ID = "test-dao";

    private List<TestDto> storage = new ArrayList<>();

    @PostConstruct
    public void init() {
        storage.add(new TestDto("1", "abc", 1));
        storage.add(new TestDto("2", "def", 2));
    }

    @NotNull
    @Override
    public DelStatus delete(@NotNull String localId) {

        storage = storage.stream()
            .filter(it -> !it.getId().equals(localId))
            .collect(Collectors.toList());

        return DelStatus.OK;
    }

    @NotNull
    @Override
    public String mutate(@NotNull LocalRecordAtts recordAtts) {

        String id = recordAtts.getId();
        TestDto dtoToUpdate;
        if (id.isBlank()) {
            String idFromAtts = recordAtts.getAtt("id", "");
            dtoToUpdate = findDtoById(idFromAtts);
            if (dtoToUpdate == null) {
                if (idFromAtts.isBlank()) {
                    idFromAtts = UUID.randomUUID().toString();
                }
                dtoToUpdate = new TestDto();
                dtoToUpdate.setId(idFromAtts);
                storage.add(dtoToUpdate);
            }
        } else {
            dtoToUpdate = findDtoById(id);
            if (dtoToUpdate == null) {
                throw new RuntimeException("Record with id '" + id + "' doesn't found");
            }
        }

        Json.getMapper().applyData(dtoToUpdate, recordAtts.getAtts());

        return dtoToUpdate.id;
    }

    @Nullable
    private TestDto findDtoById(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return storage.stream()
            .filter(it -> Objects.equals(it.id, id))
            .findFirst()
            .orElse(null);
    }

    @Nullable
    @Override
    public RecsQueryRes<?> queryRecords(@NotNull RecordsQuery recordsQuery) {

        if (!PredicateService.LANGUAGE_PREDICATE.equals(recordsQuery.getLanguage())) {
            return null;
        }

        Predicate predicate = recordsQuery.getQuery(Predicate.class);
        List<TestDto> result = predicateService.filter(storage, predicate);

        return new RecsQueryRes<>(result);
    }

    @Nullable
    @Override
    public Object getRecordAtts(@NotNull String localId) {
        return storage.stream()
            .filter(it -> it.id.equals(localId))
            .findFirst()
            .orElse(null);
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestDto {

        private String id;
        private String field0;
        private int intField;
    }
}
