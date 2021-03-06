/*
 * This file is generated by jOOQ.
 */
package org.jooq.h2.generated;


import javax.annotation.processing.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.h2.generated.tables.Accounts;
import org.jooq.h2.generated.tables.Transfers;
import org.jooq.h2.generated.tables.records.AccountsRecord;
import org.jooq.h2.generated.tables.records.TransfersRecord;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>PUBLIC</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AccountsRecord, Long> IDENTITY_ACCOUNTS = Identities0.IDENTITY_ACCOUNTS;
    public static final Identity<TransfersRecord, Long> IDENTITY_TRANSFERS = Identities0.IDENTITY_TRANSFERS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AccountsRecord> CONSTRAINT_A = UniqueKeys0.CONSTRAINT_A;
    public static final UniqueKey<TransfersRecord> CONSTRAINT_E = UniqueKeys0.CONSTRAINT_E;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AccountsRecord, Long> IDENTITY_ACCOUNTS = Internal.createIdentity(Accounts.ACCOUNTS, Accounts.ACCOUNTS.ID);
        public static Identity<TransfersRecord, Long> IDENTITY_TRANSFERS = Internal.createIdentity(Transfers.TRANSFERS, Transfers.TRANSFERS.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AccountsRecord> CONSTRAINT_A = Internal.createUniqueKey(Accounts.ACCOUNTS, "CONSTRAINT_A", Accounts.ACCOUNTS.ID);
        public static final UniqueKey<TransfersRecord> CONSTRAINT_E = Internal.createUniqueKey(Transfers.TRANSFERS, "CONSTRAINT_E", Transfers.TRANSFERS.ID);
    }
}
