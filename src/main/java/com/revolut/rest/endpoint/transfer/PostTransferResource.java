package com.revolut.rest.endpoint.transfer;

import com.revolut.db.DbOperation;
import com.revolut.pojo.Message;
import com.revolut.pojo.Transfer;
import com.revolut.validator.TransferValidator;
import lombok.extern.java.Log;
import org.jooq.h2.generated.tables.records.AccountsRecord;
import org.jooq.h2.generated.tables.records.TransfersRecord;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.revolut.pojo.ResponseMessage.BAD_REQUEST;
import static com.revolut.rest.endpoint.transfer.Util.createTransferJsonFromRecord;
import static org.jooq.h2.generated.Tables.ACCOUNTS;
import static org.jooq.h2.generated.Tables.TRANSFERS;

@RequestScoped
@Path("/transfers")
@Log
public class PostTransferResource {

    @Inject
    private DbOperation dbOperation;
    @Inject
    private TransferValidator validator;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransfer(Transfer transfer) {

        Optional<Response> response;
        if ((response = validator.validateTransfer(transfer)).isPresent()) {
            return response.get();
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

                        return Response.ok(createTransferJsonFromRecord(transferRecord)).build();
                    });
                }
        );
    }
}
