package com.revolut.rest.endpoint.transfer;

import org.jooq.Record;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import static org.jooq.h2.generated.Tables.TRANSFERS;

public class Util {
    static JsonObject createTransferJsonFromRecord(Record record) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add(TRANSFERS.ID.getName(), record.getValue(TRANSFERS.ID))
                .add(TRANSFERS.FROM_ACCOUNT.getName(), record.getValue(TRANSFERS.FROM_ACCOUNT))
                .add(TRANSFERS.TO_ACCOUNT.getName(), record.getValue(TRANSFERS.TO_ACCOUNT))
                .add(TRANSFERS.AMOUNT.getName(), record.getValue(TRANSFERS.AMOUNT))
                .add(TRANSFERS.AT.getName(), record.getValue(TRANSFERS.AT).toString());

        if (record.getValue(TRANSFERS.COMMENT) != null) {
            builder.add(TRANSFERS.COMMENT.getName(), record.getValue(TRANSFERS.COMMENT));
        } else {
            builder.addNull(TRANSFERS.COMMENT.getName());
        }

        return builder.build();
    }
}
