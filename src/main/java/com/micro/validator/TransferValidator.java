package com.micro.validator;

import com.micro.db.DbOperation;
import com.micro.pojo.Message;
import com.micro.pojo.Transfer;
import lombok.extern.java.Log;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static com.micro.pojo.ResponseMessage.BAD_REQUEST;
import static org.jooq.h2.generated.Tables.ACCOUNTS;

@RequestScoped
@Log
public class TransferValidator {

    @Inject
    private DbOperation dbOperation;

    public Optional<Response> validateTransfer(Transfer transfer) {
        if (transfer.getAmount() <= 0) {
            return Optional.of(Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("amount not valid !!", null))
                    .build());
        }

        if (transfer.getFromAccount() == transfer.getToAccount()) {
            return Optional.of(Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("from and to accounts cannot be same !!", null))
                    .build());
        }

        boolean fromAccountExists = dbOperation.executeAndReturn(
                context -> context.fetchExists(
                        context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getFromAccount()))
                )
        );

        if (!fromAccountExists) {
            return Optional.of(Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("From Account does not exist !!", null))
                    .build());
        }

        boolean toAccountExists = dbOperation.executeAndReturn(
                context -> context.fetchExists(
                        context.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(transfer.getToAccount()))
                )
        );

        if (!toAccountExists) {
            return Optional.of(Response.status(Response.Status.BAD_REQUEST)
                    .header("cause", BAD_REQUEST)
                    .entity(new Message("To Account does not exist !!", null))
                    .build());
        }

        return Optional.empty();
    }
}
