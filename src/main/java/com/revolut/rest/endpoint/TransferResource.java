package com.revolut.rest.endpoint;

import com.revolut.db.DbOperation;
import com.revolut.pojo.Message;
import com.revolut.pojo.Transfer;
import lombok.extern.java.Log;
import org.jooq.Record;
import org.jooq.h2.generated.tables.records.AccountsRecord;
import org.jooq.h2.generated.tables.records.TransfersRecord;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.revolut.pojo.ResponseMessage.BAD_REQUEST;
import static com.revolut.pojo.ResponseMessage.NO_RECORD;
import static org.jooq.h2.generated.Tables.ACCOUNTS;
import static org.jooq.h2.generated.Tables.TRANSFERS;

@RequestScoped
@Path("/transfers")
@Log
public class TransferResource {

    @Inject
    private DbOperation dbOperation;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransfers() {
        return Response.ok(
                dbOperation.executeAndReturn(
                        context -> context.select()
                                .from(TRANSFERS)
                                .fetch()
                                .map(
                                        record -> Json.createObjectBuilder()
                                                .add(TRANSFERS.ID.getName(), record.getValue(TRANSFERS.ID))
                                                .add(TRANSFERS.FROM_ACCOUNT.getName(), record.getValue(TRANSFERS.FROM_ACCOUNT))
                                                .add(TRANSFERS.TO_ACCOUNT.getName(), record.getValue(TRANSFERS.TO_ACCOUNT))
                                                .add(TRANSFERS.AMOUNT.getName(), record.getValue(TRANSFERS.AMOUNT))
                                                .add(TRANSFERS.AT.getName(), record.getValue(TRANSFERS.AT).toString())
                                                .add(TRANSFERS.COMMENT.getName(), record.getValue(TRANSFERS.COMMENT))
                                                .build()
                                )
                )
        ).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{transferId}")
    public Response getTransfer(@PathParam("transferId") long transferId) {
        Record record = dbOperation.executeAndReturn(
                context -> context.select()
                        .from(TRANSFERS)
                        .where(TRANSFERS.ID.equal(transferId))
                        .fetchAny()
        );
        log.info("record " + record);

        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("cause", NO_RECORD)
                    .entity(new Message(NO_RECORD, null))
                    .build();
        }

        return Response.ok(
                Json.createObjectBuilder()
                        .add(TRANSFERS.ID.getName(), record.getValue(TRANSFERS.ID))
                        .add(TRANSFERS.FROM_ACCOUNT.getName(), record.getValue(TRANSFERS.FROM_ACCOUNT))
                        .add(TRANSFERS.TO_ACCOUNT.getName(), record.getValue(TRANSFERS.TO_ACCOUNT))
                        .add(TRANSFERS.AMOUNT.getName(), record.getValue(TRANSFERS.AMOUNT))
                        .add(TRANSFERS.AT.getName(), record.getValue(TRANSFERS.AT).toString())
                        .add(TRANSFERS.COMMENT.getName(), record.getValue(TRANSFERS.COMMENT))
                        .build()
        ).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransfer(Transfer transfer) {

        if (transfer.getAmount() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("amount not valid !!", null))
                    .build();
        }

        if (transfer.getFromAccount() == transfer.getToAccount()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("from and to accounts cannot be same !!", null))
                    .build();
        }

        boolean fromAccountExists = dbOperation.executeAndReturn(
                context -> context.fetchExists(
                        context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getFromAccount()))
                )
        );

        if (!fromAccountExists) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("From Account does not exist !!", null))
                    .build();
        }

        boolean toAccountExists = dbOperation.executeAndReturn(
                context -> context.fetchExists(
                        context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getToAccount()))
                )
        );

        if (!toAccountExists) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("To Account does not exist !!", null))
                    .build();
        }

        log.info("transfer.getComment() " + transfer.getComment());

        return dbOperation.executeAndReturn(
                context -> {
                    // running in a transaction
                    return context.transactionResult(configuration -> {

                        AccountsRecord fromAccountsRecord = context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getFromAccount())).fetchOne();
                        AccountsRecord toAccountsRecord = context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getToAccount())).fetchOne();

                        if (fromAccountsRecord.getBalance().doubleValue() < transfer.getAmount()) {
                            return Response.status(Response.Status.BAD_REQUEST)
                                    .header("cause", BAD_REQUEST)
                                    .entity(new Message("Insufficient Balance !!", null))
                                    .build();
                        }

                        // debiting from account
                        context.update(ACCOUNTS).set(ACCOUNTS.BALANCE, new BigDecimal(fromAccountsRecord.getBalance().doubleValue() - transfer.getAmount()))
                                .where(ACCOUNTS.ID.eq(fromAccountsRecord.getId())).execute();
                        // crediting to account
                        context.update(ACCOUNTS).set(ACCOUNTS.BALANCE, new BigDecimal(toAccountsRecord.getBalance().doubleValue() + transfer.getAmount()))
                                .where(ACCOUNTS.ID.eq(toAccountsRecord.getId())).execute();

                        // creating a transfer
                        TransfersRecord transferRecord = new TransfersRecord();
                        transferRecord.setFromAccount(transfer.getFromAccount());
                        transferRecord.setToAccount(transfer.getToAccount());
                        transferRecord.setAmount(new BigDecimal(transfer.getAmount()));
                        transferRecord.setAt(Timestamp.valueOf(LocalDateTime.now()));
                        transferRecord.setComment(transfer.getComment());
                        transferRecord = context.insertInto(TRANSFERS).set(transferRecord).returning().fetchOne();

                        log.info("transferRecord.getValue(TRANSFERS.COMMENT) " + transferRecord.getValue(TRANSFERS.COMMENT));

                        JsonObjectBuilder builder = Json.createObjectBuilder()
                                .add(TRANSFERS.ID.getName(), transferRecord.getValue(TRANSFERS.ID))
                                .add(TRANSFERS.FROM_ACCOUNT.getName(), transferRecord.getValue(TRANSFERS.FROM_ACCOUNT))
                                .add(TRANSFERS.TO_ACCOUNT.getName(), transferRecord.getValue(TRANSFERS.TO_ACCOUNT))
                                .add(TRANSFERS.AMOUNT.getName(), transferRecord.getValue(TRANSFERS.AMOUNT))
                                .add(TRANSFERS.AT.getName(), transferRecord.getValue(TRANSFERS.AT).toString());
                        if (transferRecord.getValue(TRANSFERS.COMMENT) != null) {
                            builder.add(TRANSFERS.COMMENT.getName(), transferRecord.getValue(TRANSFERS.COMMENT));
                        } else {
                            builder.addNull(TRANSFERS.COMMENT.getName());
                        }

                        return Response.ok(
                                builder.build()
                        ).build();
                    });
                }
        );
    }
}
