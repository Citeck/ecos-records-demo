package ru.citeck.ecos.records.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.citeck.ecos.commons.json.Json;
import ru.citeck.ecos.records3.record.request.msg.MsgLevel;
import ru.citeck.ecos.records3.record.request.msg.ReqMsg;
import ru.citeck.ecos.records3.rest.v1.RequestResp;
import ru.citeck.ecos.records3.rest.v1.RestHandlerV1;
import ru.citeck.ecos.records3.rest.v1.delete.DeleteBody;
import ru.citeck.ecos.records3.rest.v1.delete.DeleteResp;
import ru.citeck.ecos.records3.rest.v1.mutate.MutateBody;
import ru.citeck.ecos.records3.rest.v1.mutate.MutateResp;
import ru.citeck.ecos.records3.rest.v1.query.QueryResp;
import ru.citeck.ecos.records3.rest.v2.query.QueryBodyV2;

import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordsApiController {

    private RestHandlerV1 restHandler;

    @PostMapping(path = "/query")
    public ResponseEntity<byte[]> postQuery(@RequestBody byte[] body) {

        QueryBodyV2 queryBody = Json.getMapper().readNotNull(body, QueryBodyV2.class);
        QueryResp result = restHandler.queryRecords(queryBody, false);
        return encodeResponse(result);
    }

    @PostMapping(path = "/mutate")
    public ResponseEntity<byte[]> postMutate(@RequestBody byte[] body) {

        MutateBody mutateBody = Json.getMapper().readNotNull(body, MutateBody.class);
        MutateResp result = restHandler.mutateRecords(mutateBody, false);
        return encodeResponse(result);
    }

    @PostMapping(path = "/delete")
    public ResponseEntity<byte[]> postDelete(@RequestBody byte[] body) {

        DeleteBody deleteBody = Json.getMapper().readNotNull(body, DeleteBody.class);
        DeleteResp result = restHandler.deleteRecords(deleteBody, false);
        return encodeResponse(result);
    }

    private ResponseEntity<byte[]> encodeResponse(RequestResp response) {
        HttpStatus status = HttpStatus.OK;
        List<ReqMsg> messages = response.getMessages();
        for (ReqMsg msg : messages) {
            if (msg.getLevel() == MsgLevel.ERROR) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        return new ResponseEntity<>(Json.getMapper().toBytesNotNull(response), headers, status);
    }

    @Autowired
    public void setRestHandler(RestHandlerV1 restHandler) {
        this.restHandler = restHandler;
    }
}

